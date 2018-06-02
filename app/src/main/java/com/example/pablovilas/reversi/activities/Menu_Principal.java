package com.example.pablovilas.reversi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.activities.juego.JuegoActivity;
import com.example.pablovilas.reversi.activities.listado_partidas.AccessBDActivity;

public class Menu_Principal extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);
        getSupportActionBar().setTitle(R.string.menu_principal);
    }

    public void muestraAyuda (View clickedButton) {
        Intent in = new Intent(this, Ayuda.class);
        startActivity(in);
    }

    public void comenzarPartida(View clickedButton) {
        Intent in = new Intent(this, JuegoActivity.class);
        startActivity(in);
    }

    public void consultarPartidas(View clickedButton) {
        Intent in = new Intent(this, AccessBDActivity.class);
        startActivity(in);
    }

    public void salir (View clickedButton) {
        finish();
    }
}
