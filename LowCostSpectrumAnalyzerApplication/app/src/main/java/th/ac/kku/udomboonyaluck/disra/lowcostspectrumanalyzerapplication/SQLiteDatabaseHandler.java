package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PlayersDB";
    private static final String TABLE_NAME = "Players";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_POSITION = "position";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_N = "n";
    private static final String KEY_X1 = "x1";
    private static final String KEY_Y1 = "y1";
    private static final String KEY_X2 = "x2";
    private static final String KEY_Y2 = "y2";
    private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_POSITION,
            KEY_HEIGHT, KEY_N ,KEY_X1,KEY_Y1,KEY_X2,KEY_Y2};

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATION_TABLE = "CREATE TABLE Players ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
                + "position TEXT, " + "height INTEGER, "+"n INTEGER," +"x1 INTEGER, " + "y1 INTEGER, "
                + "x2 INTEGER, " + "y2 INTEGER )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(Data data) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?",
                new String[] { String.valueOf(data.getId()) });
        db.close();

    }

    public Data getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Data data = new Data();
        data.setId(Integer.parseInt(cursor.getString(0)));
        data.setName(cursor.getString(1));
        data.setPosition(cursor.getString(2));
        data.setHeight(Integer.parseInt(cursor.getString(3)));
        data.setN(Integer.parseInt(cursor.getString(4)));
        data.setX1(Integer.parseInt(cursor.getString(5)));
        data.setY1(Integer.parseInt(cursor.getString(6)));
        data.setX2(Integer.parseInt(cursor.getString(7)));
        data.setY2(Integer.parseInt(cursor.getString(8)));

        return data;
    }

    public List<Data> allDatas() {

        List<Data> datas = new LinkedList<Data>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Data data = null;

        if (cursor.moveToFirst()) {
            do {
                data = new Data();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setName(cursor.getString(1));
                data.setPosition(cursor.getString(2));
                data.setHeight(Integer.parseInt(cursor.getString(3)));
                data.setN(Integer.parseInt(cursor.getString(4)));
                data.setX1(Integer.parseInt(cursor.getString(5)));
                data.setY1(Integer.parseInt(cursor.getString(6)));
                data.setX2(Integer.parseInt(cursor.getString(7)));
                data.setY2(Integer.parseInt(cursor.getString(8)));
                datas.add(data);
            } while (cursor.moveToNext());
        }

        return datas;
    }

    public void addData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName());
        values.put(KEY_POSITION, data.getPosition());
        values.put(KEY_HEIGHT, data.getHeight());
        values.put(KEY_N, data.getN());
        values.put(KEY_X1,data.getX1());
        values.put(KEY_Y1,data.getY1());
        values.put(KEY_X2,data.getX2());
        values.put(KEY_Y2,data.getY2());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updateData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName());
        values.put(KEY_POSITION, data.getPosition());
        values.put(KEY_HEIGHT, data.getHeight());
        values.put(KEY_N, data.getN());
        values.put(KEY_X1, data.getX1());
        values.put(KEY_Y1, data.getY1());
        values.put(KEY_X2, data.getX2());
        values.put(KEY_Y2,data.getY2());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(data.getId()) });

        db.close();

        return i;
    }

    public void removeAllDatas(){
        String q = "DELETE FROM " + TABLE_NAME;
        getWritableDatabase().execSQL(q);

    }

}