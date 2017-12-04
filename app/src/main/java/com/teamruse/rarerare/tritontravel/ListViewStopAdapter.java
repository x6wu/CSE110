package com.teamruse.rarerare.tritontravel;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.teamruse.rarerare.tritontravel.History.stopsList;

/**
 * Created by JingJing on 11/29/17.
 */

public class ListViewStopAdapter extends BaseAdapter {

        private static ArrayList<StopHistory> listContact;

        private LayoutInflater mInflater;
        private Context mContext;
        public ListViewStopAdapter(Context context, ArrayList<StopHistory> results){
            listContact = results;
            mInflater = LayoutInflater.from(context);
            this.mContext=context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listContact.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return listContact.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.one_row, null);
                holder = new ViewHolder();
                holder.txtname = (TextView) convertView.findViewById(R.id.stopname);
                holder.txttime = (TextView) convertView.findViewById(R.id.time);

                holder.delete = (ImageButton) convertView.findViewById(R.id.delete) ;
                holder.delete.setOnClickListener(new View.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        deleteHist(listContact.get(position).getId());
                        History.stopsList.remove(position);

                        notifyDataSetChanged();
                        if (History.stopsList.isEmpty()) {
                            //History.noHis=(TextView) myView.findViewById(R.id.noHistory);
                            History.noHis.setText("No history");
                        }

                        Toast.makeText(v.getContext(),"removed", Toast.LENGTH_SHORT).show();
                    }
                });


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.placeId=listContact.get(position).getPlaceId();
            holder.txtname.setText(listContact.get(position).getStopName());
            holder.txttime.setText(listContact.get(position).getStopTimeStr());
            holder.id=listContact.get(position).getId();
            return convertView;
        }

        static class ViewHolder{
            TextView txtname, txttime;
            String placeId;
            long id;
            ImageButton delete;
        }
        protected void deleteHist(long id){
            (new StopHistoryBaseHelper(mContext)).deleteItem(id);
        }

}

