package com.yandi.yarud.scannerabsensiupi;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yandi.yarud.scannerabsensiupi.models.Ruangan;
import com.yandi.yarud.scannerabsensiupi.networks.Config;
import com.yandi.yarud.scannerabsensiupi.utils.CheckConnection;
import com.yandi.yarud.scannerabsensiupi.utils.DBHandler;
import com.yandi.yarud.scannerabsensiupi.utils.NetworkStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView scannerButton, settingButton;
    private TextView namaRuangan, informasiText;
    private ProgressBar progressBar;
    final int waktu = 60 * 1000;
    private Timer timer;
    private DBHandler dbHandler;
    private String koderuanganDB, namaRuanganDB;
    private ConstraintLayout layarUtama;
    private ArrayList<String> listnim;

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
        } else {
            namaRuangan.setText(namaRuanganDB);
            namaRuangan.setVisibility(View.VISIBLE);
        }
    }

    private void isiFormRuangan() {
        Intent intent = new Intent(MainActivity.this, FormRuanganActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDatabase() {
        try {
            List<Ruangan> listRuangan = dbHandler.getAllRuangan();
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
        layarUtama.setOnClickListener(this);
    }

    private void initView() {
        scannerButton = findViewById(R.id.MainCardViewMhsAbsen);
        settingButton = findViewById(R.id.MainCardViewSetting);
        namaRuangan = findViewById(R.id.textViewNamaRuangan);
        informasiText = findViewById(R.id.textViewInfo);
        progressBar = findViewById(R.id.progressBar);
        layarUtama = findViewById(R.id.MainConstraint);
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
                cekJadwalRuangan();
                break;
            case R.id.MainCardViewSetting:
                keamananDialog();
                break;
            case R.id.MainConstraint:
                new CheckConnection();
                if (!CheckConnection.apakahTerkoneksiKeInternet(MainActivity.this)){
                    Toast.makeText(this, "Belum ada koneksi Internet", Toast.LENGTH_SHORT).show();
                    displayFailed();
                } else {
                    displaySuccess();
                }
                break;
        }
    }

    private void cekJadwalRuangan(){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_JADWAL + namaRuanganDB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                                String kodemk = jsonObject.getString("kodemk");
//                                String hari = jsonObject.getString("hari");
//                                String jam1 = jsonObject.getString("jam1");
//                                String jam2 = jsonObject.getString("jam2");
                                String idmk = jsonObject.getString("id");
                                getMahasiswa(idmk);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Tidak ada Jadwal", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "svc", "kambinggulingmbe");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getMahasiswa(String idmk) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_MHS_MATKUL + idmk,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listnim = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                listnim.add(jsonObject.getString("NIM"));
                            }
                            String ListNim = String.valueOf(listnim);
                            keScanActivity(ListNim);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Tidak ada Jadwal", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "svc", "kambinggulingmbe");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void keScanActivity(String listNim) {
        Toast.makeText(this, listNim, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ScanQRActivity.class);
        intent.putExtra("LISTNIM",listNim);
        startActivity(intent);
    }

    private void keamananDialog() {
        final String usernameEdit = "admin", kataSandi = "admin123";
        @SuppressLint("InflateParams") View subview = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        final EditText subUserAdmin = subview.findViewById(R.id.userAdmin);
        final EditText subPasswordAdmin = subview.findViewById(R.id.passwordAdmin);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.todoDialogLight);
        builder.setIcon(R.drawable.ic_info)
                .setTitle("Keamanan")
                .setMessage("Masukan user & password admin untuk merubah data ruangan !")
                .setView(subview)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (subUserAdmin.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "Masukan Username", Toast.LENGTH_SHORT).show();
                        } else if (subPasswordAdmin.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "Masukan Password", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!subUserAdmin.getText().toString().equals(usernameEdit)){
                                Toast.makeText(MainActivity.this, "Username salah", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!subPasswordAdmin.getText().toString().equals(kataSandi)){
                                    Toast.makeText(MainActivity.this, "Password salah", Toast.LENGTH_SHORT).show();
                                } else {
                                    keFormRuanganActivity();
                                }
                            }
                        }
                    }
                })
                .setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button yes = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        yes.setTextColor(Color.rgb(29,145,36));
    }

    private void keFormRuanganActivity() {
        dbHandler.deleteRuangan();
        Intent intent = new Intent(MainActivity.this, FormRuanganActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
