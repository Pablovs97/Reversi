package com.example.pablovilas.reversi.activities.juego;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pablovilas.reversi.R;

public class LogFrag extends Fragment {

    TextView txtDetalle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_frag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txtDetalle = (TextView) getView().findViewById(R.id.log);

        if(savedInstanceState != null){
            txtDetalle.setText(savedInstanceState.getString("detalle"));
        }
    }

    public void addLog(String texto) {
        txtDetalle = (TextView) getView().findViewById(R.id.log);
        txtDetalle.setText(txtDetalle.getText() + texto);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("detalle", (String) txtDetalle.getText());
    }

}
