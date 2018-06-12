package com.example.pablovilas.reversi.bbdd;

/**
 * Created by Pablo Vilas on 02/06/2018.
 */

public class PartidasClass {
    private String alias;
    private String date;
    private String medida;
    private String control;
    private String num_blacks;
    private String num_whites;
    private String total_time;
    private String state;

    public PartidasClass(){}

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

    public String getMedida() {
        return medida;
    }

    public String getControl() {
        return control;
    }

    public String getNum_blacks() {
        return num_blacks;
    }

    public String getNum_whites() {
        return num_whites;
    }

    public String getTotal_time() {
        return total_time;
    }

    public String getState() {
        return state;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public void setNum_blacks(String num_blacks) {
        this.num_blacks = num_blacks;
    }

    public void setNum_whites(String num_whites) {
        this.num_whites = num_whites;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.getState() + "\n" +
                "Alias: " + this.getAlias() + "\n" +
                "Fecha: " + this.getDate() + "\n" +
                "Medida: " + this.getMedida() + "x" + this.getMedida() + " casillas\n" +
                "Num. fichas negras: " + this.getNum_blacks() + "\n" +
                "Num. fichas blancas: " + this.getNum_whites() + "\n" +
                "Tiempo total: " + this.getTotal_time() + " segundos\n";
    }
}
