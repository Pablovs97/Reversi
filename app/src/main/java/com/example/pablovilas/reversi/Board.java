package com.example.pablovilas.reversi;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.StringTokenizer;

public class Board implements Parcelable {

    public Cell[][] cells;
    public int medida;

    public int black;
    public int white;

    Board(int medida) {
        this.medida = medida;
        this.cells = new Cell[medida][medida];
        this.black = 0;
        this.white = 0;
        initBoard();
    }

    Board(Board other) {
        this.medida = other.medida;
        this.cells = other.getCells();
        this.black = other.black;
        this.white = other.white;
    }

    protected Board(Parcel in) {
        medida = in.readInt();
        cells = (Cell[][]) in.readSerializable();
        black = in.readInt();
        white = in.readInt();
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

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

        this.black = 2;
        this.white = 2;
    }

    // Devuelve una copia de la matriz de celdas.
    public Cell[][] getCells(){
        Cell[][] copy = new Cell[medida][medida];
        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if (this.cells[i][j].isWhite()){
                    copy[i][j] = Cell.white();
                } else if (this.cells[i][j].isBlack()){
                    copy[i][j] = Cell.black();
                } else if (this.cells[i][j].isHint()){
                    copy[i][j] = Cell.hint();
                } else if (this.cells[i][j].isEmpty()){
                    copy[i][j] = Cell.empty();
                }
            }
        }
        return copy;
    }

    public void setCells(Cell[][] cells){
        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if (cells[i][j].isWhite()){
                    this.cells[i][j] = Cell.white();
                } else if (cells[i][j].isBlack()){
                    this.cells[i][j] = Cell.black();
                } else if (cells[i][j].isHint()){
                    this.cells[i][j] = Cell.hint();
                } else if (cells[i][j].isEmpty()){
                    this.cells[i][j] = Cell.empty();
                }
            }
        }
    }

    public void setStartRoundValues(Cell[][] initial, int initial_whites, int initial_blacks){
        this.setCells(initial);
        this.white = initial_whites;
        this.black = initial_blacks;
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
            this.white++;
        }
    }

    public void setBlack(Position position) {
        if (this.isValidEmpy(position)) {
            this.cells[position.getRow()][position.getColumn()].setBlack();
            this.black++;
        }
    }

    public void reverse(Position position) {
        if (this.isValidFull(position)) {
            changeColors(position);
        }
    }

    private boolean isValidEmpy(Position position) {
        return this.contains(position) && this.isEmpty(position);
    }

    private boolean isValidFull(Position position) {
        return this.contains(position) && !this.isEmpty(position);
    }

    private void changeColors(Position position) {
        if (this.cells[position.getRow()][position.getColumn()].isBlack()) {
            this.cells[position.getRow()][position.getColumn()].setNewWhite();
            this.black--;
            this.white++;
        } else {
            this.cells[position.getRow()][position.getColumn()].setNewBlack();
            this.black++;
            this.white--;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.medida);
        dest.writeSerializable(this.cells);
        dest.writeInt(this.black);
        dest.writeInt(this.white);
    }
}
