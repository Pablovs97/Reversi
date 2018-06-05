package com.example.pablovilas.reversi.bbdd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PartidasBD extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Partidas";
    private static final int DATABASE_VERSION = 2;

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

    public PartidasBD(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
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

    // Insertar una partida en la base de datos.
    public void insertPartida(PartidasClass partida){
        SQLiteDatabase db = this.getWritableDatabase();

        if(db != null){
            db.execSQL("INSERT INTO Partidas (alias, date, medida, control, num_blacks, num_whites, total_time, state) VALUES (" +
                    "'" + partida.getAlias() +"', " +
                    "'" + partida.getDate() + "', " +
                    "'" + partida.getMedida() + "', " +
                    "'" + partida.getControl() + "', " +
                    "'" + partida.getNum_blacks() + "', " +
                    "'" + partida.getNum_whites() + "', " +
                    "'" + partida.getTotal_time() + "', " +
                    "'" + partida.getState() + "')");
        }
    }

    // Obtener todas las partidas guardadas.
    public List<PartidasClass> allPartidas() {
        List<PartidasClass> partidas = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PartidasClass partida;

        if (cursor.moveToFirst()) {
            do {
                partida = new PartidasClass();
                partida.setAlias(cursor.getString(1));
                partida.setDate(cursor.getString(2));
                partida.setMedida(cursor.getString(3));
                partida.setControl(cursor.getString(4));
                partida.setNum_blacks(cursor.getString(5));
                partida.setNum_whites(cursor.getString(6));
                partida.setTotal_time(cursor.getString(7));
                partida.setState(cursor.getString(8));
                partidas.add(partida);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return partidas;
    }

    // Conseguir la partida seleccionada.
    public PartidasClass getSelectedPartida(int position) {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PartidasClass partida = new PartidasClass();

        if (cursor.moveToPosition(position)) {
            partida.setAlias(cursor.getString(1));
            partida.setDate(cursor.getString(2));
            partida.setMedida(cursor.getString(3));
            partida.setControl(cursor.getString(4));
            partida.setNum_blacks(cursor.getString(5));
            partida.setNum_whites(cursor.getString(6));
            partida.setTotal_time(cursor.getString(7));
            partida.setState(cursor.getString(8));
        }

        cursor.close();
        return partida;
    }

    // Elimina una partida en la base de datos.
    public void deletePartida(List<Integer> positions){
        Collections.sort(positions, Collections.reverseOrder());
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        for(int position: positions){
            if (cursor.moveToPosition(position)) {
                Log.d("pos", String.valueOf(position));
                Log.d("date", cursor.getString(2));
                db.delete(TABLE_NAME, "date=?", new String[]{cursor.getString(2)});
            }
        }
        cursor.close();
    }
}
