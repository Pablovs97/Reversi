package com.example.pablovilas.reversi;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.Serializable;

public class ImageAdapter extends BaseAdapter implements Serializable{
    private Context context;
    private Cell[][] cells;
    Juego juego;

    ImageAdapter(Context context, Cell[][] cells, Juego juego) {
        this.context = context;
        this.cells = cells;
        this.juego = juego;
    }

    @Override
    public int getCount() {
        return cells.length* cells.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageButton imageButton = new ImageButton(context);
        imageButton.setLayoutParams(new GridView.LayoutParams((parent.getWidth() - (parent.getPaddingEnd() + parent.getPaddingEnd())) / cells.length,
                (parent.getWidth() - (parent.getPaddingEnd() + parent.getPaddingEnd())) / cells.length));
        imageButton.setScaleType(ImageButton.ScaleType.FIT_XY);
        imageButton.setPadding(0, 0, 0, 0);

        int x = position % this.cells.length;
        int y = position / this.cells.length;

         if (this.cells[x][y].isNewBlack()){
            imageButton.setBackgroundResource(R.drawable.flip_w_to_b);
            AnimationDrawable ad = (AnimationDrawable) imageButton.getBackground();
            ad.start();
            juego.game.board.cells[x][y] = Cell.black();
         } else if (this.cells[x][y].isNewWhite()){
            imageButton.setBackgroundResource(R.drawable.flip_b_to_w);
            AnimationDrawable ad = (AnimationDrawable) imageButton.getBackground();
            ad.start();
             juego.game.board.cells[x][y] = Cell.white();
         } else if (this.cells[x][y].isWhite()){
            imageButton.setImageResource(R.drawable.cell_white);
        } else if (this.cells[x][y].isBlack()){
            imageButton.setImageResource(R.drawable.cell_black);
        } else if (this.cells[x][y].isHint() && this.juego.game.state == State.BLACK_TURN){
            imageButton.setImageResource(R.drawable.cell_hint);
        } else if (this.cells[x][y].isEmpty() || this.juego.game.state == State.WHITE_TURN){
            imageButton.setImageResource(R.drawable.cell_background);
        }

        imageButton.setOnClickListener(new CellListener(context, new Position(x, y), this.cells[x][y], juego));

        return imageButton;
    }
}
