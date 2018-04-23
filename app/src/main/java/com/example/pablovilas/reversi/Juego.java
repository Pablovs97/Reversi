package com.example.pablovilas.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class Juego extends AppCompatActivity {

    int medida;
    int tiempo;
    boolean control_tiempo;
    String alias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        Intent intent = getIntent();

        medida = Integer.parseInt(intent.getStringExtra("Medida"));
        alias = intent.getStringExtra("Alias");
        tiempo = intent.getIntExtra("Tiempo", 0);
        control_tiempo = intent.getBooleanExtra("Controlar", false);

        GridView gv = (GridView) findViewById(R.id.gridView);
        gv.setNumColumns(medida);
        gv.setAdapter(new ImageAdapter(this, initialBoard()));
    }

    public Cell[][] initialBoard(){
        Cell[][] board = new Cell[medida][medida];

        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                board[i][j] = Cell.empty();
            }
        }

        board[medida/2 - 1][medida/2 - 1] = Cell.white();
        board[medida/2 - 1][medida/2] = Cell.black();
        board[medida/2][medida/2 - 1] = Cell.black();
        board[medida/2][medida/2] = Cell.white();

        return board;
    }
}
