package org.goodev.material;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import org.goodev.material.util.FileUtil;
import org.goodev.material.util.FrescoUtils;
import org.goodev.material.util.L;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.UI;
import org.goodev.material.widget.zoomable.ZoomableDraweeView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by yfcheng on 2015/12/11.
 */
public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.shot)
    ZoomableDraweeView mDraweeView;
    @Bind(R.id.back)
    ImageButton back;
    @Bind(R.id.save)
    ImageButton source;
    @Bind(R.id.container)
    FrameLayout container;
    String mUrl;
    boolean mCanSave;
    String mTitle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        String url = intent.getStringExtra(Launcher.EXTRA_URL);
        String preUrl = intent.getStringExtra(Launcher.EXTRA_PRE_URL);
        mTitle = intent.getStringExtra(Launcher.EXTRA_TITLE);
        if (mTitle != null) {
            mTitle = mTitle.replaceAll(" ","_");
            if (mTitle.length() > 15) {
                mTitle = mTitle.substring(0, 15);
            }
        }
        mUrl = url;
        ButterKnife.bind(this);

        FrescoUtils.setShotHierarchy(this, mDraweeView, 0);
        BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                enableSaveButton();
            }
        };
        FrescoUtils.setShotUrl(mDraweeView, url, preUrl, subscriber, true);

        back.setOnClickListener(this);
        source.setOnClickListener(this);

        if (UI.isLollipop()) {
            supportPostponeEnterTransition();
            source.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    source.getViewTreeObserver().removeOnPreDrawListener(this);
                    enterAnimation();
                    supportStartPostponedEnterTransition();
                    return true;
                }
            });
        }

        Nammu.init(getApplicationContext());
    }

    private void enableSaveButton() {
        mCanSave = true;
    }

    private void enterAnimation() {
        Interpolator interp = AnimationUtils.loadInterpolator(this, android.R.interpolator
                .fast_out_slow_in);
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


    }

    /**
     * Used to handle result of askForPermission for Location, in better way than onRequestPermissionsResult() and handling with big switch statement
     */
    final PermissionCallback permissionLocationCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            save();
        }

        @Override
        public void permissionRefused() {
            showTips();
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            supportFinishAfterTransition();
        } else if (v.getId() == R.id.save) {
            if (!mCanSave) {
                UI.showToast(this, R.string.image_is_loading);
                return;
            }
            if (Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                save();
            } else {
                if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //User already refused to give us this permission or removed it
                    //Now he/she can mark "never ask again" (sic!)
                    showTips();
                } else {
                    //First time asking for permission
                    // or phone doesn't offer permission
                    // or user marked "never ask again"
                    Nammu.askForPermission(this, Manifest.permission.READ_CONTACTS, permissionLocationCallback);
                }
            }
        }
    }

    private void showTips() {
        Snackbar.make(mDraweeView, R.string.storage_permission_info,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Nammu.askForPermission(PhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionLocationCallback);
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void save() {
        try {
            ImageRequest imageRequest = ImageRequest.fromUri(mUrl);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
            File file = ((FileBinaryResource) resource).getFile();

            String fileName = mUrl;
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            if (mTitle != null) {
                fileName = mTitle + fileName;
            }
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File dir = new File(pic, "material/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File renamed = new File(dir, fileName);
            if (!renamed.exists()) {
                renamed.createNewFile();
                FileUtil.copy(file, renamed);
            }
            UI.showToast(this, getString(R.string.image_saved_to, renamed.getAbsolutePath()));
//            Snackbar.make(mDraweeView,R.string.image_is_saved, Snackbar.LENGTH_LONG);
        } catch (Exception ex) {
            Log.w("SHARE", "Sharing " + mUrl + " failed", ex);
        }
    }


}
