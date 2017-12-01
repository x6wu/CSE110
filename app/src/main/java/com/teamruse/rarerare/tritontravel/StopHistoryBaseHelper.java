package com.teamruse.rarerare.tritontravel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by rarerare on 11/30/17.
 */

public class StopHistoryBaseHelper extends SQLiteOpenHelper {
    private static final String TAG="history_base_helper";
    public static final String DATABASE_NAME = "StopHistory.db";
    private static final String TABLE_NAME="stop_history";
    public static final int DATABASE_VERSION = 1;




    private static final String COL_NAME_ID="id";
    private static final String COL_NAME_NAME="name";
    public static final String COL_NAME_PLACE_ID = "place_id";
    public static final String COL_NAME_TIME = "time";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_NAME_ID + " INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    COL_NAME_NAME + " TEXT," +
                    COL_NAME_PLACE_ID + " TEXT," +
                    COL_NAME_TIME + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    public StopHistoryBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    }
    protected void writeStopHistory(StopHistory sh){
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME_NAME, sh.getStopName());
        values.put(COL_NAME_PLACE_ID, sh.getPlaceId());
        values.put(COL_NAME_TIME, sh.getStopTime());
        long newRowId = db.insert(TABLE_NAME, null, values);
        Log.d(TAG, "written, id="+String.valueOf(newRowId));
    }
    protected ArrayList<StopHistory> getStopHistoryList(){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COL_NAME_ID,
                COL_NAME_NAME,
                COL_NAME_PLACE_ID,
                COL_NAME_TIME

        };
        Cursor cur=db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null

        );
        ArrayList<StopHistory> histList=new ArrayList<StopHistory>();
        while(cur.moveToNext()) {
            cur.getColumnIndexOrThrow(COL_NAME_NAME);

            StopHistory sh=new StopHistory(cur.getString(cur.getColumnIndexOrThrow(COL_NAME_NAME)),
                    cur.getString(cur.getColumnIndexOrThrow(COL_NAME_TIME)),
                    cur.getString(cur.getColumnIndexOrThrow(COL_NAME_PLACE_ID)));
            sh.setId(cur.getInt(cur.getColumnIndexOrThrow(COL_NAME_ID)));
            Log.d(TAG,"id"+cur.getInt(cur.getColumnIndexOrThrow(COL_NAME_ID))+"name"
                            +cur.getString(cur.getColumnIndexOrThrow(COL_NAME_NAME)));
            histList.add(sh);
        }
        return histList;
    }

    protected void deleteTable(){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_NAME, null,null);
    }
    protected void deleteItem(long id){
        String selection = COL_NAME_ID + "=?";
    // Specify arguments in placeholder order.
        Log.d(TAG, "id to delete"+id);
        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_NAME, selection,selectionArgs);
    }
}
