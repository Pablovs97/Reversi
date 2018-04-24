package com.example.pablovilas.reversi;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class CellListener implements OnClickListener {

    private int position;
    private Context context;
    private Cell cell;

    CellListener(Context context, int position, Cell cell) {
        this.context = context;
        this.position = position;
        this.cell = cell;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, cell.toString(), Toast.LENGTH_LONG).show();
    }
}
