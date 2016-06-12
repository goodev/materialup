package org.goodev.material;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.assist.AssistContent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Collect;
import org.goodev.material.model.Comment;
import org.goodev.material.model.Post;
import org.goodev.material.model.Upvote;
import org.goodev.material.model.User;
import org.goodev.material.ui.Dialogs;
import org.goodev.material.util.AnimUtils;
import org.goodev.material.util.ColorUtils;
import org.goodev.material.util.FrescoUtils;
import org.goodev.material.util.HtmlUtils;
import org.goodev.material.util.ImeUtils;
import org.goodev.material.util.L;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.Pref;
import org.goodev.material.util.ShareDribbbleImageTask;
import org.goodev.material.util.UI;
import org.goodev.material.util.ViewUtils;
import org.goodev.material.util.customtabs.CustomTabActivityHelper;
import org.goodev.material.widget.AuthorTextView;
import org.goodev.material.widget.CheckableImageButton;
import org.goodev.material.widget.ElasticDragDismissFrameLayout;
import org.goodev.material.widget.FABToggle;
import org.goodev.material.widget.FabOverlapTextView;
import org.goodev.material.widget.ParallaxScrimageView;
import org.goodev.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostActivity extends AppCompatActivity {


    private static final int RC_LOGIN_LIKE = 0;
    private static final int RC_LOGIN_COMMENT = 1;
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private static final int PICK_SELECTION = 100;

    @Bind(R.id.draggable_frame)
    ElasticDragDismissFrameLayout draggableFrame;
    @Bind(R.id.back)
    ImageButton back;
    @Bind(R.id.source)
    ImageButton source;
    @Bind(R.id.shot)
    ParallaxScrimageView imageView;
    @Bind(R.id.fab_heart)
    FABToggle fab;
    private View shotSpacer;
    private View title;
    private View description;
    private LinearLayout shotActions;
    private Button voteCount;
    private Button viewCount;
    private Button share;
    private TextView playerName;
    private SimpleDraweeView playerAvatar;
    private TextView shotTimeAgo;
    private ListView commentsList;
    private DribbbleCommentsAdapter commentsAdapter;
    private View commentFooter;
    private SimpleDraweeView userAvatar;
    private EditText enterComment;
    private ImageButton postComment;

    private Post shot;
    private int fabOffset;
    private boolean performingLike;
    private boolean allowComment;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    private Pref mPref;
    private boolean mNeedUpdateConent;
    private boolean mFull;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mPref = Pref.get();
        Intent intent = getIntent();
        shot = Parcels.unwrap(intent.getParcelableExtra(Launcher.EXTRA_SHOT));
        //来自于搜索结果
        mNeedUpdateConent = intent.getBooleanExtra(Launcher.EXTRA_SEARCH, false);
        mFull = intent.getBooleanExtra(Launcher.EXTRA_FULL, false);
        setExitSharedElementCallback(fabLoginSharedElementCallback);
        if (UI.isLollipop()) {
            setupTransitionListener();
        }
        Resources res = getResources();

        ButterKnife.bind(this);
        View shotDescription = getLayoutInflater().inflate(R.layout.post_description,
                commentsList, false);
        shotSpacer = shotDescription.findViewById(R.id.shot_spacer);
        title = shotDescription.findViewById(R.id.shot_title);
        description = shotDescription.findViewById(R.id.shot_description);
        shotActions = (LinearLayout) shotDescription.findViewById(R.id.shot_actions);
        voteCount = (Button) shotDescription.findViewById(R.id.shot_like_count);
        viewCount = (Button) shotDescription.findViewById(R.id.shot_view_count);
        share = (Button) shotDescription.findViewById(R.id.shot_share_action);
        playerName = (TextView) shotDescription.findViewById(R.id.player_name);
        playerAvatar = (SimpleDraweeView) shotDescription.findViewById(R.id.player_avatar);
        shotTimeAgo = (TextView) shotDescription.findViewById(R.id.shot_time_ago);
        commentsList = (ListView) findViewById(R.id.dribbble_comments);
        commentsList.addHeaderView(shotDescription);
        setupCommenting();
        commentsList.setOnScrollListener(scrollListener);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UI.isLollipop()) {
                    expandImageAndFinish();
                } else {
                    supportFinishAfterTransition();
                }
            }
        });
        source.setImageResource(shot.getSourceIcon());
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.launchUrl(PostActivity.this, shot.redirect);
            }
        });
        fab.setOnClickListener(fabClick);
        if (!UI.isLollipop()) {
            fab.setAlpha(1f);
            fab.setVisibility(View.VISIBLE);
        }
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(getWindow()) {
            @Override
            public void onDragDismissed() {
                expandImageAndFinish();
            }
        };

        BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                if (bitmap != null && Build.VERSION.SDK_INT >= 21) {
                    processPalette(bitmap);
                }
            }
        };

        FrescoUtils.setShotHierarchy(this, imageView, shot.getBackground());
        FrescoUtils.setShotUrl(imageView, shot.getImageUrl(), shot.getTeaserUrl(), subscriber, false);
        // load the main image
