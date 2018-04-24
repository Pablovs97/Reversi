package com.example.pablovilas.reversi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private Cell[][] board;

    ImageAdapter(Context context, Cell[][] board) {
        this.context = context;
        this.board = board;
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

        if (this.board[x][y].isWhite()){
            imageButton.setImageResource(R.drawable.cell_white);
        } else if (this.board[x][y].isBlack()){
            imageButton.setImageResource(R.drawable.cell_black);
        } else if (this.board[x][y].isHint()){
            imageButton.setImageResource(R.drawable.cell_move);
        } else if (this.board[x][y].isEmpty()){
            imageButton.setImageResource(R.drawable.cell_background);
        }

        imageButton.setOnClickListener(new CellListener(context, position, this.board[x][y]));

        return imageButton;
    }
}
