package in.co.ashclan.mirchithunder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.CheckPointModel;

public class DataBaseHelper extends SQLiteOpenHelper {

    //database helper
    public static final String DATABASE_NAME = "database.db";

    //checkpoint Table
    public static final String TABLE_CHECKPT = "check_point_table";
    public static final String CKPT_COl_1 = "ID";
    public static final String CKPT_COL_2 = "puid";
    public static final String CKPT_COL_3 = "batch";
    public static final String CKPT_COL_4 = "start_time";
    public static final String CKPT_COL_5 = "checkpt_time";
    public static final String CKPT_COL_6 = "end_time";
    public static final String CKPT_COL_7 = "startptImage";
    public static final String CKPT_COL_8 = "checkptImage";
    public static final String CKPT_COL_9 = "endptImage";

    public static final String CREATE_TABLE_CHECKPT =
            "CREATE TABLE " + TABLE_CHECKPT + "("
                    + CKPT_COl_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CKPT_COL_2 + " TEXT,"
                    + CKPT_COL_3 + " TEXT,"
                    + CKPT_COL_4 + " TEXT,"
                    + CKPT_COL_5 + " TEXT,"
                    + CKPT_COL_6 + " TEXT,"
                    + CKPT_COL_7 + " TEXT,"
                    + CKPT_COL_8 + " TEXT,"
                    + CKPT_COL_9 + " TEXT"
                    + ")";

    //Batch Master
    public static final String TABLE_BATCH = "batch_table";
    public static final String BATCH_COl_1 = "ID";
    public static final String BATCH_COL_2 = "batchName";
    public static final String BATCH_COL_3 = "start_time";
    public static final String BATCH_COL_4 = "status";

    public static final String CREATE_TABLE_BATCH =
            "CREATE TABLE " + TABLE_BATCH + "("
                    + BATCH_COl_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + BATCH_COL_2 + " TEXT,"
                    + BATCH_COL_3 + " TEXT,"
                    + BATCH_COL_4 + " TEXT"
                    + ")";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create tables TABLE BATCH
        db.execSQL(CREATE_TABLE_BATCH);
        //CREATE TABLE CHECK POINT
        db.execSQL(CREATE_TABLE_CHECKPT);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKPT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATCH);

        // Create tables again
        onCreate(db);
    }

    /***************************************************************************************/
    //First Timely Add batches
    public void onBatchAdd(BatchModel details){
        ContentValues values = new ContentValues();
        values.put(BATCH_COL_2,details.getBatchName());
        values.put(BATCH_COL_4,details.getBatchstatus());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BATCH,null,values);
        Log.e("-->DatabaseOperation","Values Inserted into Batch");
        db.close();
    }
    //Check if there Batch Exist
    public boolean checkBatchExist() {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQL = String.format("SELECT * FROM batch_table");
        cursor = db.rawQuery(SQL,null);
        if(cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }
    //get All Batches
    public List<BatchModel> getAllBatchs() {
        List<BatchModel> reg = new ArrayList<BatchModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM batch_table WHERE status = 'NotAllotted'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        {
            while (cursor.moveToNext()) {
                BatchModel getset = new BatchModel();

                getset.setBatchid(cursor.getString(0));
                getset.setBatchName(cursor.getString(1));
                getset.setBatchStartTime(cursor.getString(2));
                getset.setBatchstatus(cursor.getString(3));

                reg.add(getset);
            }
            cursor.close();
        }
        return reg;
    }
    //Update Batches start Time
    public void updateBatchTime(String batchStarttime,String batchId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE batch_table SET start_time = '"+batchStarttime+"',"+"status = '"+"Allotted"+"' WHERE ID = '"+batchId+"'");
        db.execSQL(query);
    }
    /***************************************************************************************/

    public void onCheckpointAdd(CheckPointModel details)
    {
        ContentValues values = new ContentValues();

        values.put(CKPT_COL_2,details.getPuid());
        values.put(CKPT_COL_3,details.getBatch());
        values.put(CKPT_COL_4,details.getStart_time());
        values.put(CKPT_COL_5,details.getCheckpoint_time());
        values.put(CKPT_COL_6,details.getEndPointTime());
        values.put(CKPT_COL_7,details.getStartptImage());
        values.put(CKPT_COL_8,details.getCheckptImage());
        values.put(CKPT_COL_9,details.getEndptImage());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CHECKPT,null,values);
        Log.e("-->DatabaseOperation","Values Inserted into Table CheckPoint");
        db.close();
    }

    public boolean isIdAvailable(String puid)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM check_point_table WHERE puid ='%s';",puid);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
            return false ;
        }
        cursor.close();
        return true;
    }

    public void updateCheckPointTime(String currentTime,String puid)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE check_point_table SET checkpt_time = '"+currentTime+"' WHERE puid = '"+puid+"'");
        db.execSQL(query);
    }

    public void updateEndPointTime(String endpointTime,String puid)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE check_point_table SET end_time = '"+endpointTime+"' WHERE puid = '"+puid+"'");
        db.execSQL(query);


    }

    public void updateStartPtImage(String uri,String puid)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE check_point_table SET startptImage = '"+uri+"' WHERE puid = '"+puid+"'");
        db.execSQL(query);
        Log.e("-->DatabaseOperation","Values Updated");

    }
    public void updateCheckPtImage(String uri,String puid)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE check_point_table SET checkptImage = '"+uri+"' WHERE puid = '"+puid+"'");
        db.execSQL(query);
    }
    public void updateEndPtImage(String uri,String puid)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE check_point_table SET endptImage = '"+uri+"' WHERE puid = '"+puid+"'");
        db.execSQL(query);
    }
    //get All Batches
    public ArrayList<CheckPointModel> getAllParticipant() {
        ArrayList<CheckPointModel> reg = new ArrayList<CheckPointModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM check_point_table ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        {
            while (cursor.moveToNext()) {
                CheckPointModel getset = new CheckPointModel();

                getset.setId(cursor.getString(0));
                getset.setPuid(cursor.getString(1));
                getset.setBatch(cursor.getString(2));
                getset.setStart_time(cursor.getString(3));
                getset.setCheckpoint_time(cursor.getString(4));
                getset.setEndPointTime(cursor.getString(5));
                getset.setStartptImage(cursor.getString(6));
                getset.setCheckptImage(cursor.getString(7));
                getset.setEndptImage(cursor.getString(8));

                reg.add(getset);
            }
            cursor.close();
        }
        return reg;
    }

    public void getAllParticipant(CheckPointModel model) {

        // Select All Query
        String selectQuery = "SELECT  * FROM check_point_table ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        {
            while (cursor.moveToNext()) {

                CheckPointModel getset = new CheckPointModel();

                getset.setId(cursor.getString(0));
                getset.setPuid(cursor.getString(1));
                getset.setBatch(cursor.getString(2));
                getset.setStart_time(cursor.getString(3));
                getset.setCheckpoint_time(cursor.getString(4));
                getset.setEndPointTime(cursor.getString(5));
                getset.setStartptImage(cursor.getString(6));
                getset.setCheckptImage(cursor.getString(7));
                getset.setEndptImage(cursor.getString(8));

            }
            cursor.close();
        }
    }


}
