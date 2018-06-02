package com.example.pablovilas.reversi.activities.listado_partidas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.bbdd.PartidasBD;

import java.util.ArrayList;
import java.util.List;

public class QueryFrag extends android.support.v4.app.Fragment {

    private ResultadoListener listener;
    private Cursor c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listado_partidas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        PartidasBD pdb = new PartidasBD(getActivity(), "Partidas", null, 2);
        SQLiteDatabase db = pdb.getWritableDatabase();

        String[] campos = new String[]{"alias", "date", "medida", "control", "num_blacks", "num_whites", "total_time", "state"};
        c = db.query("Partidas", campos, null, null, null, null, null, null);

        List<String> entries = new ArrayList<>();

        if(c.moveToFirst()){
            do {
                String alias = c.getString(0);
                String date = c.getString(1);
                String state = c.getString(7);
                alias = alias + " - " + date + "\n" + state;
                entries.add(alias);
            } while (c.moveToNext());
        }

        ListView lv = (ListView) getView().findViewById(R.id.lv);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        entries);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
                if(listener!=null){
                    if (c.moveToPosition(pos)){
                        String alias = c.getString(0);
                        String date = c.getString(1);
                        String medida = c.getString(2);
                        String control = c.getString(3);
                        String num_blacks = c.getString(4);
                        String num_whites = c.getString(5);
                        String total_time = c.getString(6);
                        String state = c.getString(7);
                        listener.onResultadoSeleccionado(state + "\n" +
                                "Alias: " + alias + "\n" +
                                "Fecha: " + date + "\n" +
                                "Medida: " + medida + "\n" +
                                "Num. fichas negras: " + num_blacks + "\n" +
                                "Num. fichas blancas: " + num_whites + "\n" +
                                "Tiempo total: " + total_time + "\n");
                    }
                }
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    public interface ResultadoListener {
        void onResultadoSeleccionado(String str);
    }

    public void setResultadoListener(ResultadoListener listener) {
        this.listener = listener;
    }
}
