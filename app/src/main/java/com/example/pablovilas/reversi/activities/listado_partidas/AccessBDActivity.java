package com.example.pablovilas.reversi.activities.listado_partidas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.activities.BaseActivity;

public class AccessBDActivity extends BaseActivity implements QueryFrag.ResultadoListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultado_partidas);
        QueryFrag frgListado = (QueryFrag) getSupportFragmentManager().findFragmentById(R.id.listado_partidas);
        frgListado.setResultadoListener(this);
    }

    @Override
    public void onResultadoSeleccionado(String str) {
        RegFrag fgdet = (RegFrag) getSupportFragmentManager().findFragmentById(R.id.fragmento_detalle);

        if (fgdet != null && fgdet.isInLayout()) {
            RegFrag fdetail = (RegFrag) getSupportFragmentManager().findFragmentById(R.id.fragmento_detalle);
            fdetail.mostrarDetalle(str);
        } else {
            Intent intent = new Intent(this, DetailRegActivity.class);
            intent.putExtra("str", str);
            startActivity(intent);
        }
    }

    public void volver(View view) {
        finish();
    }
}