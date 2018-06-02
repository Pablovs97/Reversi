package com.example.pablovilas.reversi.activities;

import android.os.Bundle;
import android.view.View;

import com.example.pablovilas.reversi.R;

public class Ayuda extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda);
    }

    public void volver(View view){
        finish();
    }
}
