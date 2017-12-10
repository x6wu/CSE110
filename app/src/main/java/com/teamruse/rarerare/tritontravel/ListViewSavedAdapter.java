package com.teamruse.rarerare.tritontravel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 * Ruoyu Xu extracted Abstract class of Zijing's adapter
 */

abstract class ListViewSavedAdapter extends BaseAdapter {
    protected ArrayList<StopHistory> listContact;
    protected LayoutInflater mInflater;
    protected Context mContext;

    ListViewSavedAdapter( Context context, ArrayList<StopHistory> results) {
        this.mContext=context;
        mInflater = LayoutInflater.from(context);
        listContact = results;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listContact.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        Log.d("lvSavedStopAdapter", "arg0:" + arg0 + " listContact.size="+listContact.size());
        if(listContact.size()==0){
            return null;
        }
        return listContact.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }



    static class ViewHolder{
        TextView txtname;
        String placeId;
        long id;
        ImageButton delete;
    }
}
