package org.tyaa.android.sqlitedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    /**
     * Tag для Log.d
     */
    private final static String TAG = "===== MySQLite";
    /**
     * Название Базы Данных
     */
    private final static String dbName = "MyDbOne";
    /**
     * Версия Базы Данных
     */
    private final static int dbVersion = 1;
    /**
     * Название таблицы "Товары"
     */
    public final static String tblNameProducts = "Products";

    public MySQLiteOpenHelper(Context context)
    {
        super(context, MySQLiteOpenHelper.dbName,
                null, MySQLiteOpenHelper.dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate: " + db.getPath());

        String query = "CREATE TABLE Products("
            + "id integer not null primary key autoincrement, "
            + "name text, "
            + "price real, "
            + "weight integer)";

        db.execSQL(query);

        ContentValues row = new ContentValues();
        row.put("name", "Snickers");
        row.put("price", 12.50);
        row.put("weight", 45);
        db.insert(MySQLiteOpenHelper.tblNameProducts,
                null, row);
        row = new ContentValues();
        row.put("name", "Mars");
        row.put("price", 13.90);
        row.put("weight", 50);
        db.insert(MySQLiteOpenHelper.tblNameProducts,
                null, row);
        row = new ContentValues();
        row.put("name", "Bounty");
        row.put("price", 15.30);
        row.put("weight", 60);
        db.insert(MySQLiteOpenHelper.tblNameProducts,
            null, row);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(
            TAG
            , "onUpgrade: " + db.getPath()
                + "; oldVersion: " + oldVersion
                + "; newVersion: " + newVersion
        );
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        Log.d(TAG, "onOpen: " + db.getPath());
    }

    @Override
    public void onDowngrade (SQLiteDatabase db,
                             int oldVersion, int newVersion)
    {
        Log.d(
            TAG, "onDowngrade: "
                + db.getPath()
                + "; oldVersion: " + oldVersion
                + "; newVersion: " + newVersion
        );
    }
}