//        Uri uri = Uri.parse(shot.getImageUrl());
//        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setImageRequest(imageRequest)
//                .setOldController(imageView.getController())
//                .build();
//        processImageWithPaletteApi(imageRequest, controller);


        imageView.setOnClickListener(shotClick);
        shotSpacer.setOnClickListener(shotClick);

        supportPostponeEnterTransition();
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                .OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                calculateFabPosition();
                enterAnimation(savedInstanceState != null);
                supportStartPostponedEnterTransition();
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((FabOverlapTextView) title).setText(shot.title);
        } else {
            ((TextView) title).setText(shot.title);
        }
        NumberFormat nf = NumberFormat.getInstance();
        voteCount.setText(
                res.getQuantityString(R.plurals.upvotes,
                        (int) shot.getVotes(),
                        nf.format(shot.getVotes())));
//        // TODO onClick show likes
//        voteCount.setText(String.valueOf(shot.getVotes()));
//        viewCount.setText(
//                res.getQuantityString(R.plurals.views,
//                        (int) shot.getVotes(),
//                        nf.format(shot.getVotes())));
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ShareDribbbleImageTask(PostActivity.this, shot).execute();
            }
        });
        viewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCollections();
            }
        });
        voteCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shot.getUpvoters() != null) {
                    AlertDialog dialog = Dialogs.getUpvoterDialog(PostActivity.this, shot.getUpvoters());
                }
            }
        });

        View.OnClickListener launchUser = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(shot.userUrl)) {
                    launchUser();
                }
            }
        };
        playerName.setOnClickListener(launchUser);
        playerAvatar.setOnClickListener(launchUser);
        shotTimeAgo.setOnClickListener(launchUser);
        setupUserInfo();
        setupDribbble();
    }

    private void launchUser() {
        Launcher.launchUser(this, shot.userUrl, shot.avatarUrl, null, playerAvatar);
    }

    private void pickCollections() {
        Intent intent = new Intent();
        intent.setClass(this, SelectCollectionActivity.class);
        intent.putExtra(Launcher.EXTRA_ID, shot.id);
        startActivityForResult(intent, PICK_SELECTION);
    }


    private void setupUserInfo() {
        if (!TextUtils.isEmpty(shot.userName)) {
            playerName.setText(shot.userName);
            if (!TextUtils.isEmpty(shot.avatarUrl)) {
                playerAvatar.setImageURI(Uri.parse(shot.avatarUrl));
            }

//            if (!TextUtils.isEmpty(shot.userUrl)) {
//                playerAvatar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        String url = shot.userUrl.startsWith("/") ? Api.ENDPOINT + shot.userUrl : shot.userUrl;
//                        PostActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                    }
//                });
//            }
            if (shot.createTime != null) {
                shotTimeAgo.setText(shot.createTime);
            }
            playerName.setVisibility(View.VISIBLE);
            playerAvatar.setVisibility(View.VISIBLE);
            shotTimeAgo.setVisibility(View.VISIBLE);
        } else {
            playerName.setVisibility(View.GONE);
            playerAvatar.setVisibility(View.GONE);
            shotTimeAgo.setVisibility(View.GONE);
        }
    }

    private void processImageWithPaletteApi(ImageRequest request, DraweeController controller) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(request, imageView.getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                if (bitmap != null) {
                    processPalette(bitmap);
                }
            }
        }, CallerThreadExecutor.getInstance());

        imageView.setController(controller);
    }


    private void processPalette(Bitmap bitmap) {
        float imageScale = (float) imageView.getHeight() / (float) bitmap.getHeight();
        float twentyFourDip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                getResources().getDisplayMetrics());
        Palette.from(bitmap)
                .maximumColorCount(3)
                .clearFilters()
                .setRegion(0, 0, bitmap.getWidth() - 1, (int) (twentyFourDip / imageScale))
                        // - 1 to work around https://code.google.com/p/android/issues/detail?id=191013
                .generate(new Palette.PaletteAsyncListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onGenerated(Palette palette) {
                        boolean isDark;
                        @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                        if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                            isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                        } else {
                            isDark = lightness == ColorUtils.IS_DARK;
                        }

                        int color = Color.WHITE;
                        if (!isDark) { // make back icon dark on light images
                            color = ContextCompat.getColor(PostActivity.this, R.color.dark_icon);
                        }
                        back.setColorFilter(color);
                        source.setColorFilter(color);

                        // color the status bar. Set a complementary dark color on L,
                        // light or dark color on M (with matching status bar icons)
                        int statusBarColor = getWindow().getStatusBarColor();
                        Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
                        if (topColor != null &&
                                (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                            statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                    isDark, SCRIM_ADJUSTMENT);
                            // set a light status bar on M+
                            if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ViewUtils.setLightStatusBar(imageView);
                            }
                        }

                        if (statusBarColor != getWindow().getStatusBarColor()) {
                            imageView.setScrimColor(statusBarColor);
                            ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(getWindow
                                    ().getStatusBarColor(), statusBarColor);
                            statusBarColorAnim.addUpdateListener(new ValueAnimator
                                    .AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    getWindow().setStatusBarColor((int) animation
                                            .getAnimatedValue());
                                }
                            });
                            statusBarColorAnim.setDuration(1000);
                            statusBarColorAnim.setInterpolator(AnimationUtils
                                    .loadInterpolator(PostActivity.this, android.R
                                            .interpolator.fast_out_slow_in));
                            statusBarColorAnim.start();
                        }
                    }
                });

        Palette.from(bitmap)
                .clearFilters() // by default palette ignore certain hues (e.g. pure
                        // black/white) but we don't want this.
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // color the ripple on the image spacer (default is grey)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            shotSpacer.setBackground(ViewUtils.createRipple(palette, 0.25f, 0.5f,
                                    ContextCompat.getColor(PostActivity.this, R.color.mid_grey),
                                    true));
                        }
                        // slightly more opaque ripple on the pinned image to compensate
                        // for the scrim
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            imageView.setForeground(ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                    ContextCompat.getColor(PostActivity.this, R.color.mid_grey),
                                    true));
                        }
                    }
                });

        // TODO should keep the background if the image contains transparency?!
