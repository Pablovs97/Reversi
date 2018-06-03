package com.example.pablovilas.reversi.activities.listado_partidas;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.activities.BaseActivity;

public class DetailRegActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultado_detalle);
        RegFrag detail = (RegFrag) getSupportFragmentManager().findFragmentById(R.id.fragmento_detalle);
        detail.mostrarDetalle(getIntent().getStringExtra("str"));
    }
}
