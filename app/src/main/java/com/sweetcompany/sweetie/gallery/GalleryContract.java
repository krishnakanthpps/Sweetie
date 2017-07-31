package com.sweetcompany.sweetie.gallery;

import android.graphics.Bitmap;
import java.util.List;

/**
 * Created by ghiro on 22/07/2017.
 */

interface GalleryContract {

    interface View {
        void setPresenter(GalleryContract.Presenter presenter);
        void updateMediaList(List<MediaVM> medias);
        void updateGalleryInfo(GalleryVM gallery);

        void updateMedia(MediaVM mediaVM);
        void removeMedia(MediaVM mediaVM);
        void changeMedia(MediaVM mediaVM);
    }
    
    interface Presenter {
        void sendMedia(MediaVM media);
    }
}
