package com.example.pablovilas.reversi.activities.listado_partidas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pablovilas.reversi.R;

public class RegFrag extends Fragment {

    private TextView txtDetalle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detalle_partidas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txtDetalle = (TextView) getView().findViewById(R.id.detalle);

        if(savedInstanceState != null){
            txtDetalle.setText(savedInstanceState.getString("detalle"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("detalle", (String) txtDetalle.getText());
    }

    public void mostrarDetalle(String texto) {
        txtDetalle = (TextView) getView().findViewById(R.id.detalle);
        txtDetalle.setText(texto);
    }
}
