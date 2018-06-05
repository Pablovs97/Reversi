package com.example.pablovilas.reversi.activities.listado_partidas;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryFrag extends Fragment {

    private ResultadoListener listener;
    private List<PartidasClass> entries;
    private ListView lv;
    private AdaptadorPartidas adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listado_partidas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        final PartidasBD pdb = new PartidasBD(getActivity());

        // Obtener todas las partidas guardadas.
        entries = pdb.allPartidas();

        // Añadimos al listView el adaptador personalizado.
        lv = (ListView) getView().findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new MultiChoiceListener());
        adapter = new AdaptadorPartidas(this);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
                if(listener!=null){
                    // Conseguir la partida seleccionada.
                    PartidasClass selectedPartida = pdb.getSelectedPartida(pos);

                    listener.onResultadoSeleccionado(selectedPartida.getState() + "\n" +
                            "Alias: " + selectedPartida.getAlias() + "\n" +
                            "Fecha: " + selectedPartida.getDate() + "\n" +
                            "Medida: " + selectedPartida.getMedida() + "x" + selectedPartida.getMedida() + " casillas\n" +
                            "Num. fichas negras: " + selectedPartida.getNum_blacks() + "\n" +
                            "Num. fichas blancas: " + selectedPartida.getNum_whites() + "\n" +
                            "Tiempo total: " + selectedPartida.getTotal_time() + " segundos\n");
                }
            }
        });
    }

    // Interfaz listener
    public interface ResultadoListener {
        void onResultadoSeleccionado(String str);
    }

    // Set listener
    public void setResultadoListener(ResultadoListener listener) {
        this.listener = listener;
    }

    // Adapter personalizado, consistente en varios textViews(Alias, fecha, estado final de la partida)
    // un ImageView, referente al estado final de la partida y una imagen que es mostrada cuando el item
    // es seleccionado.
    class AdaptadorPartidas extends ArrayAdapter<PartidasClass> {

        Activity context;
        List<Integer> positions = new ArrayList<>();

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

            ImageView checkbox = (ImageView) item.findViewById(R.id.checkbox);

            if (positions != null && positions.contains(position)){
                checkbox.setBackgroundResource(R.drawable.checkbox);
            }

            return(item);
        }

        public void setSelectedPartidas(List<Integer> positions){
            this.positions = positions;
        }
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    // MultiChoiceListener, actionmode que nos permitirá seleccionar una o varias partidas y
    // realizar acciones sobre ellas.
    private class MultiChoiceListener implements ListView.MultiChoiceModeListener {
        private int selectionCount;
        private List<Integer> selectedItemsPosition = new ArrayList<>();
        private PartidasBD pdb = new PartidasBD(getActivity());

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.listado_partidas, menu);
            mode.setTitle(R.string.partida_seleccionada);
            setSubtitle(mode);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.eliminar:
                    Toast.makeText(getActivity(), "Shared " + lv.getCheckedItemCount() + " items", Toast.LENGTH_SHORT).show();
                    removeSelectedPartidas();
                    mode.finish();
                    break;
                default:
                    Toast.makeText(getActivity(), "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {}

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (checked) {
                selectionCount++;
                selectedItemsPosition.add(position);
                adapter.setSelectedPartidas(selectedItemsPosition);
                adapter.notifyDataSetChanged();
            } else {
                selectionCount--;
                selectedItemsPosition.remove(Integer.valueOf(position));
                adapter.notifyDataSetChanged();
            }

            setSubtitle(mode);
        }

        private void setSubtitle(ActionMode mode) {
            final int checkedCount = lv.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(R.string.una_seleccionada);
                    break;
                default:
                    mode.setSubtitle(checkedCount + " " + getString(R.string.varias_seleccionadas));
                    break;
            }
        }

        private void removeSelectedPartidas() {
            pdb.deletePartida(selectedItemsPosition);
            entries = pdb.allPartidas();
            adapter = new AdaptadorPartidas(QueryFrag.this);
            lv.setAdapter(adapter);

            if (selectedItemsPosition.size() == 1){
                showToast(R.drawable.shape_toast_red, R.drawable.delete, getString(R.string.una_eliminada));
            } else {
                showToast(R.drawable.shape_toast_red, R.drawable.delete, selectedItemsPosition.size() + " " + getString(R.string.varias_eliminada));
            }

            listener.onResultadoSeleccionado("");

            selectedItemsPosition = new ArrayList<>();
        }
    }

    // Muestra el Toast personalizado, el cuál consta de un ImageView y un TextView.
    public void showToast(int toast_shape, int image, String msg){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) getView().findViewById(R.id.toast));
        layout.setBackgroundResource(toast_shape);

        ImageView imageView = (ImageView) layout.findViewById(R.id.toast_iv);
        imageView.setBackgroundResource(image);

        TextView text = (TextView) layout.findViewById(R.id.toast_tv);
        text.setText(msg);

        Toast toast = new Toast(getActivity());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
