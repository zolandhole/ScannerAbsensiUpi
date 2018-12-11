package com.yandi.yarud.scannerabsensiupi;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.android.volley.toolbox.JsonArrayRequest;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class FormRuanganActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener, View.OnClickListener {


    private ConstraintLayout constraintTitle,constrainFak,constrainRuangan;
    private ProgressBar progressBarForm;
    private Button btn_selesai;
    private TextView textViewTitle,textViewFak;
    private JSONArray dataFak, dataRuangan;
    private ArrayList<String> ruangans, fakultas;
    private Spinner spinnerFak, spinnerRuangan;
    private Timer timer;
    final int waktu = 10 * 1000;
    private String kodeFak;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ruangan);

        initView();
        initListener();
        initRunning();
    }
    
    private void pageLoading(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarForm.setVisibility(View.VISIBLE);
                constraintTitle.setVisibility(View.GONE);
                constrainFak.setVisibility(View.GONE);
                constrainRuangan.setVisibility(View.GONE);
                btn_selesai.setVisibility(View.GONE);     
            }
        });
    }

    private void pageFailed(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarForm.setVisibility(View.GONE);
                constraintTitle.setVisibility(View.VISIBLE);
                textViewTitle.setText(R.string.no_connection);
                constrainFak.setVisibility(View.GONE);
                constrainRuangan.setVisibility(View.GONE);
                btn_selesai.setVisibility(View.GONE);     
            }
        });
    }

    private void pageSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarForm.setVisibility(View.GONE);
                constraintTitle.setVisibility(View.VISIBLE);
                textViewTitle.setText(R.string.isi_form_ruangan_title);
                constrainFak.setVisibility(View.VISIBLE);
                constrainRuangan.setVisibility(View.GONE);
                btn_selesai.setVisibility(View.GONE);     
            }
        });
    }
    
    private void initView() {
        constraintTitle = findViewById(R.id.constraintTitle);
        constrainFak = findViewById(R.id.constrainFak);
        constrainRuangan = findViewById(R.id.constrainRuangan);
        progressBarForm = findViewById(R.id.progressBarForm);
        btn_selesai = findViewById(R.id.btn_selesai);
        textViewTitle = findViewById(R.id.textViewTitle);
        spinnerFak = findViewById(R.id.spinnerFak);
        textViewFak = findViewById(R.id.textViewFak);
        spinnerRuangan = findViewById(R.id.spinnerRuangan);
    }

    private void initListener() {
        dbHandler = new DBHandler(FormRuanganActivity.this);
        ruangans = new ArrayList<>();
        fakultas = new ArrayList<>();
        spinnerFak.setOnItemSelectedListener(FormRuanganActivity.this);
        btn_selesai.setOnClickListener(FormRuanganActivity.this);
        spinnerRuangan.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String ruangan = spinnerRuangan.getSelectedItem().toString();
                if (ruangan.equals("Pilih Ruangan ...")){
                    Toast.makeText(FormRuanganActivity.this, "Silahkan Pilih Ruangan", Toast.LENGTH_SHORT).show();
                } else {
                    btn_selesai.setVisibility(View.VISIBLE);
                    Toast.makeText(FormRuanganActivity.this, "Klik Selesai untuk menyimpan ke Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ruangans.clear();
            }
        });
    }

    private void initRunning(){
        pageLoading();
        if (!CheckConnection.apakahTerkoneksiKeInternet(this)){
            Toast.makeText(getApplicationContext(),"Tidak ada koneksi Internet",Toast.LENGTH_SHORT).show();
            pageFailed();
        } else {
            ambilDataFak();
        }
    }

    private void ambilDataFak() {
        RequestQueue requestQueue = Volley.newRequestQueue(FormRuanganActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.URL_FAK, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dataFak = response;
                        getFakultas(dataFak);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pageFailed();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s","svc","kambinggulingmbe");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void getFakultas(JSONArray jsonArray) {
        fakultas.add("Pilih Fakultas ...");
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                fakultas.add(jsonObject.getString("NAMAFAK"));
                pageSuccess();
            } catch (JSONException e) {
                pageFailed();
                e.printStackTrace();
            }
        }
        spinnerFak.setAdapter(new ArrayAdapter<>(FormRuanganActivity.this, android.R.layout.simple_spinner_dropdown_item, fakultas));
    }

    private String getKodeFak(int position){
        kodeFak = "";
        try {
            JSONObject jsonObject = dataFak.getJSONObject(position-1);
            kodeFak = jsonObject.getString("KODEFAK");
            pageSuccess();
        } catch (JSONException e) {
            pageFailed();
            e.printStackTrace();
        }
        return kodeFak;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String fakultas = spinnerFak.getSelectedItem().toString();
        if (fakultas.equals("Pilih Fakultas ...")){
            Toast.makeText(this, "Silahkan Pilih Fakultas", Toast.LENGTH_SHORT).show();
            constrainRuangan.setVisibility(View.GONE);
            textViewFak.setText("");
        } else {
            ruangans.clear();
            textViewFak.setText(getKodeFak(position));
            kodeFak = textViewFak.getText().toString().trim();
            constrainRuangan.setVisibility(View.VISIBLE);
            progressBarForm.setVisibility(View.VISIBLE);
            ambilDataRuangan(kodeFak);
            Log.e("YARUD", kodeFak);
        }
    }

    private void ambilDataRuangan(String kodeFak) {
        progressBarForm.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(FormRuanganActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.URL_RUANGAN+kodeFak, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dataRuangan = response;
                        getRuangan(dataRuangan);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pageFailed();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s","svc","kambinggulingmbe");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void getRuangan(JSONArray jsonArray) {
        ruangans.add("Pilih Ruangan ...");
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ruangans.add(jsonObject.getString("KODERUANG"));
                ruangans.removeAll(Collections.singletonList(""));
                ruangans.removeAll(Collections.singletonList("null"));
                progressBarForm.setVisibility(View.GONE);
            } catch (JSONException e) {
                pageFailed();
                e.printStackTrace();
            }
        }
        spinnerRuangan.setAdapter(new ArrayAdapter<>(FormRuanganActivity.this, android.R.layout.simple_spinner_dropdown_item, ruangans));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_selesai:
                simpanKeDatabase();
                break;
        }
    }

    private void simpanKeDatabase() {
        if (spinnerRuangan.getSelectedItem().toString().equals("Pilih Ruangan ...")){
            Toast.makeText(this, "Anda belum memilih Ruangan", Toast.LENGTH_SHORT).show();
        } else {
            if (!CheckConnection.apakahTerkoneksiKeInternet(this)){
                Toast.makeText(getApplicationContext(),"Tidak ada koneksi Internet",Toast.LENGTH_SHORT).show();
            } else {
                String kodeRuangan = "";
                List<Ruangan> listRuangan = dbHandler.getAllRuangan();
                for (Ruangan ruangan: listRuangan){
                    kodeRuangan = ruangan.getKodeRuangan();
                }
                if (kodeRuangan == null){
                    dbHandler.addRuangan(new Ruangan(1,textViewFak.getText().toString(),spinnerRuangan.getSelectedItem().toString()));
                } else if (kodeRuangan.equals("")) {
                    dbHandler.addRuangan(new Ruangan(1,textViewFak.getText().toString(),spinnerRuangan.getSelectedItem().toString()));
                } else {
                    dbHandler.updateRuangan(new Ruangan(1,textViewFak.getText().toString(),spinnerRuangan.getSelectedItem().toString()));
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
    public void onNothingSelected(AdapterView<?> parent) {
        textViewFak.setText("");
        constrainRuangan.setVisibility(View.GONE);
        ruangans.clear();
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
