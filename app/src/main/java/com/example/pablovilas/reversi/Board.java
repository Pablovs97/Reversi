package com.example.pablovilas.reversi;

import java.util.StringTokenizer;

public class Board {

    public Cell[][] cells;
    private int medida;

    public int black;
    public int white;

    Board(int medida) {
        this.medida = medida;
        this.cells = new Cell[medida][medida];
        this.black = 0;
        this.white = 0;
        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                this.cells[i][j] = Cell.empty();
            }
        }

        this.cells[medida/2 - 1][medida/2 - 1] = Cell.white();
        this.cells[medida/2 - 1][medida/2] = Cell.black();
        this.cells[medida/2][medida/2 - 1] = Cell.black();
        this.cells[medida/2][medida/2] = Cell.white();

        this.sumar(2, "Black");
        this.sumar(2, "White");
    }

    public int size(){
        return medida;
    }

    public boolean contains(Position position) {
        return position.getRow() < this.medida && position.getColumn() < this.medida && position.getRow() >= 0 && position.getColumn() >= 0;
    }

    public boolean isEmpty(Position position) {
        return this.contains(position) && (this.cells[position.getRow()][position.getColumn()].isEmpty() || this.cells[position.getRow()][position.getColumn()].isHint());
    }

    public boolean isWhite(Position position) {
        return this.contains(position) && this.cells[position.getRow()][position.getColumn()].isWhite();
    }

    public boolean isBlack(Position position) {
        return this.contains(position) && this.cells[position.getRow()][position.getColumn()].isBlack();

    }

    public void setWhite(Position position) {
        if (this.isValidEmpy(position)) {
            this.cells[position.getRow()][position.getColumn()].setWhite();
            this.sumar(1, "White");
        }
    }

    public void setBlack(Position position) {
        if (this.isValidEmpy(position)) {
            this.cells[position.getRow()][position.getColumn()].setBlack();
            this.sumar(1, "Black");
        }
    }

    public void reverse(Position position) {
        if (this.isValidFull(position)) {
            changeColors(position);
        }
    }

    public void loadBoard(String str) {
        StringTokenizer st = new StringTokenizer(str, "\n");
        int row = 0;
        this.white = 0;
        this.black = 0;
        while (st.hasMoreTokens()) {
            String rowChars = st.nextToken();
            for (int column = 0; column < this.cells[row].length; column++) {
                Cell cell = Cell.cellFromChar(rowChars.charAt(column));
                this.cells[row][column] = cell;
                if (cell.isWhite()) {
                    white += 1;
                } else if (cell.isBlack()) {
                    black += 1;
                }
            }
            row += 1;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells[i].length; j++) {
                sb.append(cells[i][j].toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    //Metodes Auxiliars
    private boolean isValidEmpy(Position position) {
        return this.contains(position) && this.isEmpty(position);
    }

    private boolean isValidFull(Position position) {
        return this.contains(position) && !this.isEmpty(position);
    }

    private void sumar(int i, String color) {
        if (color.equals("Black")) {
            this.black += i;
        } else {
            this.white += i;
        }
    }

    private void restar(int i, String color) {
        if (color.equals("Black")) {
            this.black -= i;
        } else {
            this.white -= i;
        }
    }

    private void changeColors(Position position) {
        if (this.cells[position.getRow()][position.getColumn()].isBlack()) {
            this.cells[position.getRow()][position.getColumn()].setWhite();
            this.restar(1, "Black");
            this.sumar(1, "White");
        } else {
            this.cells[position.getRow()][position.getColumn()].setBlack();
            this.sumar(1, "Black");
            this.restar(1, "White");
        }
    }

}
