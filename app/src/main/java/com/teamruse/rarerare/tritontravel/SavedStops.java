package com.teamruse.rarerare.tritontravel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by JingJing on 12/7/17.
 */

public class SavedStops extends Fragment {

    private static final String TAG="SavedStops_tag";
    private ArrayList<StopHistory> stopsList;
    private FirebaseAuth mAuth;
    private ArrayList<String> savedStopList = new ArrayList<String>();
    //private ArrayList<StopHistory> listStops;
    public SavedStops() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_stops, container, false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("stops").child("stop_id_" + mAuth.getUid());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot shot : snapshot.getChildren()) {
                            savedStopList.add(shot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        if (stopsList == null) {
            stopsList = new ArrayList<>();
        }



        stopsList.add(new StopHistory("ss","tt"));

        final ListView lv = (ListView) view.findViewById(R.id.savedStops);
        //lv.setAdapter(new ListViewSavedAdapter(getActivity(), listStops));
        lv.setAdapter(new ListViewSavedAdapter(getContext(), stopsList));

        Log.d(TAG, "log an item:"+lv.getAdapter().getItem(0));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeId = ((StopHistory) lv.getAdapter().getItem(position)).getPlaceId();
                ((MainActivity) getActivity()).goToStop(placeId);


            }
        });

        Log.d(TAG,"onCreateView");
        return view;
    }
    @Override
    public void onResume() {

        super.onResume();
        Log.d(TAG, "resume");
    }
}
