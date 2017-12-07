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

import java.util.ArrayList;

/**
 * Created by JingJing on 12/7/17.
 */

public class SavedStops extends Fragment {

    private static final String TAG="SavedStops_tag";
    public static ArrayList<StopHistory> stopsList;
    private ArrayList<StopHistory> listStops;
    public SavedStops() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_stops, container, false);

        if (stopsList == null) {
            stopsList = new ArrayList<>();
        }

        listStops = stopsList;

        stopsList.add(new StopHistory("ss","tt"));
        //stopsList.add(new StopHistory("ff","pp"));
        //stopsList.add(new StopHistory("ff","pp"));
        //stopsList.add(new StopHistory("ff","pp"));


        final ListView lv = (ListView) view.findViewById(R.id.savedStops);
        //lv.setAdapter(new ListViewSavedAdapter(getActivity(), listStops));
        lv.setAdapter(new ListViewSavedAdapter(getContext(), listStops));

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
