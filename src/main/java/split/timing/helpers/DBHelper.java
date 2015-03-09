package split.timing.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import split.timing.items.Competition;
import split.timing.items.Group;
import split.timing.items.Groupmember;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;

/**
 * Created by Antec on 16.01.14.
 */
public class DBHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/"+ACP.getContext().getPackageName()+"/databases/";

    private static String DB_NAME = "timing.db";

    private static int DB_VERSION = 1;

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private ContentValues values;

    public DBHelper() {

        super(ACP.getContext(), DB_NAME, null, DB_VERSION);
        this.myContext = ACP.getContext();

        openDataBase();
        myDataBase = getWritableDatabase();
    }

    public Cursor select(String query){
        return myDataBase.rawQuery(query,null);
    }

    public void insert(String table, ContentValues values) throws SQLException {
        myDataBase.insert(table, null, values);
    }

    public void insert(Competition c) throws  SQLException{
        values = new ContentValues();
        values.put("name",c.getName());
        values.put("date",c.getDate());
        values.put("location",c.getLocation());
        values.put("finished",c.isFinished());
        myDataBase.insert("Competition",null,values);
    }

    public void insert(Group g) throws SQLException{
        values = new ContentValues();
        values.put("name",g.getName());
        myDataBase.insert("Groups",null,values);
    }

    public void insert(Sportsmen s) throws SQLException{
        values = new ContentValues();
        values.put("name",s.getName());
        values.put("lastname",s.getLastName());
        values.put("birthday",s.getBirthday());
        values.put("year",s.getYear());
        values.put("age",s.getAge());
        values.put("club",s.getClub());
        values.put("federation",s.getFederation());
        myDataBase.insert("Sportsmen",null,values);
    }

    public void insert(Startgroup sg) throws SQLException{
        values = new ContentValues();
        values.put("name",sg.getName());
        values.put("starthour",sg.getStartHour());
        values.put("startminute",sg.getStartMinute());
        values.put("startsecond",sg.getStartSecond());
        values.put("mode",sg.getMode());
        values.put("distance",sg.getDistance());
        values.put("interval",sg.getInterval());
        values.put("startnum",sg.getStartNum());
        myDataBase.insert("Startgroup",null,values);

    }

    public void insert (Groupmember gm) throws SQLException{
        values = new ContentValues();
        values.put("groupId",gm.getGroupId());
        values.put("sportsmenId",gm.getSportsmenId());
        myDataBase.insert("Groupmember",null,values);
    }

    public void insert (Startlist sl) throws SQLException{
        values = new ContentValues();
        values.put("number",sl.getNumber());
        values.put("jersey",sl.isJersey());
        values.put("compId",sl.getCompId());
        values.put("startId",sl.getStartId());
        values.put("sportsmenId",sl.getSportsmenId());
        values.put("startposition",sl.getStartposition());
        myDataBase.insert("Startlist",null,values);
    }

    public void delete(String table, String where) throws SQLException{
        myDataBase.delete(table, where, null);
    }

    public void update(String table, ContentValues values, String where){
        myDataBase.update(table, values, where, null);
    }

    public void sqlCommand(String command){
        myDataBase.execSQL(command);

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        this.getReadableDatabase();

        try {

            copyDataBase();

        } catch (IOException e) {

            throw new Error("Error copying database");

        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;
        boolean check;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            check = true;
        } catch (SQLiteCantOpenDatabaseException sql){
            check = false;
        } catch(SQLiteException e){
            check = false;
        }

        if(checkDB != null){

            checkDB.close();
        }

        return check;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getResources().getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        if(checkDataBase()){
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }else{
            try {
                createDataBase();
            }catch (IOException e){
            }
        }

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            copyDataBase();
        }catch(IOException e){
            Log.e("Database Upgrade", "failed");
        }

        Toast.makeText(myContext, "Database Updgrade was succesful", Toast.LENGTH_SHORT).show();
        Log.e("Database Upgrade", "succesful");
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}

