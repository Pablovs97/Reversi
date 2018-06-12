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
import com.example.pablovilas.reversi.bbdd.PartidasClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Resultados extends AppCompatActivity{

    private EditText dia_hora, log, email;
    private boolean added;

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

        if(savedInstanceState != null){
            added = savedInstanceState.getBoolean("added", false);
        }

        if(!added){
            insertPartida(intent, dateFormat.format(date));
        }
    }

    // Inserta partida en la base de datos
    public void insertPartida(Intent intent, String date){
        PartidasBD pdb = new PartidasBD(this);

        PartidasClass partida = new PartidasClass();

        partida.setAlias(intent.getStringExtra("alias"));
        partida.setDate(date);
        partida.setMedida(intent.getStringExtra("medida"));
        partida.setControl(intent.getStringExtra("control"));
        partida.setNum_blacks(intent.getStringExtra("num_blacks"));
        partida.setNum_whites(intent.getStringExtra("num_whites"));
        partida.setTotal_time(intent.getStringExtra("total_time"));
        partida.setState(intent.getStringExtra("state"));

        pdb.insertPartida(partida);
        added = true;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("added", added);
    }
}
