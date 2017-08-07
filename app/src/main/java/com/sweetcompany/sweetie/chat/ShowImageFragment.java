package com.sweetcompany.sweetie.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sweetcompany.sweetie.R;
import com.sweetcompany.sweetie.gallery.PhotoVM;
import com.sweetcompany.sweetie.gallery.SlideshowDialogFragment;
import com.sweetcompany.sweetie.utils.DataMaker;
import com.sweetcompany.sweetie.utils.Utility;

import java.util.ArrayList;

/**
 * Created by ghiro on 05/08/2017.
 */

public class ShowImageFragment extends DialogFragment {

    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private TextPhotoMessageVM image;
    private ViewPager viewPager;
    private ShowImageFragment.MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 1;

    static ShowImageFragment newInstance() {
        ShowImageFragment f = new ShowImageFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);

        image = (TextPhotoMessageVM) getArguments().getSerializable("image");

        myViewPagerAdapter = new ShowImageFragment.MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo();
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo() {
        //lblTitle.setText(image.get());
        lblDate.setText(DataMaker.get_dd_MM_yy_Local(image.getTime()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            String uriToLoad;
            // is image uploaded by me?
            //verify if is in Local memory and has valid path
            if(image.isTheMainUser()) {
                if(Utility.isImageAvaibleInLocal(image.getUriLocal())) uriToLoad = image.getUriLocal();
                else uriToLoad = image.getUriStorage();
            }
            else // uploaded by partner, take uri storage
            {
                uriToLoad = image.getUriStorage();
            }

            Glide.with(getActivity())
                    .load(uriToLoad)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}