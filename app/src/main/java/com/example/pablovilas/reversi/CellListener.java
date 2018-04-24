package com.example.pablovilas.reversi;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class CellListener implements OnClickListener {

    private Position position;
    private Context context;
    private Cell cell;
    private Juego juego;

    CellListener(Context context, Position position, Cell cell, Juego juego) {
        this.context = context;
        this.position = position;
        this.cell = cell;
        this.juego = juego;
    }

    @Override
    public void onClick(View v) {
        if(cell.isHint() && juego.state==State.BLACK){
            juego.move(position);
            juego.updateGrid();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    juego.turnoCPU();
                    juego.updateGrid();
                }
            }, 500);

        } else if(juego.state == State.FINISHED) {
            Toast.makeText(context, "Se ha acabado el tiempo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }
}
