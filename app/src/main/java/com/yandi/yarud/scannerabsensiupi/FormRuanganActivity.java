package com.yandi.yarud.scannerabsensiupi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yandi.yarud.scannerabsensiupi.models.Ruangan;
import com.yandi.yarud.scannerabsensiupi.networks.Config;
import com.yandi.yarud.scannerabsensiupi.utils.CheckConnection;
import com.yandi.yarud.scannerabsensiupi.utils.DBHandler;
import com.yandi.yarud.scannerabsensiupi.utils.GetTokenUPI;
import com.yandi.yarud.scannerabsensiupi.utils.NetworkStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class FormRuanganActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private Spinner spinner;
    private ArrayList<String> ruangans;
    private JSONArray dt_mk;
    private String username = "1600862";
    private Button input_wd_btn_selesai;
    private TextView textViewRuangan,textViewKodeRuangan;
    private ProgressBar progressBarIsiForm;
    private DBHandler db;
    private Timer timer;
    final int waktu = 10 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ruangan);
        input_wd_btn_selesai = findViewById(R.id.input_wd_btn_selesai);
        spinner = findViewById(R.id.spinner);
        textViewKodeRuangan = findViewById(R.id.textViewKodeRuangan);
        textViewRuangan = findViewById(R.id.textViewRuangan);
        progressBarIsiForm = findViewById(R.id.progressBarIsiForm);
        db = new DBHandler(FormRuanganActivity.this);
        ruangans = new ArrayList<>();
        spinner.setOnItemSelectedListener(FormRuanganActivity.this);
        displayLoading();
        initRunning();

        input_wd_btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanKeDatabase();
            }
        });
    }

    private void initRunning(){
        if (!CheckConnection.apakahTerkoneksiKeInternet(this)){
            Toast.makeText(getApplicationContext(),"Tidak ada koneksi Internet",Toast.LENGTH_SHORT).show();
            displayFailed();
        } else {
            GetTokenUPI token = new GetTokenUPI(this,"FormRuangan");
            String password = "intancantik";
            token.getToken(username, password);
        }
    }

    public void displayFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input_wd_btn_selesai.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                textViewKodeRuangan.setVisibility(View.GONE);
                textViewRuangan.setText(R.string.no_connection);
                progressBarIsiForm.setVisibility(View.GONE);
            }
        });
    }

    public void displaySuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input_wd_btn_selesai.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                textViewKodeRuangan.setVisibility(View.VISIBLE);
                textViewRuangan.setText(R.string.isi_form_ruangan_title);
                progressBarIsiForm.setVisibility(View.GONE);
            }
        });
    }

    private void displayLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input_wd_btn_selesai.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                textViewKodeRuangan.setVisibility(View.GONE);
                textViewRuangan.setText(R.string.isi_form_ruangan_title);
                progressBarIsiForm.setVisibility(View.VISIBLE);
            }
        });
    }

    public void RunningPage(final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_MahasiswaKontrak + username,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j;
                        try {
                            j = new JSONObject(response);
                            dt_mk = j.getJSONArray(Config.JSON_ARRAY);
                            getRuangan(dt_mk);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders(){
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(FormRuanganActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getRuangan(JSONArray j) {
        ruangans.add("Pilih Ruangan ...");
        for (int i=0; i<j.length();i++){
            try {
                JSONObject jsonObject = j.getJSONObject(i);
                ruangans.add(jsonObject.getString(Config.TAG_RUANGAN));
                displaySuccess();
            } catch (JSONException e) {
                displayFailed();
                e.printStackTrace();
            }
        }
        spinner.setAdapter(new ArrayAdapter<>(FormRuanganActivity.this, android.R.layout.simple_spinner_dropdown_item, ruangans));
    }

    private String getKodeMK(int position){
        String kodeMK = "";
        try {
            JSONObject jsonObject = dt_mk.getJSONObject(position-1);
            kodeMK = jsonObject.getString(Config.TAG_KODERUANGAN);
            displaySuccess();
        } catch (JSONException e) {
            displayFailed();
            e.printStackTrace();
        }
        return kodeMK;
    }

    private void simpanKeDatabase() {
        if (spinner.getSelectedItem().toString().equals("Pilih Ruangan ...")){
            Toast.makeText(this, "Anda belum memilih Ruangan", Toast.LENGTH_SHORT).show();
        } else {
            if (!CheckConnection.apakahTerkoneksiKeInternet(this)){
                Toast.makeText(getApplicationContext(),"Tidak ada koneksi Internet",Toast.LENGTH_SHORT).show();
                displayFailed();
            } else {
                String kodeRuangan = "";
                List<Ruangan> listRuangan = db.getAllRuangan();
                for (Ruangan ruangan: listRuangan){
                    kodeRuangan = ruangan.getKodeRuangan();
                }
                if (kodeRuangan == null){
                    db.addRuangan(new Ruangan(1,textViewKodeRuangan.getText().toString(),spinner.getSelectedItem().toString()));
                } else if (kodeRuangan.equals("")) {
                    db.addRuangan(new Ruangan(1, textViewKodeRuangan.getText().toString(), spinner.getSelectedItem().toString()));
                } else {
                    db.updateRuangan(new Ruangan(1, textViewKodeRuangan.getText().toString(), spinner.getSelectedItem().toString()));
                }
                keMainActivity();

            }
        }
    }

    private void keMainActivity() {
        Intent intent = new Intent(FormRuanganActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String test = spinner.getSelectedItem().toString();
        if (test.equals("Pilih Ruangan ...")){
            textViewKodeRuangan.setText("");
        } else {
            textViewKodeRuangan.setText(getKodeMK(position));
        }
        displaySuccess();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textViewKodeRuangan.setText("");
    }

    private void cekInternet(){
        timer = new Timer();
        timer.schedule(new NetworkStatus(this, "form"),0,waktu);
    }

    private void matikanPengecekanInternet(){
        timer.cancel();
        timer.purge();
        timer = new Timer();
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
