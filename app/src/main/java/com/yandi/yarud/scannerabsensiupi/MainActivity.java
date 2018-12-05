package com.yandi.yarud.scannerabsensiupi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yandi.yarud.scannerabsensiupi.models.Ruangan;
import com.yandi.yarud.scannerabsensiupi.utils.DBHandler;
import com.yandi.yarud.scannerabsensiupi.utils.NetworkStatus;

import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView scannerButton, settingButton;
    private TextView namaRuangan, informasiText;
    private ProgressBar progressBar;
    final int waktu = 5000;
    private Timer timer;
    private DBHandler dbHandler;
    private String koderuanganDB, namaRuanganDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListenner();
        initRunning();
    }

    private void initRunning() {
        displayLoading();
        getDatabase();
        if (koderuanganDB == null){
            isiFormRuangan();
        } else if (koderuanganDB.equals("")){
            isiFormRuangan();
        }
    }

    private void isiFormRuangan() {
        Intent intent = new Intent(MainActivity.this, FormRuanganActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDatabase() {
        try {
            List<Ruangan> listRuangan = dbHandler.getRuangan();
            for (Ruangan ruangan: listRuangan){
                koderuanganDB = ruangan.getKodeRuangan();
                namaRuanganDB = ruangan.getRuangan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbHandler.close();
    }

    private void displayLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                scannerButton.setVisibility(View.GONE);
            }
        });
    }

    public void displayFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                scannerButton.setVisibility(View.GONE);
                informasiText.setText(R.string.no_internet);
            }
        });
    }

    public void displaySuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                scannerButton.setVisibility(View.VISIBLE);
                informasiText.setText(R.string.perhatian);
            }
        });
    }

    private void initListenner() {
        scannerButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        informasiText.setText(R.string.perhatian);
        namaRuangan.setVisibility(View.GONE);
        namaRuangan.setText(R.string.nama_ruangan);
        dbHandler = new DBHandler(MainActivity.this);
    }

    private void initView() {
        scannerButton = findViewById(R.id.MainCardViewMhsAbsen);
        settingButton = findViewById(R.id.MainCardViewSetting);
        namaRuangan = findViewById(R.id.textViewNamaRuangan);
        informasiText = findViewById(R.id.textViewInfo);
        progressBar = findViewById(R.id.progressBar);
    }

    private void cekInternet(){
            timer = new Timer();
            timer.schedule(new NetworkStatus(this, "main"),0,waktu);
    }

    private void matikanPengecekanInternet(){
            timer.cancel();
            timer.purge();
            timer = new Timer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MainCardViewMhsAbsen:
                //ke Scanner
                matikanPengecekanInternet();
                break;
            case R.id.MainCardViewSetting:
                //ke Pengaturan
                matikanPengecekanInternet();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        matikanPengecekanInternet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cekInternet();
    }
}
