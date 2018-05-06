package com.example.pablovilas.reversi;
/*



*/
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Juego extends AppCompatActivity {

    int medida;
    int tiempo;
    boolean control_tiempo;
    String alias;
    String dificultad;
    public Game game;
    GridView gv;
    ImageAdapter adapter;
    TextView numBlacks, numWhites, numEmpty, textTimer, turno;
    private static CountDownTimer cd;
    long timeLeft, initialTime;
    List<Board> board_state;
    int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        getSupportActionBar().hide();

        numBlacks = (TextView) findViewById(R.id.num_blacks);
        numWhites = (TextView) findViewById(R.id.num_whites);
        numEmpty = (TextView) findViewById(R.id.num_empty);
        textTimer = (TextView) findViewById(R.id.time_min_sec);
        turno = (TextView) findViewById(R.id.turno);

        Intent intent = getIntent();

        medida = Integer.parseInt(intent.getStringExtra("Medida"));
        alias = intent.getStringExtra("Alias");
        tiempo = intent.getIntExtra("Tiempo", 0);
        control_tiempo = intent.getBooleanExtra("Controlar", false);
        dificultad = intent.getStringExtra("Dificultad");

        TextView cpu_level = (TextView) findViewById(R.id.cpu_level);
        cpu_level.setText(dificultad);

        TextView player_alias = (TextView) findViewById(R.id.player_alias);
        player_alias.setText(alias);

        if(control_tiempo) textTimer.setTextColor(getResources().getColor(R.color.red));

        game = new Game(new Board(medida));

        board_state = new ArrayList<>();
        board_state.add(new Board(game.board));

        gv = (GridView) findViewById(R.id.gridView);
        gv.setNumColumns(medida);
        adapter = new ImageAdapter(getApplicationContext(), game.board.cells, this);
        gv.setAdapter(adapter);
        setNumFichas();

        // Cuando giramos la pantalla o la actividad deja de estar en segundo plano, con el propósito de que el tiempo sea lo mas
        // fiel posible, hacemos que este sea el tiempo total menos el tiempo actual en milisegundos menos el tiempo al inicio de la partida.
        if(savedInstanceState != null) {
            if(control_tiempo){
                timeLeft = tiempo*1000 - (System.currentTimeMillis() - savedInstanceState.getLong("Tiempo"));
            } else {
                timeLeft = System.currentTimeMillis() - savedInstanceState.getLong("Tiempo");
            }
            initialTime = savedInstanceState.getLong("Tiempo");
            // La primera vez, nos guardamos el tiempo inicial en milisegundos.
        } else {
            if(control_tiempo){
                timeLeft = tiempo*1000;
            } else {
                timeLeft = 0;
            }
            initialTime = System.currentTimeMillis();
        }
    }

    public void undo(View view){
        // Sólo puede hacerse Undo cuando es el turno del jugador
        if(getState() == State.WHITE_TURN){
            Toast.makeText(this, "Espera tu turno", Toast.LENGTH_SHORT).show();
        } else if (getState() == State.BLACK_TURN) {
            if (board_state.size() - contador - 1 > 0) {
                contador++;
                game.board = new Board(board_state.get(board_state.size() - contador - 1));
                adapter = new ImageAdapter(this, game.board.cells, this);
                gv.setAdapter(adapter);
                setNumFichas();
            } else { // No podemos ir más para detrás.
                Toast.makeText(this, "No puedes cargar el estado anterior", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void redo(View view){
        // Sólo puede hacerse Redo cuando es el turno del jugador
        if(getState() == State.WHITE_TURN){
            Toast.makeText(this, "Espera tu turno", Toast.LENGTH_SHORT).show();
        } else if (getState() == State.BLACK_TURN) {
            if (board_state.size() - contador < board_state.size()) {
                contador--;
                game.board = new Board(board_state.get(board_state.size() - contador - 1));
                adapter = new ImageAdapter(this, game.board.cells, this);
                gv.setAdapter(adapter);
                setNumFichas();
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
        // Board
        outState.putSerializable("Board", game.board.cells);
        // Time left
        outState.putLong("Tiempo", this.initialTime);
        // Num whites y blacks
        outState.putInt("Blacks", this.game.board.black);
        outState.putInt("Whites", this.game.board.white);
        // Undo/Redo list e index de esta lista.
        outState.putParcelableArrayList("UndoRedoList", (ArrayList<? extends Parcelable>) this.board_state);
        outState.putInt("Contador", this.contador);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Paramos el contador.
        cd.cancel();
        cd = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Iniciamos el contador, hacía arriba o hacía abajo dependiendo de si hay o no control de tiempo.
        if(cd == null){
            if(control_tiempo){
                startDownTimer();
            } else {
                startUpTimer();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            game.board.cells = (Cell[][]) savedInstanceState.getSerializable("Board");
            game.board.black = savedInstanceState.getInt("Blacks");
            game.board.white = savedInstanceState.getInt("Whites");
            setNumFichas();
            adapter = new ImageAdapter(this, game.board.cells, this);
            board_state = savedInstanceState.getParcelableArrayList("UndoRedoList");
            contador = savedInstanceState.getInt("Contador");
            gv.setAdapter(adapter);
        }
    }

    // Actualiza en todos los textView el número de fichas blancas, negras, vacías y el turno.
    public void setNumFichas(){
        numBlacks.setText(String.valueOf(game.board.black));
        numWhites.setText(String.valueOf(game.board.white));
        numEmpty.setText(String.format(getString(R.string.casillas_vacias), String.valueOf((this.medida*this.medida) - game.board.white - game.board.black)));
        if(game.state == State.BLACK_TURN){
            turno.setText(R.string.tu_turno);
        } else if(game.state == State.WHITE_TURN){
            turno.setText(R.string.turno_rival);
        }
    }

    public void goToResults(String msg){
        final Intent intent = new Intent(this, Resultados.class);
        intent.putExtra("Log", msg + String.format(getString(R.string.Log), this.alias, String.valueOf(this.medida), String.valueOf(tiempo - timeLeft/1000)));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
                startActivity(intent);
            }
        }, 1000);
    }

    // Cuando el jugador indica que quiere control de tiempo, el tiempo va hacia abajo, cuando se termina, acaba la partida.
    public void startDownTimer(){
        cd = new CountDownTimer(timeLeft, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                textTimer.setText(text);
                timeLeft = millisUntilFinished;
            }

            public void onFinish() {
                cd.cancel();
                game.state = State.FINISHED;
                showToast(R.drawable.shape_toast_red, R.drawable.tiempo_acabado, getString(R.string.tiempo_agotado_solo));
                goToResults(String.format(getString(R.string.tiempo_agotado), String.valueOf(game.board.black), String.valueOf(game.board.white), String.valueOf(Math.abs(game.board.black - game.board.white)), String.valueOf(medida*medida - (game.board.black + game.board.white))));
            }

        }.start();
    }

    // Cuando el jugador no activa el control de tiempo, el tiempo va hacia arriba
    public void startUpTimer(){
        cd = new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60, TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60);
                timeLeft+=1000;
                textTimer.setText(text);
            }

            public void onFinish() {}

        }.start();
    }

    // Actualizamos todos los textViews y el grid.
    public void updateGrid(){
        setNumFichas();
        game.setHints();
        adapter.notifyDataSetChanged();
        gv.setAdapter(adapter);
        if(getState() == State.BLACK_TURN){
            board_state.add(new Board(game.board));
        }
    }

    // Dependiendo de la dificultad seleccionada por el usuario elegiremos una u otra estrategia
    public void turnoCPU(){
        switch (dificultad) {
            case "Fácil":
                turnoCPUFacil();
                break;
            case "Normal":
                turnoCPUNormal();
                break;
            case "Difícil":
                turnoCPUDificil();
                break;
        }
    }

    // Miramos para cada uno de los hint el número de fichas que gira, y nos movemos a uno de los
    // que menos fichas a girado.
    public void turnoCPUFacil(){
        game.setHints();
        final Cell[][] initial = game.board.getCells();
        int initial_whites = game.board.white;
        int initial_blacks = game.board.black;
        int minNumFichas = Integer.MAX_VALUE;
        List<Position> worst_moves = new ArrayList<>();

        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if (game.board.getCells()[i][j].isHint()) {
                    int numFichas = game.numFichas(new Position(i, j), initial_whites);
                    if(numFichas == minNumFichas){
                        worst_moves.add(new Position(i, j));
                    }
                    if(numFichas < minNumFichas){
                        minNumFichas = numFichas;
                        worst_moves = new ArrayList<>();
                        worst_moves.add(new Position(i, j));
                    }
                    game.board.setStartRoundValues(initial, initial_whites, initial_blacks);
                }
            }
        }
        game.board.setStartRoundValues(initial, initial_whites, initial_blacks);
        game.move(worst_moves.get(new Random().nextInt(worst_moves.size())));
        changeTurn();
    }

    public void turnoCPUNormal(){
        game.setHints();
        List<Position> moves = new ArrayList<>();

        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if (game.board.getCells()[i][j].isHint()) {
                    moves.add(new Position(i, j));
                }
            }
        }
        game.move(moves.get(new Random().nextInt(moves.size())));
        changeTurn();
    }

    // Miramos para cada uno de los hint el número de fichas que gira, y nos movemos a uno de los
    // que más fichas a girado.
    public void turnoCPUDificil(){
        game.setHints();
        final Cell[][] initial = game.board.getCells();
        int initial_whites = game.board.white;
        int initial_blacks = game.board.black;
        int maxNumFichas = 0;
        List<Position> best_moves = new ArrayList<>();

        for (int i = 0; i < medida; i++) {
            for (int j = 0; j < medida; j++) {
                if (game.board.getCells()[i][j].isHint()) {
                    int numFichas = game.numFichas(new Position(i, j), initial_whites);
                    if(numFichas == maxNumFichas){
                        best_moves.add(new Position(i, j));
                    }
                    if(numFichas > maxNumFichas){
                        maxNumFichas = numFichas;
                        best_moves = new ArrayList<>();
                        best_moves.add(new Position(i, j));
                    }
                    game.board.setStartRoundValues(initial, initial_whites, initial_blacks);
                }
            }
        }
        game.board.setStartRoundValues(initial, initial_whites, initial_blacks);
        game.move(best_moves.get(new Random().nextInt(best_moves.size())));
        changeTurn();
    }

    // Llamará al método changeTurn de la clase Game, realizando la acción pertinente, dependiendo de a que estado se ha cambiado.
    public void changeTurn(){
        // Si el jugador ha hecho undo y realiza un movimiento, los datos almacenados a partir de aquí se borran.
        if(getState() == State.BLACK_TURN){
            if(contador != 0){
                removeArrayFromIndex(board_state.size() - contador - 1);
            }
        }

        game.changeTurn();
        updateGrid();

        // Si el estado es blanco, realizamos el turno de la CPU.
        if(getState() == State.WHITE_TURN){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    turnoCPU();
                }
            }, 1000);

        // Si el estado es Finished, ha habido bloqueo. Ningún jugador a podido tirar.
        } else if(getState() == State.FINISHED){
            if((medida*medida - (game.board.black + game.board.white)) > 0){
                showToast(R.drawable.shape_toast_red, R.drawable.bloqueo, getString(R.string.bloqueo_solo));
                goToResults(String.format(getString(R.string.bloqueo), String.valueOf(game.board.black), String.valueOf(game.board.white), String.valueOf(Math.abs(game.board.black - game.board.white)), String.valueOf(medida*medida - (game.board.black + game.board.white))));
            }
        }

        // Tablero completado, miramos el motivo del fin de la partida.
        if(((this.medida*this.medida) - game.board.white - game.board.black) == 0) {
            game.state = State.FINISHED;
            checkFinish();
        }
    }

    private void checkFinish(){
        if(game.board.black > game.board.white){
            showToast(R.drawable.shape_toast_green, R.drawable.victoria, getString(R.string.has_ganado_solo));
            goToResults(String.format(getString(R.string.has_ganado), String.valueOf(game.board.black), String.valueOf(game.board.white), String.valueOf(game.board.black - game.board.white)));
        } else if(game.board.black < game.board.white){
            showToast(R.drawable.shape_toast_red, R.drawable.derrota, getString(R.string.has_perdido_solo));
            goToResults(String.format(getString(R.string.has_perdido), String.valueOf(game.board.white), String.valueOf(game.board.black), String.valueOf(game.board.white - game.board.black)));
        } else if(game.board.black == game.board.white){
            showToast(R.drawable.shape_toast_grey, R.drawable.empate, getString(R.string.habeis_empatado));
            goToResults(getString(R.string.habeis_empatado));
        }
    }

    // Muestra el Toast personalizado, el cuál consta de un ImageView y un TextView.
    public void showToast(int toast_shape, int image, String msg){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast));
        layout.setBackgroundResource(toast_shape);

        ImageView imageView = (ImageView) layout.findViewById(R.id.toast_iv);
        imageView.setBackgroundResource(image);

        TextView text = (TextView) layout.findViewById(R.id.toast_tv);
        text.setText(msg);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public State getState(){
        return this.game.state;
    }

}
