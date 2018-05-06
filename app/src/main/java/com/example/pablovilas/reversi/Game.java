package com.example.pablovilas.reversi;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable{

    public Board board;
    public State state;

    Game(Board board) {
        this.board = board;
        this.state = State.BLACK_TURN;
        setHints();
    }

    protected Game(Parcel in) {
        state = (State) in.readSerializable();
        board = in.readParcelable(Board.class.getClassLoader());
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public void setHints(){
        for (int i = 0; i < this.board.medida; i++) {
            for (int j = 0; j < this.board.medida; j++) {
                if(this.board.cells[i][j].isHint()){
                    this.board.cells[i][j].setEmpty();
                } else if(this.board.cells[i][j].isNewBlack() && this.state == State.BLACK_TURN) {
                    this.board.cells[i][j].setBlack();
                } else if(this.board.cells[i][j].isNewWhite() && this.state == State.WHITE_TURN) {
                    this.board.cells[i][j].setWhite();
                }
                if(canPlayPosition(this.state, new Position(i, j))){
                    this.board.cells[i][j].setHint();
                }
            }
        }
    }

    public boolean isFinished() {
        return this.state.equals(State.FINISHED);
    }

    public boolean isSame(State player, Position position) {
        return player.equals(State.BLACK_TURN) && this.board.isBlack(position) || player.equals(State.WHITE_TURN) && this.board.isWhite(position);
    }

    private boolean isOther(State player, Position position) {
        return player.equals(State.BLACK_TURN) && this.board.isWhite(position) || player.equals(State.WHITE_TURN) && this.board.isBlack(position);
    }

    private boolean someSame(State player, Position position, Direction direction) {
        return !(!this.board.contains(position) || (this.board.isEmpty(position))) && ((this.board.isBlack(position) && player.equals(State.BLACK_TURN)) || (this.board.isWhite(position) && player.equals(State.WHITE_TURN)) || someSame(player, position.move(direction), direction));
    }

    private boolean isReverseDirection(State player, Position position, Direction direction) {
        return isOther(player, position.move(direction)) && someSame(player, position.move(direction), direction);
    }

    private boolean[] directionsOfReverse(State player, Position position) {
        boolean[] result = new boolean[Direction.ALL.length];
        for (int i = 0; i < Direction.ALL.length; i++) {
            result[i] = isReverseDirection(player, position, Direction.ALL[i]);
        }
        return result;
    }

    private static boolean allFalse(boolean[] bools) {
        for (boolean bool : bools) {
            if (bool) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlayPosition(State player, Position position) {
        return this.board.isEmpty(position) && !allFalse(directionsOfReverse(player, position));
    }

    private boolean canPlay(State player) {
        for (int i = 0; i < this.board.size(); i++) {
            for (int j = 0; j < this.board.size(); j++) {
                if (canPlayPosition(player, new Position(i, j))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void disk(Position position) {
        if (State.BLACK_TURN == this.state) {
            this.board.setBlack(position);
        } else {
            this.board.setWhite(position);
        }
    }

    private void reverse(Position position, Direction direction) {
        position = position.move(direction);
        if (this.state == State.BLACK_TURN) {
            reverseToBlack(position, direction);
        } else {
            reverseToWhite(position, direction);
        }
    }

    private void reverseToWhite(Position position, Direction direction) {
        while (this.board.isBlack(position)) {
            this.board.reverse(position);
            position = position.move(direction);
        }
    }

    private void reverseToBlack(Position position, Direction direction) {
        while (this.board.isWhite(position)) {
            this.board.reverse(position);
            position = position.move(direction);
        }
    }

    private void reverse(Position position, boolean[] directions) {
        for (int i = 0; i < Direction.ALL.length; i++) {
            if (directions[i]) {
                reverse(position, Direction.ALL[i]);
            }
        }
    }

    public void changeTurn() {
        if (this.state != State.FINISHED) {
            if (canPlay(getJugadorContrario())) {
                this.state = getJugadorContrario();
            } else if (!canPlay(this.state)) {
                this.state = State.FINISHED;
            }
        }
    }

    private State getJugadorContrario() {
        if (this.state == State.BLACK_TURN) {
            return State.WHITE_TURN;
        } else {
            return State.BLACK_TURN;
        }
    }

    public void move(Position position) {
        if (!this.board.isEmpty(position)) {
            return;
        }
        boolean[] directions = this.directionsOfReverse(this.state, position);
        if (allFalse(directions)) {
            return;
        }
        this.disk(position);
        this.reverse(position, directions);
    }

    public int numFichas(Position position, int numWhites){
        if (!this.board.isEmpty(position)) {
            return -1;
        }
        boolean[] directions = this.directionsOfReverse(this.state, position);
        if (allFalse(directions)) {
            return -1;
        }
        this.disk(position);
        this.reverse(position, directions);
        return this.board.white - numWhites - 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.board, flags);
        dest.writeSerializable(this.state);
    }
}
