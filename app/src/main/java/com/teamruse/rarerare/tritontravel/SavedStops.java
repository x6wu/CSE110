package com.teamruse.rarerare.tritontravel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by JingJing on 12/7/17.
 */

public class SavedStops extends Fragment {

    private static final String TAG="SavedStops_tag";
    private FirebaseAuth mAuth;
    private ArrayList<StopHistory> savedStopList = new ArrayList<StopHistory>();
    private View view;

    //private ArrayList<StopHistory> listStops;
    public SavedStops() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saved_stops, container, false);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("stops").child("stop_id_" + mAuth.getUid());
        ref.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange called.");
                    for(DataSnapshot shot : snapshot.getChildren()) {
                        StopHistoryWithKey data =null;
                        try {
                            data = shot.getValue(StopHistoryWithKey.class);
                        }catch (DatabaseException e){
                            Log.e(TAG, e.getMessage());
                        }
                        if (data!=null){
                            Log.d(TAG,  "retrieved data (StopHistory):" +
                                    "\n\tplaceId: " + data.getPlaceId()
                                    +"\n\tstopName: "+ data.getStopName());
                            data.setKey(shot.getKey());
                            savedStopList.add(data);
                        }

                    }
                    //update list and update UI render
                    final ListView lv = (ListView) view.findViewById(R.id.savedStops);
                    //lv.setAdapter(new ListViewSavedStopAdapter(getActivity(), listStops));
                    Log.d("SavedStops","calling adapter, size of savedStopList:"+savedStopList.size());
                    lv.setAdapter(new ListViewSavedStopAdapter(getActivity(), savedStopList));

                    Log.d(TAG, "log an item:"+lv.getAdapter().getItem(0));
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String placeId = ((StopHistory) lv.getAdapter().getItem(position)).getPlaceId();
                            ((MainActivity) getActivity()).goToStop(placeId);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });


        final ListView lv = (ListView) view.findViewById(R.id.savedStops);
        //lv.setAdapter(new ListViewSavedStopAdapter(getActivity(), listStops));
        Log.d("SavedStops","calling adapter, size of savedStopList:"+savedStopList.size());
        lv.setAdapter(new ListViewSavedStopAdapter(getContext(), savedStopList));

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
