package com.example.pablovilas.reversi.logica_juego;

public class Position {

    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public Position move(Direction direction) {
        return new Position(this.row + direction.getChangeInRow(), this.column + direction.getChangeInColumn());
    }
}

