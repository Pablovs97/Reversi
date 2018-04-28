package com.example.pablovilas.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Resultados extends AppCompatActivity{

    private EditText dia_hora, log, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultados);

        dia_hora = (EditText) findViewById(R.id.dia_hora_et);
        log = (EditText) findViewById(R.id.log_et);
        email = (EditText) findViewById(R.id.email_et);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        dia_hora.setText(dateFormat.format(date));

        Intent intent = getIntent();
        log.setText(intent.getStringExtra("Log"));
    }

    public void sendMail(View view) {
        Intent in = new Intent(Intent.ACTION_SEND);
        in.setType("plain/text");
        in.putExtra(Intent.EXTRA_EMAIL, new String[] {email.getText().toString()});
        in.putExtra(Intent.EXTRA_SUBJECT, "Log - " + dia_hora.getText().toString());
        in.putExtra(Intent.EXTRA_TEXT, log.getText().toString());
        startActivity(in);
    }
}
