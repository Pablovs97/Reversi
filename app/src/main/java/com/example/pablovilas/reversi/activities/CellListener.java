package com.example.pablovilas.reversi.activities;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.activities.juego.ParrillaFrag;
import com.example.pablovilas.reversi.logica_juego.Cell;
import com.example.pablovilas.reversi.logica_juego.Position;
import com.example.pablovilas.reversi.logica_juego.State;

public class CellListener implements OnClickListener {

    private Position position;
    private Context context;
    private Cell cell;
    private ParrillaFrag juego;

    CellListener(Context context, Position position, Cell cell, ParrillaFrag juego) {
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
            juego.updateLog(String.format(context.getString(R.string.log_casilla), position.getColumn(), position.getRow()));
        } else if(juego.getState() != State.FINISHED) {
            juego.showToast(R.drawable.shape_toast_grey, R.drawable.invalid, context.getString(R.string.casilla_invalida));
        }
    }
}
