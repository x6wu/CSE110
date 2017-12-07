package com.teamruse.rarerare.tritontravel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by JingJing on 12/7/17.
 */

public class SavedRoutes extends Fragment {
    private static final String TAG="SavedRoutes_tag";
    ListView lv;
    public static ArrayList<StopHistory> routesList;
    private ArrayList<StopHistory> listRoutes;

    public SavedRoutes() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_routes, container, false);

        if (routesList == null) {
            routesList = new ArrayList<>();
        }

        listRoutes = routesList;


        listRoutes.add(new StopHistory("ff","pp"));
        lv = (ListView) view.findViewById(R.id.savedRoutes);
        lv.setAdapter(new ListViewSavedAdapter(getActivity(), listRoutes));


        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeId = ((StopHistory) lv.getAdapter().getItem(position)).getPlaceId();
                ((MainActivity) getActivity()).goToStop(placeId);


            }
        });*/

        Log.d(TAG, "onCreateView");
        return view;
    }
    @Override
    public void onResume() {

        super.onResume();
        Log.d(TAG, "resume");
    }
}
