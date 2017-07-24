package com.sweetcompany.sweetie.gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sweetcompany.sweetie.R;
import com.sweetcompany.sweetie.actions.ActionNewChatFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghiro on 22/07/2017.
 */

public class GalleryFragment extends Fragment implements GalleryContract.View, View.OnClickListener{

    private static final String TAG = "ChatFragment";
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;

    private Toolbar mToolBar;
    private RecyclerView mGalleryListView;
    private LinearLayoutManager mLinearLayoutManager;

    private GalleryAdapter mGalleryAdapter;
    private GalleryContract.Presenter mPresenter;

    private static final String endpoint = "https://api.androidhive.info/json/glide.json";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private ProgressDialog pDialogPhoto;

    private FloatingActionButton mFabAddPhoto;

    private VolleyController mVolleyController;

    private Context mContext;

    public static com.sweetcompany.sweetie.gallery.GalleryFragment newInstance(Bundle bundle) {
        com.sweetcompany.sweetie.gallery.GalleryFragment newGalleryFragment = new com.sweetcompany.sweetie.gallery.GalleryFragment();
        newGalleryFragment.setArguments(bundle);

        return newGalleryFragment;
    }

    @Override
    public void setPresenter(GalleryContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        pDialog = new ProgressDialog(mContext);
        images = new ArrayList<>();

        mGalleryAdapter = new GalleryAdapter(mContext, images);
        //mGalleryAdapter.setGalleryAdapterListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.gallery_fragment, container, false);

        String titleGallery = getArguments().getString(GalleryActivity.GALLERY_TITLE);
        Log.d(TAG, "from Intent GALLERY_TITLE: " +
                getArguments().getString(GalleryActivity.GALLERY_TITLE));
        Log.d(TAG, "from Intent GALLERY_DATABASE_KEY: " +
                getArguments().getString(GalleryActivity.GALLERY_DATABASE_KEY));

        mGalleryListView = (RecyclerView) root.findViewById(R.id.photo_list);

        mToolBar = (Toolbar) root.findViewById(R.id.gallery_toolbar);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        parentActivity.getSupportActionBar().setTitle(titleGallery);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mGalleryListView.setLayoutManager(mLinearLayoutManager);
        mGalleryListView.setAdapter(mGalleryAdapter);

        mVolleyController = new VolleyController(mContext);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
        mGalleryListView.setLayoutManager(mLayoutManager);
        mGalleryListView.setItemAnimator(new DefaultItemAnimator());
        mGalleryListView.setAdapter(mGalleryAdapter);


        pDialogPhoto = new ProgressDialog(mContext);
        pDialogPhoto.setMessage("Uploading....");

        mFabAddPhoto = (FloatingActionButton) root.findViewById(R.id.fab_add_photo);
        mFabAddPhoto.setClickable(false);

        mFabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        mGalleryListView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(mContext, mGalleryListView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();

        return root;
    }

    private void fetchImages() {

        pDialog.setMessage("Downloading json...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);
                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mGalleryAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        mVolleyController.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void updatePhotos(List<PhotoVM> photos) {

    }

    @Override
    public void updateGalleryInfo(GalleryVM gallery) {

    }

    @Override
    public void updatePhoto(PhotoVM photoVM) {

    }

    @Override
    public void removePhoto(PhotoVM photoVM) {

    }

    @Override
    public void changePhoto(PhotoVM photoVM) {

    }
}