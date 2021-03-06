package com.codernauti.sweetie.gallery;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.codernauti.sweetie.R;

class PhotoViewHolder extends MediaViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    private final ImageView mThumbnail;
    private final ProgressBar mProgressBar;
    private final ImageView mCheckIcon;

    PhotoViewHolder(View itemView) {
        super(itemView);

        mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_upload);
        mCheckIcon = (ImageView) itemView.findViewById(R.id.photo_check_icon);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    void showProgressBar(boolean show) {
        if (show){
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void setImage(String uri){
        Glide.with(itemView.getContext()).load(uri)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.image_placeholder)
                /*.diskCacheStrategy(DiskCacheStrategy.ALL)*/
                .into(mThumbnail);
    }

    public void setViewHolderSelected(boolean selected) {
        mThumbnail.setSelected(selected);
        if (selected) {
            mCheckIcon.setVisibility(View.VISIBLE);
            updateTintColor();
        } else {
            mCheckIcon.setVisibility(View.GONE);
            removeTintColor();
        }
    }

    private void updateTintColor() {
        mThumbnail.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.gallery_photo_selected));
    }

    private void removeTintColor() {
        mThumbnail.setColorFilter(null);
    }


    @Override
    public void onClick(View v) {
        mListener.onPhotoClicked(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        mListener.onPhotoLongClicked(getAdapterPosition());
        return true;
    }
}