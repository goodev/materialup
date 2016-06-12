package org.goodev.material.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.goodev.material.R;

/**
 * Created by yfcheng on 2015/6/6.
 */
public class FrescoUtils {

    //@formatter:off
    public static final void setShotHierarchy(Context context, DraweeView view, int bg) {
        setShotHierarchy(context, view, ScalingUtils.ScaleType.FIT_CENTER, bg);
    }

    public static final void setShotHierarchy(Context context, DraweeView view, ScalingUtils.ScaleType type, int bg) {
        final Resources res = context.getResources();
        final int color = ThemeUtil.getThemeColor(context, R.attr.colorAccent);
        final ProgressBarDrawable progress = new ProgressBarDrawable();
        progress.setBackgroundColor(Color.parseColor("#33000000"));
        progress.setColor(color);
        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(res)
                .setPlaceholderImage(res.getDrawable(R.drawable.ic_holder))
                .setProgressBarImage(progress)
                .setBackground(new ColorDrawable(bg))
                .setActualImageScaleType(type)
                .build();
        view.setHierarchy(gdh);
    }


    //    public static final void  setShotUrl(DraweeView view, String url, String thumbnail){
//        setShotUrl(view, url, thumbnail, null);
//    }
    public static final void setShotUrl(DraweeView view, String url, String thumbnail/*, BaseControllerListener listener*/) {
        setShotUrl(view, url, thumbnail, null, false);
    }

    private static final void setSubscribe(Context context, ImageRequest request, BaseDataSubscriber subscriber) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(request, context);
        dataSource.subscribe(subscriber, CallerThreadExecutor.getInstance());
    }

    public static final void setShotUrl(DraweeView view, String url, String thumbnail, BaseDataSubscriber subscriber, boolean full) {
        if (TextUtils.isEmpty(thumbnail) && TextUtils.isEmpty(url)) {
            return;
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
//                .setResizeOptions(
//                        new ResizeOptions(300, 400))
                .setProgressiveRenderingEnabled(true)
                .build();
        ImageRequest lowRequest = null;
        if (!TextUtils.isEmpty(thumbnail)) {
            lowRequest = ImageRequest.fromUri(thumbnail);
        }

        if (subscriber != null) {
            if (lowRequest != null && !full) {
                setSubscribe(view.getContext(), lowRequest, subscriber);
            } else if (imageRequest != null) {
                setSubscribe(view.getContext(), imageRequest, subscriber);
            }
        }

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setLowResImageRequest(lowRequest)
                .setOldController(view.getController())
                .setAutoPlayAnimations(true)
//                .setControllerListener(listener)
                .build();

//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        ImageRequest request = lowRequest == null ? imageRequest : lowRequest;
//        DataSource<CloseableReference<CloseableImage>> dataSource =
//                imagePipeline.fetchDecodedImage(request, view.getContext());
//        dataSource.subscribe(new BaseBitmapDataSubscriber() {
//            @Override
//            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//
//            }
//
//            @Override protected void onNewResultImpl(@Nullable Bitmap bitmap) {
//                Palette.from(bitmap).maximumColorCount(3).generate(new Palette.PaletteAsyncListener() {
//                    @Override public void onGenerated(Palette palette) {
//                    }
//                });
//            }
//        }, CallerThreadExecutor.getInstance());

        view.setController(draweeController);
    }

    public static final void setShotImage(DraweeView view, Uri uri) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
//                        .setResizeOptions(new ResizeOptions(1024,1024))
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(view.getController())
                .setAutoPlayAnimations(true)
                .build();
        view.setController(draweeController);
    }
    //@formatter:on
}
