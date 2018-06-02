package com.example.pablovilas.reversi.activities.juego;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.example.pablovilas.reversi.R;

public class JuegoActivity extends FragmentActivity implements ParrillaFrag.UpgradeLogListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego_activity);
        ParrillaFrag frgListado = (ParrillaFrag) getSupportFragmentManager().findFragmentById(R.id.parrilla_frag);
        frgListado.setUpgradeLogListener(this);
    }

    @Override
    public void onClickUpgradeLog(String str) {
        LogFrag fgdet = (LogFrag) getSupportFragmentManager().findFragmentById(R.id.log_frag);

        if (fgdet != null && fgdet.isInLayout()) {
            LogFrag fdetail = (LogFrag) getSupportFragmentManager().findFragmentById(R.id.log_frag);
            fdetail.addLog(str);
        }
    }
}
