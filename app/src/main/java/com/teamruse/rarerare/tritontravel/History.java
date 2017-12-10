package com.teamruse.rarerare.tritontravel;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by JingJing on 11/13/17.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class History extends Fragment {

    View myView;
    public static ArrayList<StopHistory> stopsList;
    private ArrayList<StopHistory> listStops;
    private Fragment currFrag=this;
    public static TextView noHis;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.history, container, false);
        defineButtons(myView);
        /*if ( History.stopsList == null) {
            History.stopsList = new ArrayList<>();
        }*/

        stopsList= new StopHistoryBaseHelper(getContext()).getStopHistoryList();
        listStops = stopsList;

        noHis = (TextView) myView.findViewById(R.id.noHistory);

        if (stopsList.isEmpty()) {
            noHis.setText("No history");
        }
        else {
            noHis.setText("");
        }

                //getStopHistory();

        final ListView lv = (ListView)myView.findViewById(R.id.stopListView);
        lv.setAdapter(new ListViewStopAdapter(getActivity(), listStops));

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String placeId=((StopHistory)lv.getAdapter().getItem(position)).getPlaceId();
               ((MainActivity)getActivity()).goToStop(placeId);


           }
       });


        return myView;
    }



    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.clear).setOnClickListener(buttonClickListener);
        //view.findViewById(R.id.delete).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    //startActivity(new Intent(login.this, signup.class));
                    ((MainActivity)getActivity()).switchFrag(R.id.back);
                    break;

                case R.id.clear:
                    //startActivity(new Intent(login.this, signup.class));
                    clearHist();
                    break;

            }
        }

    };

    public void clearHist() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Clear all history?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                (new StopHistoryBaseHelper(currFrag.getActivity())).deleteTable();
                stopsList.clear();
                listStops = stopsList;
                ListView lv = (ListView)myView.findViewById(R.id.stopListView);
                lv.setAdapter(new ListViewStopAdapter(getActivity(), listStops));
                noHis.setText("No history");
                Toast.makeText(getContext(),"Cleared", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Nvm", Toast.LENGTH_SHORT).show();
            }
        });

        //alert.show();

        AlertDialog dialog = alert.create();
        dialog.show();

        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if(b != null) {
            b.setTextColor(Color.parseColor("#064264"));

        }


    }


}
