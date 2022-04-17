package org.eim_systems.privatetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class PersistenceManager {
    private static PersistenceManager persistenceManager;
    private static Context context;
    private static TrackReaderDbHelper dbHelper;
    private static SQLiteDatabase db;

    private static final String SQL_CREATE_TRACKS =
            "CREATE TABLE " + TrackEntries.TABLE_NAME + " (" +
                    TrackEntries._ID + " INTEGER PRIMARY KEY," +
                    TrackEntries.COLUMN_NAME_NAME + " TEXT," +
                    TrackEntries.COLUMN_NAME_DATE + " INTEGER,"+
                    TrackEntries.COLUMN_NAME_DISTANCE + " REAL,"+
                    TrackEntries.COLUMN_NAME_UP + " REAL,"+
                    TrackEntries.COLUMN_NAME_DOWN + " REAL,"+
                    TrackEntries.COLUMN_NAME_TIME +" INTEGER)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TrackEntries.TABLE_NAME;
    private final String sortOrder = TrackEntries.COLUMN_NAME_DATE + "DESC";
    private PersistenceManager(){
    }
    public static PersistenceManager getInstance(){
        if(persistenceManager==null)throw new RuntimeException("call init() first");
        return persistenceManager;
    }
    public static PersistenceManager init(Context cnt){
        if(persistenceManager!=null)throw new RuntimeException("Already instantiated");
        context = cnt;
        dbHelper = new TrackReaderDbHelper(context);
        db = dbHelper.getWritableDatabase();
        persistenceManager = new PersistenceManager();
        return persistenceManager;

    }
    public static class TrackEntries implements BaseColumns {
        public static final String TABLE_NAME = "tracks";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_UP = "up";
        public static final String COLUMN_NAME_DOWN = "down";
        public static final String COLUMN_NAME_TIME = "time";
    }
    public static class TrackReaderDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "TrackReader.db";
        private final String[] projection = {
                BaseColumns._ID,
                TrackEntries.COLUMN_NAME_NAME,
                TrackEntries.COLUMN_NAME_DATE,
                TrackEntries.COLUMN_NAME_DISTANCE,
                TrackEntries.COLUMN_NAME_UP,
                TrackEntries.COLUMN_NAME_DOWN
                //TrackEntries.COLUMN_NAME_TIME
        };


        public TrackReaderDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TRACKS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
    //todo time?
    public void insertRow(MetaData metaData){
        ContentValues values = new ContentValues();
        values.put(TrackEntries.COLUMN_NAME_NAME, metaData.getName());
        values.put(TrackEntries.COLUMN_NAME_DATE, metaData.getDate().getTime());
        values.put(TrackEntries.COLUMN_NAME_DISTANCE, metaData.getDistance());
        values.put(TrackEntries.COLUMN_NAME_UP, metaData.getUp());
        values.put(TrackEntries.COLUMN_NAME_DOWN, metaData.getDown());
        values.put(TrackEntries.COLUMN_NAME_TIME, -1);
        long newRowId = db.insert(TrackEntries.TABLE_NAME, null, values);
    }
    public Cursor getCursor(){
        Cursor cursor = db.query(
                TrackEntries.TABLE_NAME,
                dbHelper.projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        return cursor;
    }
    public List<MetaData> getTable(){
        Cursor cursor = getCursor();
        List<MetaData> list = new ArrayList<>();
        while (cursor.moveToNext()){
            int nameIndex = cursor.getColumnIndexOrThrow(TrackEntries.COLUMN_NAME_NAME);
            String name = cursor.getString(nameIndex);
            int dateIndex = cursor.getColumnIndexOrThrow(TrackEntries.COLUMN_NAME_DATE);
            long date = cursor.getLong(dateIndex);
            int distanceIndex = cursor.getColumnIndexOrThrow(TrackEntries.COLUMN_NAME_DISTANCE);
            double distance = cursor.getDouble(distanceIndex);
            int upIndex = cursor.getColumnIndexOrThrow(TrackEntries.COLUMN_NAME_UP);
            double up = cursor.getDouble(upIndex);
            int downIndex = cursor.getColumnIndexOrThrow(TrackEntries.COLUMN_NAME_DOWN);
            double down = cursor.getDouble(downIndex);
            int timeIndex = cursor.getColumnIndexOrThrow(TrackEntries.COLUMN_NAME_TIME);
            long time = cursor.getLong(timeIndex);
            MetaData metaData = new MetaData(name, new Date(date), distance, up, down, new Date(timeIndex));
            list.add(metaData);
        }
        cursor.close();
        return list;
    }

}
