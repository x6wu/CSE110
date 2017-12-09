package com.teamruse.rarerare.tritontravel;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JingJing on 12/7/17.
 */


public class BottomSheetMenuFragment extends BottomSheetDialogFragment {
    private static final String ARG_LAYOUT_ID = "ARG_LAYOUT_ID";
    private static final int LAYOUT_ID = R.layout.fragment_list_bottom_sheet;
    private static final String TAG = "BottomSheetMenuFragment";
    //private static final int SLIST = R.layout.sheet_menu;

    public static BottomSheetMenuFragment createInstanceList(List<Path> paths) {
        return getInstance(paths);
    }


    private static BottomSheetMenuFragment getInstance(List<Path> paths) {

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, LAYOUT_ID);
        //convert to parcelable class
        ArrayList<pathResult> results = new ArrayList<>();
        for (Path path: paths) {
            results.add(new pathResult(path));
        }
        args.putParcelableArrayList("list", results);
        BottomSheetMenuFragment frag = new BottomSheetMenuFragment();
        frag.setArguments(args);
        Log.d(TAG, "Creating new BottomSheetMenuFragment");
        return frag;
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new
        BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                String state = null;

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        state = "STATE_COLLAPSED";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        state = "STATE_DRAGGING";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        state = "STATE_EXPANDED";
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        state = "STATE_SETTLING";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        state = "STATE_HIDDEN";
                        //call ALWAYS dismiss to hide the modal background
                        dismiss();
                        break;
                }

                Log.d(TAG, "State changed:" + state);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d(TAG, "onSlide: " + String.valueOf(slideOffset));
            }
        };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.fragment_list_bottom_sheet);
        LinearLayout path_container = (LinearLayout) getActivity().findViewById(R.id.path_container);

        try {
            ArrayList<pathResult> results = savedInstanceState.getParcelableArrayList("list");
            for(pathResult result: results) {
                LinearLayout a = new LinearLayout(getContext());
                a.setOrientation(LinearLayout.HORIZONTAL);
                //Place the object in the center of its container in both the vertical and horizontal
                //axis, not changing its size.
                a.setGravity(11);
                ArrayList<String> segments = result.segments;
                for(int i=0; i<segments.size(); i++) {
                    if(segments.get(i) == "Walking") {
                        ImageView img = new ImageView(getContext());
                        img.setImageResource(R.drawable.ic_walk);
                        a.addView(img);
                    } else {
                        ImageView img = new ImageView(getContext());
                        img.setImageResource(R.drawable.ic_bus);
                        a.addView(img);
                        TextView txt = new TextView(getContext());
                        txt.setText(segments.get(i));
                        a.addView(txt);
                    }
                    ImageView img = new ImageView(getContext());
                    img.setImageResource(R.drawable.ic_menu_send);
                    a.addView(img);
                }
                path_container.addView(a);
            }
        } catch(NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }



    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        //noinspection RestrictedApi
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), getArguments().getInt(ARG_LAYOUT_ID), null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        BottomSheetBehavior<View> mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView
                .getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBottomSheetBehavior.setPeekHeight(1200);
        }
    }



    //@OnClick({R.id.saveRoutesButton})
    public void onClickBottomSheet(View view) {
        FirebaseUser user = MapFragment.mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(),"Please sign in", Toast.LENGTH_SHORT).show();
        }
        else if(user != null) {

            openDialog();

            //TODO
            //database
            /*MapFragment.mDatabase.child("routes")
                    .child("route_id_" + user.getUid())
                    .push()
                    .setValue(new StopHistory(MapFragment.mOriginPlace.getName().toString()
                            + " > " + MapFragment.mDestPlace.getName().toString()
                            ,MapFragment.mOriginPlace.getId() + " > " +  MapFragment.mDestPlace.getId()
                            ,MapFragment.tag));

            Toast.makeText(getContext(),"Route Saved" , Toast.LENGTH_SHORT).show();*/
        }



        dismiss();
    }

    public void openDialog() {
        TagDialog dialog = new TagDialog();
        dialog.show(getChildFragmentManager(),"tag dialog");
    }



}
