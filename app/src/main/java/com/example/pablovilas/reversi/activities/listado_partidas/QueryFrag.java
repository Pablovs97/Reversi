package com.example.pablovilas.reversi.activities.listado_partidas;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.bbdd.PartidasBD;
import com.example.pablovilas.reversi.bbdd.PartidasClass;

import java.util.ArrayList;
import java.util.List;

public class QueryFrag extends android.support.v4.app.Fragment {

    private ResultadoListener listener;
    private Cursor c;
    private List<PartidasClass> entries;

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

        entries = new ArrayList<>();

        if(c.moveToFirst()){
            do {
                String alias = c.getString(0);
                String date = c.getString(1);
                String state = c.getString(7);
                //alias = alias + " - " + date + "\n" + state;
                entries.add(new PartidasClass(alias, date, state));
            } while (c.moveToNext());
        }

        ListView lv = (ListView) getView().findViewById(R.id.lv);
        lv.setAdapter(new AdaptadorPartidas(this));

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

    class AdaptadorPartidas extends ArrayAdapter<PartidasClass> {

        Activity context;

        AdaptadorPartidas(QueryFrag fragmentListado) {
            super(fragmentListado.getActivity(), R.layout.listview_item, entries);
            this.context = fragmentListado.getActivity();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listview_item, null);

            TextView alias = (TextView) item.findViewById(R.id.alias);
            alias.setText(entries.get(position).getAlias());

            TextView date = (TextView) item.findViewById(R.id.date);
            date.setText(entries.get(position).getDate());

            TextView status = (TextView) item.findViewById(R.id.status);
            status.setText(entries.get(position).getState());

            ImageView image = (ImageView) item.findViewById(R.id.image);

            switch (entries.get(position).getState()){
                case "VICTORIA":
                    image.setBackgroundResource(R.drawable.victoria);
                    image.setBackgroundTintList(getActivity().getColorStateList(R.color.green));
                    status.setTextColor(getActivity().getColorStateList(R.color.green));
                    break;
                case "DERROTA":
                    image.setBackgroundResource(R.drawable.derrota);
                    image.setBackgroundTintList(getActivity().getColorStateList(R.color.red));
                    status.setTextColor(getActivity().getColorStateList(R.color.red));
                    break;
                case "EMPATE":
                    image.setBackgroundResource(R.drawable.empate);
                    image.setBackgroundTintList(getActivity().getColorStateList(R.color.yellow));
                    status.setTextColor(getActivity().getColorStateList(R.color.yellow));
                    break;
                case "BLOQUEO":
                    image.setBackgroundResource(R.drawable.bloqueo);
                    image.setBackgroundTintList(getActivity().getColorStateList(R.color.orange));
                    status.setTextColor(getActivity().getColorStateList(R.color.orange));
                    break;
                case "TIEMPO AGOTADO":
                    image.setBackgroundResource(R.drawable.tiempo_acabado);
                    image.setBackgroundTintList(getActivity().getColorStateList(R.color.dark_blue));
                    status.setTextColor(getActivity().getColorStateList(R.color.dark_blue));
                    break;
            }

            return(item);
        }
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