//        imageView.setBackground(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!performingLike) {
            checkLiked();
        }
        draggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        draggableFrame.removeListener(chromeFader);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_LOGIN_LIKE:
                if (resultCode == RESULT_OK) {
                    setupDribbble(); // recreate to capture the new access token
                    // TODO when we add more authenticated actions will need to keep track of what
                    // the user was trying to do when forced to login
                    fab.setChecked(true);
                    doLike();
                    setupCommenting();
                }
                break;
            case RC_LOGIN_COMMENT:
                if (resultCode == RESULT_OK) {
                    setupCommenting();
                }
            case PICK_SELECTION:
                if (resultCode == RESULT_OK) {
                    long id = data.getLongExtra(Launcher.EXTRA_ID, -1);
                    boolean checked = data.getBooleanExtra(Launcher.EXTRA_CHECKED, false);
                    if (checked) {
                        UI.showToast(this, R.string.add_post_to_collection_success);
                    } else if (id >= 0) {
                        addToCollections(id);
                    }
                }
        }
    }

    private void addToCollections(long id) {
        if (!Utils.hasInternet(this)) {
            return;
        }
        if (UI.notLogin(PostActivity.this)) {
            return;
        }
        Api.getApiService().addToCollection(id, shot.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(collect -> handleCollect(collect)
                        , new ErrorCallback(this)
                )
        ;
    }

    private void handleCollect(Collect collect) {
        if (JsoupUtil.OK.equals(collect.status)) {
            UI.showToast(this, R.string.add_post_to_collection_success);
        }
    }

    @Override
    public void onBackPressed() {
        if (UI.isLollipop()) {
            expandImageAndFinish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigateUp() {
        expandImageAndFinish();
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onProvideAssistContent(AssistContent outContent) {
        outContent.setWebUri(Uri.parse(shot.url));
    }

    private void setupCommenting() {
        allowComment = App.isLogin();
        if (allowComment && commentFooter == null) {
            commentFooter = getLayoutInflater().inflate(R.layout.dribbble_enter_comment,
                    commentsList, false);
            userAvatar = (SimpleDraweeView) commentFooter.findViewById(R.id.avatar);
            enterComment = (EditText) commentFooter.findViewById(R.id.comment);
            postComment = (ImageButton) commentFooter.findViewById(R.id.post_comment);
            enterComment.setOnFocusChangeListener(enterCommentFocus);
            commentsList.addFooterView(commentFooter);
        } else if (!allowComment && commentFooter != null) {
            commentsList.removeFooterView(commentFooter);
            commentFooter = null;
            Toast.makeText(getApplicationContext(),
                    R.string.prospects_cant_post, Toast.LENGTH_SHORT).show();
        }

        if (allowComment) {
            String avatar = mPref.getUserAvatar();
            if (!TextUtils.isEmpty(avatar)) {
                userAvatar.setImageURI(Uri.parse(avatar));
            }
        }
    }

    private View.OnClickListener shotClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            String url = shot.url.startsWith("/") ? Api.ENDPOINT + shot.url : shot.url;
//            openLink(url);
            Launcher.openPhoteView(PostActivity.this, shot, imageView);
        }
    };

    private void openLink(String url) {
        CustomTabActivityHelper.openCustomTab(
                PostActivity.this,
                new CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(PostActivity.this, R.color.dribbble))
                        .build(),
                Uri.parse(url));
    }


    private View.OnFocusChangeListener enterCommentFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            // kick off an anim (via animated state list) on the post button. see
            // @drawable/ic_add_comment_state
            postComment.setActivated(hasFocus);
        }
    };

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItemPosition, int
                visibleItemCount, int totalItemCount) {
            if (commentsList.getMaxScrollAmount() > 0
                    && firstVisibleItemPosition == 0
                    && commentsList.getChildAt(0) != null) {
                int listScroll = commentsList.getChildAt(0).getTop();
                imageView.setOffset(listScroll);
                fab.setOffset(fabOffset + listScroll);
            }
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // as we animate the main image's elevation change when it 'pins' at it's min height
            // a fling can cause the title to go over the image before the animation has a chance to
            // run. In this case we short circuit the animation and just jump to state.
            imageView.setImmediatePin(scrollState == AbsListView.OnScrollListener
                    .SCROLL_STATE_FLING);
        }
    };

    private View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fab.toggle();
            doLike();

