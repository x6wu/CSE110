package com.teamruse.rarerare.tritontravel;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//import static com.teamruse.rarerare.tritontravel.History.stopsList;

/**
 * Created by JingJing on 12/6/17.
 */

public class ListViewSavedRouteAdapter extends ListViewSavedAdapter {

    public ListViewSavedRouteAdapter(Context context, ArrayList<StopHistory> results){
        super( context, results);
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.one_row_save, null);
            holder = new ViewHolder();
            holder.txtname = (TextView) convertView.findViewById(R.id.savestopname);


            holder.delete = (ImageButton) convertView.findViewById(R.id.delete) ;
            holder.delete.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {

                    //TODO 
                    //SavedStops.stopsList.remove(position);


                    StopHistoryWithKey saveRoute=(StopHistoryWithKey)(listContact.get(position));

                    ((MainActivity)mContext).deleteRoute(saveRoute.getKey());
                    listContact.remove(position);
                    notifyDataSetChanged();
                   /* if (History.stopsList.isEmpty()) {
                        //History.noHis=(TextView) myView.findViewById(R.id.noHistory);
                        History.noHis.setText("No history");
                    }*/

                    Toast.makeText(v.getContext(),"removed", Toast.LENGTH_SHORT).show();
                }
            });


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.placeId=listContact.get(position).getPlaceId();
        if (!listContact.get(position).getTag().isEmpty())
            holder.txtname.setText(listContact.get(position).getTag());
        else
            holder.txtname.setText(listContact.get(position).getStopName());

        holder.id=listContact.get(position).getId();
        return convertView;
    }

}

