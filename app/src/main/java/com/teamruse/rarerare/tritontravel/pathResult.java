package com.teamruse.rarerare.tritontravel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/**
 * Created by Shuyuan Ma on 2017/12/8.
 */

public class pathResult implements Parcelable {
    /*
     * Shuyuan Ma @Dec 8th
     * Implement Parcelable for bottom sheet.
     * I will only parcel the necessary data for bottom sheet display in the following order:
     *      String Duration
     *      String[] (walking or bus short_name)
     */
    //public LatLng mStartLocation;
    //public LatLng mEndLocation;
    public String mDuration;
    public ArrayList<String> segments;

    public pathResult(Path path) {
        //mStartLocation = path.getStartLocation();
        //mEndLocation = path.getEndLocation();
        mDuration = path.getDuration();
        segments = new ArrayList<String>();

        ArrayList<PathSegment> recPathSegments = path.getPathSegments();
        for (int i = 0; i < recPathSegments.size(); ++i) {
            PathSegment currSegment = recPathSegments.get(i);
            switch (currSegment.getTravelMode()) {
                case BUS:
                    BusSegment currbusSegment = (BusSegment)currSegment;
                    segments.add(currbusSegment.getBusName());
                    break;
                case SHUTTLE:
                    ShuttleSegment currshuttleSegment = (ShuttleSegment)currSegment;
                    segments.add(currshuttleSegment.getShuttleHeadsign());
                    break;
                case WALKING:
                    segments.add("Walking");
                    break;
            }
        }
    }

    protected pathResult(Parcel in) {
        //mStartLocation = in.readParcelable(LatLng.class.getClassLoader());
        //mEndLocation = in.readParcelable(LatLng.class.getClassLoader());
        mDuration = in.readString();
        segments = in.createStringArrayList();
    }

    public static final Creator<pathResult> CREATOR = new Creator<pathResult>() {
        @Override
        public pathResult createFromParcel(Parcel in) {
            return new pathResult(in);
        }

        @Override
        public pathResult[] newArray(int size) {
            return new pathResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //parcel.writeParcelable(mStartLocation,i);
        //parcel.writeParcelable(mEndLocation,i);
        parcel.writeString(mDuration);
        parcel.writeStringList(segments);
    }


}