//            if (dribbblePrefs.isLoggedIn()) {
//                fab.toggle();
//                doLike();
//            } else {
//                Intent login = new Intent(PostActivity.this, DribbbleLogin.class);
//                login.putExtra(FabDialogMorphSetup.EXTRA_SHARED_ELEMENT_START_COLOR,
//                        ContextCompat.getColor(PostActivity.this, R.color.dribbble));
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
//                        (PostActivity.this, fab, getString(R.string.transition_dribbble_login));
//                startActivityForResult(login, RC_LOGIN_LIKE, options.toBundle());
//            }
        }
    };

    private SharedElementCallback fabLoginSharedElementCallback = new SharedElementCallback() {
        @Override
        public Parcelable onCaptureSharedElementSnapshot(View sharedElement,
                                                         Matrix viewToGlobalMatrix,
                                                         RectF screenBounds) {
            // store a snapshot of the fab to fade out when morphing to the login dialog
            int bitmapWidth = Math.round(screenBounds.width());
            int bitmapHeight = Math.round(screenBounds.height());
            Bitmap bitmap = null;
            if (bitmapWidth > 0 && bitmapHeight > 0) {
                bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                sharedElement.draw(new Canvas(bitmap));
            }
            return bitmap;
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitionListener() {
        Transition.TransitionListener shotReturnHomeListener = new AnimUtils
                .TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);
                // hide the fab as for some reason it jumps position??  TODO work out why
                fab.setVisibility(View.INVISIBLE);
                // fade out the "toolbar" & list as we don't want them to be visible during return
                // animation
                back.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setInterpolator(AnimationUtils.loadInterpolator(PostActivity.this, android.R
                                .interpolator.linear_out_slow_in));
                source.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setInterpolator(AnimationUtils.loadInterpolator(PostActivity.this, android.R
                                .interpolator.linear_out_slow_in));
                ViewCompat.setElevation(imageView, 1f);
                ViewCompat.setElevation(back, 0f);
                ViewCompat.setElevation(source, 0f);
                commentsList.animate()
                        .alpha(0f)
                        .setDuration(50)
                        .setInterpolator(AnimationUtils.loadInterpolator(PostActivity.this, android.R
                                .interpolator.linear_out_slow_in));
            }
        };

        getWindow().getSharedElementReturnTransition().addListener(shotReturnHomeListener);
    }

    private void loadComments() {
        commentsList.setAdapter(getLoadingCommentsAdapter());
        if (!Utils.hasInternet(this)) {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }
        Api.getApiService().getComments(shot.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comments -> handleResponse(comments), new ErrorCallback(this));
    }

    private void handleResponse(List<Comment> comments) {
        if (comments != null && !comments.isEmpty()) {
            commentsAdapter = new DribbbleCommentsAdapter(PostActivity.this, R.layout
                    .dribbble_comment, comments);
            commentsList.setAdapter(commentsAdapter);
            commentsList.setDivider(getResources().getDrawable(R.drawable.list_divider));
            commentsList.setDividerHeight(getResources().getDimensionPixelSize(R.dimen
                    .divider_height));
        } else {
            commentsList.setAdapter(getNoCommentsAdapter());
        }
    }

    private void expandImageAndFinish() {
        if (imageView.getOffset() != 0f) {
            Animator expandImage = ObjectAnimator.ofFloat(imageView, ParallaxScrimageView.OFFSET,
                    0f);
            expandImage.setDuration(80);
            expandImage.setInterpolator(AnimationUtils.loadInterpolator(this, android.R
                    .interpolator.fast_out_slow_in));
            expandImage.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    supportFinishAfterTransition();
                }
            });
            expandImage.start();
        } else {
            supportFinishAfterTransition();
        }
    }

    private void setupDribbble() {
        commentsList.setAdapter(getLoadingCommentsAdapter());
        if (shot != null && shot.getImageUrl() == null) {
            loaderImage();
        }

        if (mFull) {
            handleContent(shot);
            return;
        }
        if (!Utils.hasInternet(this)) {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }

        Api.getApiService().getPostSidebar(shot.id)
                .map(muResponse -> JsoupUtil.getPostContent1(shot, muResponse))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(content -> handleContent(content), new ErrorCallback(this));
    }

    private void loaderImage() {
        mNeedUpdateConent = true;
        Api.getApiService().getPost(shot.id)
                .map(muResponse ->JsoupUtil.getPostContent(shot, muResponse))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(content -> handleContent(content), new ErrorCallback(this));
    }

    private void handleUpvotersContent(List<User> users) {
        shot.setUpvoters(users);
    }

    private void updateVoteCount() {
        NumberFormat nf = NumberFormat.getInstance();
        voteCount.setText(
                getResources().getQuantityString(R.plurals.upvotes,
                        (int) shot.getVotes(),
                        nf.format(shot.getVotes())));
    }

    private void handleContent(Post content) {
        shot = content;
//        loaderUpvoters();
        if (mNeedUpdateConent) {
            BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {
                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }

                @Override
                protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                    if (bitmap != null && UI.isLollipop()) {
                        processPalette(bitmap);
                    }
                }
            };
            FrescoUtils.setShotUrl(imageView, shot.getImageUrl(), shot.getTeaserUrl(), subscriber, false);
            source.setImageResource(shot.getSourceIcon());
        }
        checkLiked();
        updateVoteCount();
        if (shot.isHasComments()) {
            loadComments();
        } else {
            commentsList.setAdapter(getNoCommentsAdapter());
        }

        setupUserInfo();
        if (!TextUtils.isEmpty(shot.description)) {
            final Spanned descText = shot.getParsedDescription(
                    ContextCompat.getColorStateList(this, R.color.dribbble_links),
                    ContextCompat.getColor(this, R.color.dribbble_link_highlight));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((FabOverlapTextView) description).setText(descText);
            } else {
                HtmlUtils.setTextWithNiceLinks((TextView) description, descText);
            }
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }
    }

    private void calculateFabPosition() {
        // calculate 'natural' position i.e. with full height image. Store it for use when scrolling
        fabOffset = imageView.getHeight() + title.getHeight() - (fab.getHeight() / 2);
        fab.setOffset(fabOffset);

        // calculate min position i.e. pinned to the collapsed image when scrolled
        fab.setMinOffset(imageView.getMinimumHeight() - (fab.getHeight() / 2));
    }

    /**
     * Animate in the title, description and author – can't do this in a content transition as they
     * are within the ListView so do it manually.  Also handle the FAB tanslation here so that it
     * plays nicely with #calculateFabPosition
     */
    private void enterAnimation(boolean isOrientationChange) {
        Interpolator interp = null;
        if (UI.isLollipop()) {
            interp = AnimationUtils.loadInterpolator(this, android.R.interpolator
                    .fast_out_slow_in);
        } else {
            interp = AnimationUtils.loadInterpolator(this, android.R.interpolator
                    .accelerate_decelerate);
        }
        int offset = title.getHeight();
        viewEnterAnimation(title, offset, interp);
        if (description.getVisibility() == View.VISIBLE) {
            offset *= 1.5f;
            viewEnterAnimation(description, offset, interp);
        }
        // animate the fab without touching the alpha as this is handled in the content transition
        offset *= 1.5f;
        float fabTransY = fab.getTranslationY();
        fab.setTranslationY(fabTransY + offset);
        fab.animate()
                .translationY(fabTransY)
                .setDuration(600)
                .setInterpolator(interp)
                .start();
        offset *= 1.5f;
        viewEnterAnimation(shotActions, offset, interp);
        offset *= 1.5f;
        viewEnterAnimation(playerName, offset, interp);
        viewEnterAnimation(playerAvatar, offset, interp);
        viewEnterAnimation(shotTimeAgo, offset, interp);
        back.animate()
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(interp)
                .start();
        source.animate()
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(interp)
                .start();

        if (isOrientationChange) {
            // we rely on the window enter content transition to show the fab. This isn't run on
            // orientation changes so manually show it.
            Animator showFab = ObjectAnimator.ofPropertyValuesHolder(fab,
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f));
            showFab.setStartDelay(300L);
            showFab.setDuration(300L);
            showFab.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            showFab.start();
        }
    }

    private void viewEnterAnimation(View view, float offset, Interpolator interp) {
        view.setTranslationY(offset);
        view.setAlpha(0.8f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private void doLike() {
        if (!Utils.hasInternet(this)) {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            fab.toggle();
            return;
        }
        if (UI.notLogin(PostActivity.this)) {
            return;
        }
        performingLike = true;
        if (fab.isChecked()) {
            Api.getApiService().upvotes(shot.id, "").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(vote -> handleVote(vote), new ErrorCallback(this));
//            dribbbleApi.like(shot.id, "", new retrofit.Callback<Like>() {
//                @Override
//                public void success(Like like, Response response) {
//                    performingLike = false;
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    performingLike = false;
//                }
//            });
        } else {
            Api.getApiService().downvote(shot.id, "").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(vote -> handleVote(vote), new ErrorCallback(this));
//            dribbbleApi.unlike(shot.id, new retrofit.Callback<Void>() {
//                @Override
//                public void success(Void aVoid, Response response) {
//                    performingLike = false;
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    performingLike = false;
//                }
//            });
        }
    }

    private void handleVote(Upvote vote) {
        shot.setVoted(fab.isChecked());
        int count = vote.count;
        if (!fab.isChecked() && count > 0) {
            count -= 1;
        }

        if (count != shot.getVotes()) {
            shot.setVotes(count);
            updateVoteCount();
            AnimatorSet s = new AnimatorSet();
            s.setDuration(300).setInterpolator(new FastOutSlowInInterpolator());
            s.playTogether(
                    ObjectAnimator.ofFloat(voteCount, "alpha", 0, 1, 1, 1),
                    ObjectAnimator.ofFloat(voteCount, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                    ObjectAnimator.ofFloat(voteCount, "scaleY", 0.3f, 1.05f, 0.9f, 1));
            s.start();
        }
    }

    private void checkLiked() {
        fab.setChecked(shot.isVoted());
        fab.jumpDrawablesToCurrentState();
//        if (dribbblePrefs.isLoggedIn()) {
//            dribbbleApi.liked(shot.id, new retrofit.Callback<Like>() {
//                @Override
//                public void success(Like like, Response response) {
//                    // note that like.user will be null here
//                    fab.setChecked(like != null);
//                    fab.jumpDrawablesToCurrentState();
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    // 404 is expected if shot is not liked
//                    fab.setChecked(false);
//                    fab.jumpDrawablesToCurrentState();
//                }
//            });
//        }
    }

    public void postComment(View view) {
        if (!Utils.hasInternet(this)) {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }
        if (App.isLogin()) {
            CharSequence text = enterComment.getText();
            if (TextUtils.isEmpty(text)) return;
            enterComment.setEnabled(false);
            try {
                JSONObject body = new JSONObject();
                //"post_id":2778,"body":"awesome animation!"}
                body.put("post_id", shot.id);
                body.put("body", text.toString().trim());
                RequestBody rb = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), body.toString());
                Api.getApiService().postComment(shot.id, rb)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(comment -> {
                                    loadComments();
                                    enterComment.getText().clear();
                                    enterComment.setEnabled(true);
                                }, throwable -> {
                                    enterComment.setEnabled(true);
                                    HttpException e = (HttpException) throwable;
                                    try {
                                        L.d(e.response().errorBody().string());
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    throwable.printStackTrace();
                                }
                        );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent login = new Intent(PostActivity.this, LoginActivity.class);
//            login.putExtra(FabDialogMorphSetup.EXTRA_SHARED_ELEMENT_START_COLOR, ContextCompat.getColor
//                    (this, R.color.background_light));
//            ActivityOptions options =
//                    ActivityOptions.makeSceneTransitionAnimation(PostActivity.this, postComment,
//                            getString(R.string.transition_dribbble_login));
            startActivityForResult(login, RC_LOGIN_COMMENT, null);
        }
    }

    private boolean isOP(long playerId) {
        return true;
//        return shot.user != null && shot.user.id == playerId;
    }

    private ListAdapter getNoCommentsAdapter() {
        String[] noComments = {getString(R.string.no_comments)};
        return new ArrayAdapter<>(this, R.layout.dribbble_no_comments, noComments);
    }

    private ListAdapter getLoadingCommentsAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return PostActivity.this.getLayoutInflater().inflate(R.layout.loading, parent,
                        false);
            }
        };
    }

    protected class DribbbleCommentsAdapter extends ArrayAdapter<Comment> {

        private final LayoutInflater inflater;
        private Transition change;
        private int expandedCommentPosition = ListView.INVALID_POSITION;

        public DribbbleCommentsAdapter(Context context, int resource, List<Comment> comments) {
            super(context, resource, comments);
            inflater = LayoutInflater.from(context);
            if (UI.isLollipop()) {
                change = new AutoTransition();
                change.setDuration(200L);
                change.setInterpolator(AnimationUtils.loadInterpolator(context,
                        android.R.interpolator.fast_out_slow_in));
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            if (view == null) {
                view = newNewCommentView(position, container);
            }
            bindComment(getItem(position), position, view);
            return view;
        }

        private View newNewCommentView(int position, ViewGroup parent) {
            View view = inflater.inflate(R.layout.dribbble_comment, parent, false);
            view.setTag(R.id.player_avatar, view.findViewById(R.id.player_avatar));
            view.setTag(R.id.comment_author, view.findViewById(R.id.comment_author));
            view.setTag(R.id.comment_time_ago, view.findViewById(R.id.comment_time_ago));
            view.setTag(R.id.comment_text, view.findViewById(R.id.comment_text));
            view.setTag(R.id.comment_reply, view.findViewById(R.id.comment_reply));
            view.setTag(R.id.comment_like, view.findViewById(R.id.comment_like));
            view.setTag(R.id.comment_likes_count, view.findViewById(R.id.comment_likes_count));
            return view;
        }

        private void bindComment(final Comment comment, final int position, final View view) {
            final SimpleDraweeView avatar = (SimpleDraweeView) view.getTag(R.id.player_avatar);
            final AuthorTextView author = (AuthorTextView) view.getTag(R.id.comment_author);
            final TextView timeAgo = (TextView) view.getTag(R.id.comment_time_ago);
            final TextView commentBody = (TextView) view.getTag(R.id.comment_text);
            final ImageButton reply = (ImageButton) view.getTag(R.id.comment_reply);
            final CheckableImageButton likeHeart =
                    (CheckableImageButton) view.getTag(R.id.comment_like);
            final TextView likesCount = (TextView) view.getTag(R.id.comment_likes_count);

            final String url = comment.user.avatarUrl;
            if (TextUtils.isEmpty(url)) {
                avatar.setImageURI(null);
            } else {
                avatar.setImageURI(Uri.parse(url));
            }
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Launcher.launchUser(PostActivity.this, comment.user.path, comment.user.avatarUrl, null, avatar);
                }
            });
            author.setText(comment.user.fullName);
            author.setOriginalPoster(isOP(comment.user.id));
            timeAgo.setText(comment.createdAt == null ? "" :
                    DateUtils.getRelativeTimeSpanString(comment.createdAt.getTime(),
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS));
            HtmlUtils.setTextWithNiceLinks(commentBody, comment.getParsedBody(commentBody));

            view.setActivated(position == expandedCommentPosition);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isExpanded = reply.getVisibility() == View.VISIBLE;
                    if (UI.isLollipop()) {
                        TransitionManager.beginDelayedTransition(commentsList, change);
                    }
                    view.setActivated(!isExpanded);
                    if (!isExpanded) { // do expand
                        expandedCommentPosition = position;
                        reply.setVisibility(View.VISIBLE);
                        likeHeart.setVisibility(View.VISIBLE);
                        likesCount.setVisibility(View.VISIBLE);
                        if (!comment.liked && Utils.hasInternet(PostActivity.this)) {
                            Api.getApiService().likedComment(shot.id, comment.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(c -> {
                                        comment.liked = c.liked;
                                        comment.commentLikesCount = c.commentLikesCount;
                                        likesCount.setText(String.valueOf(c.commentLikesCount));
                                        likeHeart.setChecked(c.liked);
                                        likeHeart.jumpDrawablesToCurrentState();
                                    }, throwable -> {
                                        comment.liked = false;
                                        likeHeart.setChecked(false);
                                        likeHeart.jumpDrawablesToCurrentState();
                                    });

                        }
                        if (enterComment != null && enterComment.hasFocus()) {
                            enterComment.clearFocus();
                            ImeUtils.hideIme(enterComment);
                        }
                        view.requestFocus();
                    } else { // do collapse
                        expandedCommentPosition = ListView.INVALID_POSITION;
                        reply.setVisibility(View.GONE);
                        likeHeart.setVisibility(View.GONE);
                        likesCount.setVisibility(View.GONE);
                    }
                    notifyDataSetChanged();
                }
            });

            reply.setVisibility((position == expandedCommentPosition && allowComment) ?
                    View.VISIBLE : View.GONE);
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterComment.setText(comment.user.twitterUsername + " ");
                    enterComment.setSelection(enterComment.getText().length());

                    // collapse the comment and scroll the reply box (in the footer) into view
                    expandedCommentPosition = ListView.INVALID_POSITION;
                    notifyDataSetChanged();
                    enterComment.requestFocus();
                    commentsList.smoothScrollToPositionFromTop(commentsList.getCount(), 0, 300);
                }
            });

            likeHeart.setChecked(comment.liked);
            likeHeart.setVisibility(position == expandedCommentPosition ? View.VISIBLE : View.GONE);
            likeHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.isLogin()) {
                        if (!Utils.hasInternet(PostActivity.this)) {
                            Toast.makeText(PostActivity.this, R.string.check_network, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!comment.liked) {
                            comment.liked = true;
                            comment.commentLikesCount++;
                            likesCount.setText(String.valueOf(comment.commentLikesCount));
                            notifyDataSetChanged();
                            Api.getApiService().likedComment(shot.id, comment.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(c -> {
                                        comment.liked = c.liked;
                                        comment.commentLikesCount = c.commentLikesCount;
                                        likesCount.setText(String.valueOf(c.commentLikesCount));
                                        likeHeart.setChecked(c.liked);
                                        likeHeart.jumpDrawablesToCurrentState();
                                    }, throwable -> {
                                        comment.liked = false;
                                        likeHeart.setChecked(false);
                                        likeHeart.jumpDrawablesToCurrentState();
                                    });
                        } else {
                            comment.liked = false;
                            comment.commentLikesCount--;
                            likesCount.setText(String.valueOf(comment.commentLikesCount));
                            notifyDataSetChanged();
                            Api.getApiService().unlikedComment(shot.id, comment.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(c -> {
                                        comment.liked = c.liked;
                                        comment.commentLikesCount = c.commentLikesCount;
                                        likesCount.setText(String.valueOf(c.commentLikesCount));
                                        likeHeart.setChecked(c.liked);
                                        likeHeart.jumpDrawablesToCurrentState();
                                    }, throwable -> {
                                        comment.liked = true;
                                        likeHeart.setChecked(true);
                                        likeHeart.jumpDrawablesToCurrentState();
                                    });
                        }
                    } else {
                        likeHeart.setChecked(false);
                        startActivityForResult(new Intent(PostActivity.this,
                                LoginActivity.class), RC_LOGIN_LIKE);
                    }
                }
            });
            likesCount.setVisibility(
                    position == expandedCommentPosition ? View.VISIBLE : View.GONE);
            likesCount.setText(String.valueOf(comment.commentLikesCount));
//            likesCount.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dribbbleApi.getCommentLikes(shot.id, comment.id,
//                            new retrofit.Callback<List<Like>>() {
//                                @Override
//                                public void success(List<Like> likes, Response response) {
//                                    // TODO something better than this.
//                                    StringBuilder sb = new StringBuilder("Liked by:\n\n");
//                                    for (Like like : likes) {
//                                        if (like.user != null) {
//                                            sb.append("@");
//                                            sb.append(like.user.username);
//                                            sb.append("\n");
//                                        }
//                                    }
//                                    Toast.makeText(getApplicationContext(), sb.toString(), Toast
//                                            .LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void failure(RetrofitError error) {
//                                    Log.e("GET COMMENT LIKES", error.getMessage(), error);
//                                }
//                            });
//                }
//            });
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

    }

    private void handleUser(User s) {
    }
}
