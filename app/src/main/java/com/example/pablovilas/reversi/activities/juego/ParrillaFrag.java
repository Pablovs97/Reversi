package com.example.pablovilas.reversi.activities.juego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pablovilas.reversi.R;
import com.example.pablovilas.reversi.activities.ImageAdapter;
import com.example.pablovilas.reversi.activities.Resultados;
import com.example.pablovilas.reversi.logica_juego.Board;
import com.example.pablovilas.reversi.logica_juego.Cell;
import com.example.pablovilas.reversi.logica_juego.Game;
import com.example.pablovilas.reversi.logica_juego.Position;
import com.example.pablovilas.reversi.logica_juego.State;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ParrillaFrag extends Fragment {

    TextView numBlacks, numWhites, numEmpty, textTimer, turno;
    int medida, tiempo, contador;
    boolean control_tiempo;
    boolean primer = true;
    String alias, dificultad;
    public Game game;
    GridView gv;
    ImageAdapter adapter;
    private static CountDownTimer cd;
    long timeLeft, initialTime;
    List<Board> board_state;
    Button undo, redo;
    private UpgradeLogListener listener;
    String inicio_tirada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.juego, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        numBlacks = (TextView) getView().findViewById(R.id.num_blacks);
        numWhites = (TextView) getView().findViewById(R.id.num_whites);
        numEmpty = (TextView) getView().findViewById(R.id.num_empty);
        textTimer = (TextView) getView().findViewById(R.id.time_min_sec);
        turno = (TextView) getView().findViewById(R.id.turno);

        undo = (Button) getView().findViewById(R.id.undo);
        redo = (Button) getView().findViewById(R.id.redo);

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redo();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        medida = Integer.parseInt(prefs.getString(getResources().getString(R.string.key_medida), "8"));
        alias = prefs.getString(getResources().getString(R.string.key_alias), "Player 1");
        tiempo = medida*20;
        control_tiempo = prefs.getBoolean(getResources().getString(R.string.key_control), false);
        dificultad = prefs.getString(getResources().getString(R.string.key_dificultad), "Normal");

        // CPU level y alias en pantalla.
        TextView cpu_level = (TextView) getView().findViewById(R.id.cpu_level);
        cpu_level.setText(dificultad);
        TextView player_alias = (TextView) getView().findViewById(R.id.player_alias);
        player_alias.setText(alias);

        // Color rojo para el temporizador si hay control de tiempo.
        if(control_tiempo) textTimer.setTextColor(getResources().getColor(R.color.red));

        // Nuevo juego.
        game = new Game(new Board(medida));

        // Lista de boards para Undo/Redo
        board_state = new ArrayList<>();
        board_state.add(new Board(game.board));

        // GridView -> Parrilla
        gv = (GridView) getView().findViewById(R.id.gridView);
        gv.setNumColumns(medida);
        adapter = new ImageAdapter(getActivity(), game.board.cells, this);
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

        initialLog();
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        try {
            listener = (UpgradeLogListener) c;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(c.toString() + " must implement onClickUpgradeLog");
        }
    }

    public void initialLog(){
        String str = String.format(getString(R.string.log_alias_medida), alias, medida);
        if(control_tiempo){
            str += getString(R.string.log_si_control);
        } else {
            str += getString(R.string.log_no_control);
        }
        str += "\n";
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", new Locale("es", "ES"));
        Date date = new Date();
        inicio_tirada = dateFormat.format(date);
        listener.onClickUpgradeLog(str);
    }

    public void undo(){
        // Sólo puede hacerse Undo cuando es el turno del jugador
        if(getState() == State.WHITE_TURN){
            Toast.makeText(getActivity(), "Espera tu turno", Toast.LENGTH_SHORT).show();
        } else if (getState() == State.BLACK_TURN) {
            if (board_state.size() - contador - 1 > 0) {
                contador++;
                game.board = new Board(board_state.get(board_state.size() - contador - 1));
                adapter = new ImageAdapter(getActivity(), game.board.cells, this);
                gv.setAdapter(adapter);
                setNumFichas();
            } else { // No podemos ir más para detrás.
                showToast(R.drawable.shape_toast_grey, R.drawable.invalid, getString(R.string.no_undo));
            }
        }
    }

    public void redo(){
        // Sólo puede hacerse Redo cuando es el turno del jugador
        if(getState() == State.WHITE_TURN){
            Toast.makeText(getActivity(), "Espera tu turno", Toast.LENGTH_SHORT).show();
        } else if (getState() == State.BLACK_TURN) {
            if (board_state.size() - contador < board_state.size()) {
                contador--;
                game.board = new Board(board_state.get(board_state.size() - contador - 1));
                adapter = new ImageAdapter(getActivity(), game.board.cells, this);
                gv.setAdapter(adapter);
                setNumFichas();
            } else { // No podemos ir más para delante.
                showToast(R.drawable.shape_toast_grey, R.drawable.invalid, getString(R.string.no_redo));
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
    public void onSaveInstanceState(Bundle outState) {
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
    public void onPause() {
        super.onPause();
        // Paramos el contador.
        cd.cancel();
        cd = null;
    }

    @Override
    public void onResume() {
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
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            game.board.cells = (Cell[][]) savedInstanceState.getSerializable("Board");
            game.board.black = savedInstanceState.getInt("Blacks");
            game.board.white = savedInstanceState.getInt("Whites");
            setNumFichas();
            adapter = new ImageAdapter(getActivity(), game.board.cells, this);
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

    public void goToResults(String msg, String state){
        final Intent intent = new Intent(getActivity(), Resultados.class);
        intent.putExtra("Log", msg + String.format(getString(R.string.Log), this.alias, String.valueOf(this.medida), String.valueOf(tiempo - timeLeft/1000)));
        intent.putExtra("alias", String.valueOf(this.alias));
        intent.putExtra("medida", String.valueOf(this.medida));
        intent.putExtra("control", String.valueOf(this.control_tiempo));
        intent.putExtra("num_blacks", String.valueOf(this.game.board.black));
        intent.putExtra("num_whites", String.valueOf(this.game.board.white));
        intent.putExtra("total_time", String.valueOf(tiempo - timeLeft/1000));
        intent.putExtra("state", state);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                getActivity().finish();
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
                goToResults(String.format(getString(R.string.tiempo_agotado), String.valueOf(game.board.black), String.valueOf(game.board.white), String.valueOf(Math.abs(game.board.black - game.board.white)), String.valueOf(medida*medida - (game.board.black + game.board.white))), "TIEMPO AGOTADO");
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
                goToResults(String.format(getString(R.string.bloqueo), String.valueOf(game.board.black), String.valueOf(game.board.white), String.valueOf(Math.abs(game.board.black - game.board.white)), String.valueOf(medida*medida - (game.board.black + game.board.white))), "BLOQUEO");
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
            goToResults(String.format(getString(R.string.has_ganado), String.valueOf(game.board.black), String.valueOf(game.board.white), String.valueOf(game.board.black - game.board.white)), "VICTORIA");
        } else if(game.board.black < game.board.white){
            showToast(R.drawable.shape_toast_red, R.drawable.derrota, getString(R.string.has_perdido_solo));
            goToResults(String.format(getString(R.string.has_perdido), String.valueOf(game.board.white), String.valueOf(game.board.black), String.valueOf(game.board.white - game.board.black)), "DERROTA");
        } else if(game.board.black == game.board.white){
            showToast(R.drawable.shape_toast_grey, R.drawable.empate, getString(R.string.habeis_empatado));
            goToResults(getString(R.string.habeis_empatado), "EMPATE");
        }
    }

    // Muestra el Toast personalizado, el cuál consta de un ImageView y un TextView.
    public void showToast(int toast_shape, int image, String msg){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) getView().findViewById(R.id.toast));
        layout.setBackgroundResource(toast_shape);

        ImageView imageView = (ImageView) layout.findViewById(R.id.toast_iv);
        imageView.setBackgroundResource(image);

        TextView text = (TextView) layout.findViewById(R.id.toast_tv);
        text.setText(msg);

        Toast toast = new Toast(getActivity());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public State getState(){
        return this.game.state;
    }

    public interface UpgradeLogListener {
        void onClickUpgradeLog(String str);
    }

    public void setUpgradeLogListener(UpgradeLogListener listener) {
        this.listener = listener;
    }

    public void updateLog(String s){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", new Locale("es", "ES"));
        Date date = new Date();
        String fin_tirada = dateFormat.format(date);
        s += String.format(getString(R.string.log_casillas), medida*medida - (game.board.black + game.board.white), inicio_tirada, fin_tirada);
        if(control_tiempo){
            s += String.format(getString(R.string.log_tiempo), textTimer.getText());
        }
        s += "\n";
        inicio_tirada = fin_tirada;
        listener.onClickUpgradeLog(s);
    }
}
