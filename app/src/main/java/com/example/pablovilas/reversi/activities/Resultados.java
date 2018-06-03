package com.example.pablovilas.reversi.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.pablovilas.reversi.activities.listado_partidas.AccessBDActivity;
import com.example.pablovilas.reversi.bbdd.PartidasBD;
import com.example.pablovilas.reversi.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Resultados extends AppCompatActivity{

    private EditText dia_hora, log, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultados);

        dia_hora = (EditText) findViewById(R.id.dia_hora_et);
        log = (EditText) findViewById(R.id.log_et);
        email = (EditText) findViewById(R.id.email_et);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("es", "ES"));
        Date date = new Date();
        dia_hora.setText(dateFormat.format(date));

        Intent intent = getIntent();
        log.setText(intent.getStringExtra("Log"));

        PartidasBD pdb = new PartidasBD(this, "Partidas", null, 2);
        SQLiteDatabase db = pdb.getWritableDatabase();
        if(db != null){
            String alias = intent.getStringExtra("alias");
            String medida = intent.getStringExtra("medida");
            String control = intent.getStringExtra("control");
            String num_blacks = intent.getStringExtra("num_blacks");
            String num_whites = intent.getStringExtra("num_whites");
            String total_time = intent.getStringExtra("total_time");
            String state = intent.getStringExtra("state");

            db.execSQL("INSERT INTO Partidas (alias, date, medida, control, num_blacks, num_whites, total_time, state) VALUES (" +
                    "'" + alias +"', " +
                    "'" + dateFormat.format(date) + "', " +
                    "'" + medida + "', " +
                    "'" + control + "', " +
                    "'" + num_blacks + "', " +
                    "'" + num_whites + "', " +
                    "'" + total_time + "', " +
                    "'" + state + "')");

            db.close();
        }


    }

    public void sendMail(View view) {
        Intent in = new Intent(Intent.ACTION_SEND);
        in.setType("plain/text");
        in.putExtra(Intent.EXTRA_EMAIL, new String[] {email.getText().toString()});
        in.putExtra(Intent.EXTRA_SUBJECT, "Log - " + dia_hora.getText().toString());
        in.putExtra(Intent.EXTRA_TEXT, log.getText().toString());
        startActivity(in);
    }

    public void nuevaPartida(View view){
        finish();
    }

    public void salir(View view){
        finishAffinity();
    }

    public void consultarPartidas(View view) {
        finish();
        startActivity(new Intent(this, AccessBDActivity.class));
    }
}
