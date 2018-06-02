package com.example.pablovilas.reversi.bbdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PartidasBD extends SQLiteOpenHelper {
    private String sqlCreate = "CREATE TABLE Partidas " +
                       "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "alias TEXT, " +
                       "date TEXT, " +
                       "medida TEXT, " +
                       "control TEXT, " +
                       "num_blacks TEXT, " +
                       "num_whites TEXT, " +
                       "total_time TEXT, " +
                       "state TEXT )";

    public PartidasBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Partidas");
        db.execSQL(sqlCreate);
    }
}
