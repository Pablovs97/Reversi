package com.example.pablovilas.reversi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class Configuracion extends AppCompatActivity {

    private EditText alias;
    private SeekBar seekBar;
    private Spinner spinner;
    private TextView value;
    private TextView time_tv;
    private CheckBox checkBox;
    private int time = 180;
    private final int[] values = {4, 6, 8, 10, 12};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion);

        alias = (EditText) findViewById(R.id.alias_et);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        spinner = (Spinner) findViewById(R.id.spinner1);
        value = (TextView) findViewById(R.id.medida_v);
        time_tv = (TextView) findViewById(R.id.tiempo);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        time_tv.setText(String.format(getString(R.string.tiempo_segundos), String.valueOf(time)));
        seekBar.setProgress(2);
        seekBar.setMax(values.length - 1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText(String.valueOf(values[progress]));
                time = 60*(progress+1);
                time_tv.setText(String.format(getString(R.string.tiempo_segundos), String.valueOf(time)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void comenzar(View view){
        Intent intent = new Intent(this, Juego.class);
        if(alias.getText().toString().isEmpty()){
            intent.putExtra("Alias", "Jugador 1");
        } else {
            intent.putExtra("Alias", alias.getText().toString());
        }
        intent.putExtra("Medida", value.getText());
        intent.putExtra("Tiempo", time);
        intent.putExtra("Controlar", checkBox.isChecked());
        intent.putExtra("Dificultad", String.valueOf(spinner.getSelectedItem()));
        startActivity(intent);
    }
}
