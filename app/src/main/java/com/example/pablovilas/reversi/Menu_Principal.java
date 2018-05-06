package com.example.pablovilas.reversi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Menu_Principal extends AppCompatActivity {

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

    public void muestraConfiguracion (View clickedButton) {
        Intent in = new Intent(this, Configuracion.class);
        startActivity(in);
    }

    public void salir (View clickedButton) {
        finish();
    }
}
