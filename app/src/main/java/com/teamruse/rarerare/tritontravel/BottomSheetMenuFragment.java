package com.teamruse.rarerare.tritontravel;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JingJing on 12/7/17.
 */


public class BottomSheetMenuFragment extends BottomSheetDialogFragment {
    private static final String ARG_LAYOUT_ID = "ARG_LAYOUT_ID";
    private static final int LIST = R.layout.fragment_list_bottom_sheet;
    //private static final int SLIST = R.layout.sheet_menu;

    public static BottomSheetMenuFragment createInstanceList() {
        return getInstance(LIST);
    }
    /*public static BottomSheetMenuFragment createInstanceListS() {
        return getInstance(SLIST);
    }*/

    private static BottomSheetMenuFragment getInstance(int layoutId) {
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        BottomSheetMenuFragment frag = new BottomSheetMenuFragment();
        frag.setArguments(args);
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

                    Log.d(BottomSheetMenuFragment.class.getSimpleName(), state);
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    Log.d(BottomSheetMenuFragment.class.getSimpleName(), String.valueOf(slideOffset));
                }
            };


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



    @OnClick({R.id.saveRoutesButton})
    public void onClickBottomSheet(View view) {
        //FirebaseUser user = MapFragment.mAuth.getCurrentUser();
        if (!((MainActivity)getActivity()).signedIn()) {
            Toast.makeText(getContext(),"Please sign in", Toast.LENGTH_SHORT).show();
        }
        else {

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
