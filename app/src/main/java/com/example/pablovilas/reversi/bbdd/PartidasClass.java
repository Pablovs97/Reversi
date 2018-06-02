package com.example.pablovilas.reversi.bbdd;

/**
 * Created by Pablo Vilas on 02/06/2018.
 */

public class PartidasClass {
    String alias;
    String date;
    String medida;
    String control;
    String num_blacks;
    String num_whites;
    String total_time;
    String state;

    public PartidasClass(String alias, String date, String medida, String control, String num_blacks, String num_whites, String total_time, String state){
        this.alias = alias;
        this.date = date;
        this.medida = medida;
        this.control = control;
        this.num_blacks = num_blacks;
        this.num_whites = num_whites;
        this.total_time = total_time;
        this.state = state;
    }

    public PartidasClass(String alias, String date, String state){
        this.alias = alias;
        this.date = date;
        this.state = state;
    }

    public String getAlias() {
        return alias;
    }

    public String getDate() {
        return date;
    }

    public String getState() {
        return state;
    }
}
