package com.vojkovladimir.world.ui.request;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.vojkovladimir.world.R;

/**
 * @author vojkovladimir.
 */
public class LoadRequestListener implements RequestListener<String, GlideDrawable> {

    ImageView mImageView;
    int color;

    public LoadRequestListener(ImageView imageView, int color) {
        mImageView = imageView;
        this.color = color;
    }


    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                               boolean isFirstResource) {
        mImageView.setColorFilter(color);
        mImageView.setScaleType(ImageView.ScaleType.CENTER);
        mImageView.setImageResource(R.drawable.ic_image_black_48dp);
        return true;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model,
                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
                                   boolean isFirstResource) {
        GlideAnimation<GlideDrawable> animation = new DrawableCrossFadeFactory<GlideDrawable>()
                .build(isFromMemoryCache, isFirstResource);
        mImageView.clearColorFilter();
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        target.onResourceReady(resource, animation);
        return true;
    }
}
