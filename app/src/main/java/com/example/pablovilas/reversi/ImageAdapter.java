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
    private Cell[][] board;
    Juego juego;

    ImageAdapter(Context context, Cell[][] board, Juego juego) {
        this.context = context;
        this.board = board;
        this.juego = juego;
    }

    @Override
    public int getCount() {
        return board.length*board.length;
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

        ImageButton imageButton;

        if (convertView == null) {
            imageButton = new ImageButton(context);
            imageButton.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / board.length, parent.getWidth() / board.length));
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setScaleType(ImageButton.ScaleType.FIT_XY);
            imageButton.setPadding(0, 0, 0, 0);
        } else {
            imageButton = (ImageButton) convertView;
        }

        int x = position % this.board.length;
        int y = position / this.board.length;

         if (this.board[x][y].isNewBlack()){
            imageButton.setBackgroundResource(R.drawable.flip_w_to_b);
            AnimationDrawable ad = (AnimationDrawable) imageButton.getBackground();
            ad.start();
         } else if (this.board[x][y].isNewWhite()){
            imageButton.setBackgroundResource(R.drawable.flip_b_to_w);
            AnimationDrawable ad = (AnimationDrawable) imageButton.getBackground();
            ad.start();
         } else if (this.board[x][y].isWhite()){
            imageButton.setImageResource(R.drawable.cell_white);
        } else if (this.board[x][y].isBlack()){
            imageButton.setImageResource(R.drawable.cell_black);
        } else if (this.board[x][y].isHint() && this.juego.state == State.BLACK){
            imageButton.setImageResource(R.drawable.cell_hint);
        } else if (this.board[x][y].isEmpty() || this.juego.state == State.WHITE){
            imageButton.setImageResource(R.drawable.cell_background);
        }

        imageButton.setOnClickListener(new CellListener(context, new Position(x, y), this.board[x][y], juego));

        return imageButton;
    }
}
