package com.sweetcompany.sweetie.gallery;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.sweetcompany.sweetie.R;

import org.w3c.dom.Text;

import java.net.UnknownHostException;

/**
 * Created by ghiro on 25/07/2017.
 */

class PhotoViewHolder extends MediaViewHolder implements View.OnClickListener {
    private TextView mPercentUploading;
    private ImageView thumbnail;
    ProgressBar pbar;

    PhotoViewHolder(View itemView) {
        super(itemView);

        mPercentUploading = (TextView) itemView.findViewById(R.id.progress_percent);

        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        thumbnail.setOnClickListener(this);

        pbar = (ProgressBar) itemView.findViewById(R.id.progressBarUpload);
    }

    public void setPercentUploading(int progress){
        mPercentUploading.setText(progress + "%");
        if (progress >= 100){
            mPercentUploading.setVisibility(View.GONE);
            pbar.setVisibility(View.GONE);
        }
    }

    public void setImage(String uri){
        if(!uri.equals("")) {
            Uri uriP;
            uriP = Uri.parse(uri);

            Glide.with(itemView.getContext()).load(uriP)
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(thumbnail);
        }
    }

    @Override
    public void onClick(View v) {
        mListener.onPhotoClicked(getAdapterPosition());
        mListener.onPhotoLongClicked(getAdapterPosition());
    }

}