package com.example.pablovilas.reversi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
        if(cell.isHint() && juego.getState() == State.BLACK_TURN){
            juego.game.move(position);
            juego.changeTurn();
        } else if(juego.getState() != State.FINISHED) {
            juego.showToast(R.drawable.shape_toast_grey, R.drawable.invalid, "Casilla inválida, vuelve a intentarlo.");
        }
    }
}
