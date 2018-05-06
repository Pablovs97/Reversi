package com.example.pablovilas.reversi;

import java.io.Serializable;

public class Cell implements Serializable{

    // New white y New black creadas con el proposito de saber que celdas animar.
    private static final char WHITE = 'w';
    private static final char BLACK = 'b';
    private static final char NEW_WHITE = 'a';
    private static final char NEW_BLACK = 's';
    private static final char HINT = 'h';
    private static final char EMPTY = '·';

    private char state;

    private Cell(char state) {
        this.state = state;
    }

    public static Cell empty() {
        return new Cell(EMPTY);
    }

    public static Cell white() {
        return new Cell(WHITE);
    }

    public static Cell black() {
        return new Cell(BLACK);
    }

    public static Cell hint() {
        return new Cell(HINT);
    }

    public boolean isEmpty() {
        return this.state == EMPTY;
    }

    public boolean isWhite() {
        return this.state == WHITE || this.state == NEW_WHITE;
    }

    public boolean isNewBlack() {
        return this.state == NEW_BLACK;
    }

    public boolean isNewWhite() {
        return this.state == NEW_WHITE;
    }

    public boolean isBlack() {
        return this.state == BLACK || this.state == NEW_BLACK;
    }

    public boolean isHint() {
        return this.state == HINT;
    }

    public void setHint() {
        this.state = HINT;
    }

    public void setWhite() {
        this.state = WHITE;
    }

    public void setBlack() {
        this.state = BLACK;
    }

    public void setNewWhite() {
        this.state = NEW_WHITE;
    }

    public void setNewBlack() {
        this.state = NEW_BLACK;
    }

    public void setEmpty() {
        this.state = EMPTY;
    }

    public void reverse() {
        switch (this.state) {
            case WHITE:
                this.state = BLACK;
                break;
            case BLACK:
                this.state = WHITE;
                break;
            default:
                this.state = EMPTY;
                break;
        }
    }

    public String toString() {
        return String.valueOf(this.state);
    }
}
