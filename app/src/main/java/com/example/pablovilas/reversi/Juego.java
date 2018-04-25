package com.example.pablovilas.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Juego extends AppCompatActivity {

    int medida;
    int tiempo;
    boolean control_tiempo;
    String alias;
    private Board board;
    public State state;
    GridView gv;
    ImageAdapter adapter;
    TextView tv1, tv2, tv3, tv5;
    TextView textTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv5 = (TextView) findViewById(R.id.tv5);


        Intent intent = getIntent();

        medida = Integer.parseInt(intent.getStringExtra("Medida"));
        alias = intent.getStringExtra("Alias");
        tiempo = intent.getIntExtra("Tiempo", 0);
        control_tiempo = intent.getBooleanExtra("Controlar", false);

        board = new Board(medida);
        state = State.BLACK;

        setHints();

        textTimer = (TextView)findViewById(R.id.tv4);
        startTimer();

        gv = (GridView) findViewById(R.id.gridView);
        gv.setNumColumns(medida);
        adapter = new ImageAdapter(this, board.cells, this);
        gv.setAdapter(adapter);
        tv1.setText(String.valueOf(this.board.black));
        tv2.setText(String.valueOf(this.board.white));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Board", board.cells);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            board.setCells((Cell[][]) savedInstanceState.getSerializable("Board"));
            adapter = new ImageAdapter(this, board.cells, this);
            gv.setAdapter(adapter);
        }
    }

    public void startTimer(){
        new CountDownTimer(tiempo * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format("Time Remaining %02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                textTimer.setText(text);
            }

            public void onFinish() {
                textTimer.setText("Time's up!");
                state = State.FINISHED;
            }

        }.start();
    }

    public void updateGrid(){
        setHints();
        adapter.notifyDataSetChanged();
        gv.setAdapter(adapter);
    }

    public void setHints(){
        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if(this.board.cells[i][j].isHint()){
                    this.board.cells[i][j] = Cell.empty();
                }
                if(canPlayPosition(this.state, new Position(i, j))){
                    this.board.cells[i][j] = Cell.hint();
                }
            }
        }
    }

    public void turnoCPU(){
        setHints();
        final Cell[][] initial = this.board.getCells();
        int initial_whites = this.board.white;
        int initial_blacks = this.board.black;
        int maxNumFichas = 0;
        Position pos = new Position(0,0);

        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if (this.board.getCells()[i][j].isHint()) {
                    int numFichas = this.numFichas(new Position(i, j), initial_whites);
                    if(numFichas > maxNumFichas){
                        maxNumFichas = numFichas;
                        pos = new Position(i, j);
                    }
                    this.board.setStartRoundValues(initial, initial_whites, initial_blacks);
                }
            }
        }
        tv5.append("(" + String.valueOf(pos.getRow()) + "," + String.valueOf(pos.getColumn()) + ") " + String.valueOf(maxNumFichas) + "\n");
        this.board.setStartRoundValues(initial, initial_whites, initial_blacks);
        this.move(pos);
    }

    public boolean isFinished() {
        return this.state.equals(State.FINISHED);
    }

    public boolean isSame(State player, Position position) {
        return player.equals(State.BLACK) && this.board.isBlack(position) || player.equals(State.WHITE) && this.board.isWhite(position);
    }

    public boolean isOther(State player, Position position) {
        return player.equals(State.BLACK) && this.board.isWhite(position) || player.equals(State.WHITE) && this.board.isBlack(position);
    }

    public boolean someSame(State player, Position position, Direction direction) {
        return !(!this.board.contains(position) || (this.board.isEmpty(position))) && ((this.board.isBlack(position) && player.equals(State.BLACK)) || (this.board.isWhite(position) && player.equals(State.WHITE)) || someSame(player, position.move(direction), direction));
    }

    public boolean isReverseDirection(State player, Position position, Direction direction) {
        return isOther(player, position.move(direction)) && someSame(player, position.move(direction), direction);
    }

    public boolean[] directionsOfReverse(State player, Position position) {
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

    public boolean canPlayPosition(State player, Position position) {
        return this.board.isEmpty(position) && !allFalse(directionsOfReverse(player, position));
    }

    public boolean canPlay(State player) {
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
        if (State.BLACK == this.state) {
            this.board.setBlack(position);
        } else {
            this.board.setWhite(position);
        }
    }

    private void reverse(Position position, Direction direction) {
        position = position.move(direction);
        if (this.state == State.BLACK) {
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

    private void changeTurn() {
        if (canPlay(getJugadorContrario())) {
            this.state = getJugadorContrario();
            tv3.setText(this.state.toString());
        } else if (!canPlay(this.state)){
            this.state = State.FINISHED;
        }
        if(this.state == State.WHITE) {
            turnoCPU();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    updateGrid();
                }
            }, 3000);
        }
    }

    private State getJugadorContrario() {
        if (this.state == State.BLACK) {
            return State.WHITE;
        } else {
            return State.BLACK;
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
        this.changeTurn();
        tv1.setText(String.valueOf(this.board.black));
        tv2.setText(String.valueOf(this.board.white));
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
        return this.board.white - numWhites;
    }

}
