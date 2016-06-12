package org.goodev.ui;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.goodev.material.R;
import org.goodev.material.model.Post;
import org.goodev.material.util.FrescoUtils;
import org.goodev.material.util.L;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yfcheng on 2015/12/25.
 */
public class AdViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.user_image)
    SimpleDraweeView mUserImageView;

    @Bind(R.id.shot_title)
    TextView mShotTitleView;

    @Bind(R.id.shot_views)
    RatingBar mShotViewsView;
    @Bind(R.id.shot_description)
    TextView mShotDescriptionView;
    @Bind(R.id.shot_comment)
    TextView mShotComment;
    @Bind(R.id.shot_size)
    TextView mSize;
    @Bind(R.id.shot_image)
    SimpleDraweeView mShotImageView;

    private Activity mContext;

    public AdViewHolder(View view, Activity context) {
        super(view);
        mContext = context;
        ButterKnife.bind(this, view);
    }


    public SimpleDraweeView getShotView() {
        return mShotImageView;
    }

    public void setData(Post shot) {
        FrescoUtils.setShotUrl(mShotImageView, shot.imageUrl, null);
        mUserImageView.setImageURI(Uri.parse(shot.teaserUrl));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ads.onClickAd(mContext, shot.price);
            }
        });


        mShotTitleView.setText(shot.title);
        mShotComment.setText(shot.createTime);
        mSize.setText(shot.avatarUrl);
        mShotViewsView.setRating(shot.votes / 100f);

        if (TextUtils.isEmpty(shot.description)) {
            mShotDescriptionView.setText(null);
            mShotDescriptionView.setVisibility(View.INVISIBLE);
        } else {
            mShotDescriptionView.setText(shot.description);
        }


    }

}
