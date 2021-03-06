package com.codernauti.sweetie.gallery;

import com.codernauti.sweetie.R;

import java.io.Serializable;

public class PhotoVM extends MediaVM implements Serializable {

    PhotoVM(boolean who, String date, String desc, String uriS, String key, boolean uploading) {
        super(who, date, desc, uriS, key, uploading);
    }

    @Override
    public void configViewHolder(GalleryViewHolder viewHolder) {
        PhotoViewHolder view = (PhotoViewHolder) viewHolder;

        view.setImage(super.getUriStorage());
        view.showProgressBar(isUploading());
    }

    @Override
    public int getIdView() {
            return R.layout.gallery_thumbnail;
    }
}
