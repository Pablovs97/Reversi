package com.example.pablovilas.reversi;
/*
LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.like_popup, (ViewGroup) activity.findViewById(R.id.like_popup_layout));
        ImageView imageView = (ImageView) layout.findViewById(R.id.like_popup_iv);
        imageView.setBackgroundResource(R.drawable.white_delete_icon);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        <?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" >
  <solid android:color="#60000000" />
    <corners android:radius="8dp" />
</shape>
*/
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Juego extends AppCompatActivity {

    int medida;
    int tiempo;
    boolean control_tiempo;
    String alias;
    Board board;
    public State state;
    GridView gv;
    ImageAdapter adapter;
    TextView tv1, tv2, tv3, tv5, tv6;
    TextView textTimer;
    private static CountDownTimer cd;
    long timeLeft, initialTime;
    List<Board> board_state;
    int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);

        Intent intent = getIntent();

        medida = Integer.parseInt(intent.getStringExtra("Medida"));
        alias = intent.getStringExtra("Alias");
        tiempo = intent.getIntExtra("Tiempo", 0);
        control_tiempo = intent.getBooleanExtra("Controlar", false);

        Log.d("Alias", alias);
        Log.d("Control", String.valueOf(control_tiempo));

        board = new Board(medida);
        state = State.BLACK_TURN;

        setHints();
        board_state = new ArrayList<>();
        board_state.add(new Board(board));

        textTimer = (TextView)findViewById(R.id.tv4);

        gv = (GridView) findViewById(R.id.gridView);
        gv.setNumColumns(medida);
        adapter = new ImageAdapter(this, board.cells, this);
        gv.setAdapter(adapter);
        setNumFichas();

        // Cuando giramos la pantalla o la actividad deja de estar en segundo plano, con el propósito de que el tiempo sea lo mas
        // fiel posible, hacemos que este sea el tiempo total menos el tiempo actual en milisegundos menos el tiempo al inicio de la partida.
        if(savedInstanceState != null) {
            timeLeft = tiempo*1000 - (System.currentTimeMillis() - savedInstanceState.getLong("Tiempo"));
            initialTime = savedInstanceState.getLong("Tiempo");
        // La primera vez, nos guardamos el tiempo inicial en milisegundos.
        } else {
            initialTime = System.currentTimeMillis();
            timeLeft = tiempo*1000;
        }
    }


    public void undo(View view){
        // Sólo puede hacerse Undo cuando es el turno del jugador
        if(state == State.WHITE_TURN){
            Toast.makeText(this, "Espera tu turno", Toast.LENGTH_SHORT).show();
        } else if (state == State.BLACK_TURN) {
            if (board_state.size() - contador - 1 > 0) {
                contador++;
                board = new Board(board_state.get(board_state.size() - contador - 1));
                adapter = new ImageAdapter(this, board.cells, this);
                gv.setAdapter(adapter);
            } else { // No podemos ir más para detrás.
                Toast.makeText(this, "No puedes cargar el estado anterior", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void redo(View view){
        // Sólo puede hacerse Redo cuando es el turno del jugador
        if(state == State.WHITE_TURN){
            Toast.makeText(this, "Espera tu turno", Toast.LENGTH_SHORT).show();
        } else if (state == State.BLACK_TURN) {
            if (board_state.size() - contador < board_state.size()) {
                contador--;
                board = new Board(board_state.get(board_state.size() - contador - 1));
                adapter = new ImageAdapter(this, board.cells, this);
                gv.setAdapter(adapter);
            } else { // No podemos ir más para delante.
                Toast.makeText(this, "No puedes cargar el estado siguiente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Tras hacer Undo, si realizamos una acción, todos los estados de la parrilla a partir de este estado son eliminados del Array.
    public void removeArrayFromIndex(int index){
        List<Board> temp = new ArrayList<>();
        for(int i = 0; i <= index; i++){
            temp.add(board_state.get(i));
            contador = 0;
        }
        board_state = temp;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Board", board.cells);
        outState.putLong("Tiempo", this.initialTime);
        outState.putInt("Blacks", this.board.black);
        outState.putInt("Whites", this.board.white);
        outState.putParcelableArrayList("UndoRedoList", (ArrayList<? extends Parcelable>) this.board_state);
        outState.putInt("Contador", this.contador);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cd.cancel();
        cd = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd == null){
            startTimer();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            board.cells = (Cell[][]) savedInstanceState.getSerializable("Board");
            board.black = (int) savedInstanceState.getInt("Blacks");
            board.white = (int) savedInstanceState.getInt("Whites");
            setNumFichas();
            adapter = new ImageAdapter(this, board.cells, this);
            board_state = savedInstanceState.getParcelableArrayList("UndoRedoList");
            contador = savedInstanceState.getInt("Contador");
            gv.setAdapter(adapter);
        }
    }

    public void setNumFichas(){
        tv1.setText(String.valueOf(this.board.black));
        tv2.setText(String.valueOf(this.board.white));
        tv6.setText(String.valueOf((this.medida*this.medida) - this.board.white - this.board.black));
    }

    public void goToResults(String msg){
        Intent intent = new Intent(this, Resultados.class);
        intent.putExtra("Log",
                msg + "Alias: " + this.alias + "\nMedida tablero: " + String.valueOf(this.medida) + "\nTiempo transcurrido: " + String.valueOf(tiempo - timeLeft/1000) + " s.");
        finish();
        startActivity(intent);
    }

    public void startTimer(){
        cd = new CountDownTimer(timeLeft, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "Time Remaining %02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                textTimer.setText(text);
                timeLeft = millisUntilFinished;
                Log.d("asd", String.valueOf(timeLeft));
            }

            public void onFinish() {
                cd.cancel();
                textTimer.setText("00:00");
                state = State.FINISHED;
                goToResults("¡TIEMPO AGOTADO! :O\nOs habéis quedado sin completar el tablero.\nTu: " + String.valueOf(board.black) + " casillas.\nOponente: " + String.valueOf(board.white) + " casillas.\n"
                        + String.valueOf(Math.abs(board.black - board.white)) + " casillas de diferencia.\nHan quedado " + String.valueOf(medida*medida - (board.black + board.white)) + " casillas por cubrir.\n");
            }

        }.start();
    }

    public void updateGrid(){
        setHints();
        adapter.notifyDataSetChanged();
        gv.setAdapter(adapter);
        if(this.state == State.BLACK_TURN){
            board_state.add(new Board(board));
        }
    }

    public void setHints(){
        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
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
        this.board.setStartRoundValues(initial, initial_whites, initial_blacks);
        this.move(pos);
    }

    public boolean isFinished() {
        return this.state.equals(State.FINISHED);
    }

    public boolean isSame(State player, Position position) {
        return player.equals(State.BLACK_TURN) && this.board.isBlack(position) || player.equals(State.WHITE_TURN) && this.board.isWhite(position);
    }

    public boolean isOther(State player, Position position) {
        return player.equals(State.BLACK_TURN) && this.board.isWhite(position) || player.equals(State.WHITE_TURN) && this.board.isBlack(position);
    }

    public boolean someSame(State player, Position position, Direction direction) {
        return !(!this.board.contains(position) || (this.board.isEmpty(position))) && ((this.board.isBlack(position) && player.equals(State.BLACK_TURN)) || (this.board.isWhite(position) && player.equals(State.WHITE_TURN)) || someSame(player, position.move(direction), direction));
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

    private void changeTurn() {
        if (this.state != State.FINISHED) {
            if (canPlay(getJugadorContrario())) {
                this.state = getJugadorContrario();
                tv3.setText(this.state.toString());
            } else if (!canPlay(this.state)) {
                this.state = State.FINISHED;
                if((medida*medida - (board.black + board.white)) > 0){
                    goToResults("¡BLOQUEO! :S\nOs habéis quedado sin completar el tablero.\nTu: " + String.valueOf(board.black) + " casillas.\nOponente: " + String.valueOf(board.white) + " casillas.\n"
                            + String.valueOf(Math.abs(board.black - board.white)) + " casillas de diferencia.\nHan quedado " + String.valueOf(medida*medida - (board.black + board.white)) + " casillas por cubrir.\n");
                    cd.cancel();
                }
            }
        }
        if(this.state == State.WHITE_TURN) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    turnoCPU();
                    updateGrid();
                }
            }, 1500);
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
        this.changeTurn();
        setNumFichas();
        if(((this.medida*this.medida) - this.board.white - this.board.black) == 0){
            this.state = State.FINISHED;
            if(this.board.black > this.board.white){
                goToResults("¡HAS GANADO! :D\nTu: " + String.valueOf(board.black) + " casillas.\nOponente: " + String.valueOf(board.white) + " casillas.\n"
                + String.valueOf(board.black - board.white) + " casillas de diferencia.\n");
                cd.cancel();
            } else if(this.board.black < this.board.white){
                goToResults("¡HAS PERDIDO! :C\nOponente: " + String.valueOf(board.white) + " casillas.\nTu: " + String.valueOf(board.black) + " casillas.\n"
                        + String.valueOf(board.white - board.black) + " casillas de diferencia.\n");
                cd.cancel();
            } else if(this.board.black == this.board.white){
                goToResults("¡HABÉIS EMPATADO! :|\n");
                cd.cancel();
            }
        }
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

}
