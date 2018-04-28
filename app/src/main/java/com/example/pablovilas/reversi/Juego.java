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
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;
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
    CountDownTimer cd;

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
        //outState.putString("Tiempo", this.textTimer.getText().toString());
        outState.putInt("Tiempo", tiempo);
        cd.cancel();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            board.cells = (Cell[][]) savedInstanceState.getSerializable("Board");
            adapter = new ImageAdapter(this, board.cells, this);
            //textTimer.setText(savedInstanceState.getString("Tiempo"));
            tiempo = (int) savedInstanceState.getInt("Tiempo");
            cd.start();
            gv.setAdapter(adapter);
        }
    }

/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cd.cancel();
        outState.putString("Tiempo", this.textTimer.getText().toString());
        outState.putSerializable("Board", this.board.getCells());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cd.start();
        if (savedInstanceState != null){
            board.setCells((Cell[][]) savedInstanceState.getSerializable("Board"));
            textTimer.setText(savedInstanceState.getString("Tiempo"));
            adapter = new ImageAdapter(this, board.cells, this);
            gv.setAdapter(adapter);
        }
    }
*/
    public void goToResults(){
        Intent intent = new Intent(this, Resultados.class);
        intent.putExtra("Log", "Alias: " + this.alias + "\nMedida parrilla: " + String.valueOf(this.medida) + "\nTiempo total");
        finish();
        startActivity(intent);
    }

    public void startTimer(){
        cd = new CountDownTimer(tiempo * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format("Time Remaining %02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                textTimer.setText(text);
                tiempo--;
            }

            public void onFinish() {
                textTimer.setText("Time's up!");
                goToResults();
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
                    this.board.cells[i][j].setEmpty();
                } else if(this.board.cells[i][j].isNewBlack() && this.state == State.BLACK) {
                    this.board.cells[i][j].setBlack();
                } else if(this.board.cells[i][j].isNewWhite() && this.state == State.WHITE) {
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
        tv5.append("(" + String.valueOf(pos.getRow()) + "," + String.valueOf(pos.getColumn()) + ") " + String.valueOf(maxNumFichas) + " | ");
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
        if (this.state != State.FINISHED) {
            if (canPlay(getJugadorContrario())) {
                this.state = getJugadorContrario();
                tv3.setText(this.state.toString());
            } else if (!canPlay(this.state)) {
                this.state = State.FINISHED;
            }
        }
        if(this.state == State.WHITE) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    turnoCPU();
                    updateGrid();
                }
            }, 1500);
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
