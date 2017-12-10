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

public class SavedRoutes extends Fragment {
    private static final String TAG="SavedRoutes_tag";
    private FirebaseAuth mAuth;
    private View view;
    private ListView lv;
    private  ArrayList<StopHistory> routesList;


    public SavedRoutes() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saved_routes, container, false);
        mAuth = FirebaseAuth.getInstance();
        if (routesList == null) {
            routesList = new ArrayList<>();
        }

        lv = (ListView) view.findViewById(R.id.savedRoutes);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("routes").child("route_id_" + mAuth.getUid());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange called.");
                        for(DataSnapshot shot : snapshot.getChildren()) {
                            Log.d(TAG, "a shot");
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

                                routesList.add(data);
                            }

                        }
                        //update list and update UI render

                        //lv.setAdapter(new ListViewSavedStopAdapter(getActivity(), listStops));
                        Log.d("SavedStops","calling adapter, size of savedStopList:"+routesList.size());
                        lv.setAdapter(new ListViewSavedRouteAdapter(getContext(), routesList));

                        Log.d(TAG, "log an item:"+lv.getAdapter().getItem(0));
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                StopHistory route=(StopHistory) lv.getAdapter().getItem(position);
                                String[] originDestName=route.getStopName().split(" -> ");
                                String[] originDestId=route.getPlaceId().split(" -> ");
                                Log.d(TAG,"/"+originDestId[0]+"/"+originDestId[1]+"/");

                                ((MainActivity)getActivity()).goToRoute(originDestId[0], originDestId[1]);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        Log.d(TAG, "onCreateView");

        lv.setAdapter(new ListViewSavedStopAdapter(getActivity(), routesList));
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
}
