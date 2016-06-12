package org.goodev.material.binding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import org.goodev.material.util.FrescoUtils;
import org.goodev.material.util.L;
import org.goodev.material.widget.FABToggle;

/**
 * Created by yfcheng on 2015/11/27.
 */
public class BindingSetters {

//    @BindingMethods({
//            @BindingMethod(type = RatingBar.class, attribute = "android:rating", method = "setOnRatingBarChangeListener"),
//    })

    @BindingAdapter({"bind:normalUrl", "bind:teaserUrl", "bind:background"})
    public static void loadImage(SimpleDraweeView view, String normal, String teaser, int background) {
        FrescoUtils.setShotHierarchy(view.getContext(), view, background);
        FrescoUtils.setShotUrl(view, normal, teaser);
//        Picasso.with(view.getContext()).load(url).error(error).into(view);
    }

    @BindingAdapter({"bind:normalUrl", "bind:teaserUrl"})
    public static void loadImage(SimpleDraweeView view, String normal, String teaser) {
        ImageRequest imageRequest = ImageRequest.fromUri(normal);
        ImageRequest lowRequest = null;
        if (!TextUtils.isEmpty(teaser)) {
            lowRequest = ImageRequest.fromUri(teaser);
        }
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setLowResImageRequest(lowRequest)
                .setOldController(view.getController())
                .setAutoPlayAnimations(true)
                .build();

        view.setController(draweeController);
    }

    @BindingAdapter({"bind:normalUrl"})
    public static void loadImage(SimpleDraweeView view, String normal) {
        if (TextUtils.isEmpty(normal)) {
            view.setImageURI(null);
            return;
        }
        try {
            view.setImageURI(Uri.parse(normal));
        } catch (Exception e) {
            L.e("avatar :%s", normal);
            e.printStackTrace();
        }
    }

    @BindingAdapter({"app:vd"})
    public static void setVectorDrawable(ImageView view, @DrawableRes int id) {
        VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(view.getResources(), id, view.getContext().getTheme());
        view.setImageResource(id);
    }

    @BindingAdapter({"app:vdc"})
    public static void setVectorDrawable(FABToggle view, Drawable id) {
        view.setImageDrawable(id);
    }
}
