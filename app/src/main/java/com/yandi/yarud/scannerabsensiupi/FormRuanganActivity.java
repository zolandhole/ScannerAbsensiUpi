package com.yandi.yarud.scannerabsensiupi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.yandi.yarud.scannerabsensiupi.networks.Config;
import com.yandi.yarud.scannerabsensiupi.utils.CheckConnection;
import com.yandi.yarud.scannerabsensiupi.utils.GetTokenUPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormRuanganActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private Spinner spinner;
    private EditText editTextNamaRuangan;
    private ArrayList<String> ruangans;
    private JSONArray dt_mk;
    private String username = "1600862";
    private Button input_wd_btn_selesai;
    private TextView textViewRuangan;
    private ProgressBar progressBarIsiForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ruangan);
        input_wd_btn_selesai = findViewById(R.id.input_wd_btn_selesai);
        spinner = findViewById(R.id.spinner);
        editTextNamaRuangan = findViewById(R.id.editTextNamaRuangan);
        textViewRuangan = findViewById(R.id.textViewRuangan);
        progressBarIsiForm = findViewById(R.id.progressBarIsiForm);

        ruangans = new ArrayList<>();
        spinner.setOnItemSelectedListener(FormRuanganActivity.this);
        displayLoading();
        initRunning();
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
        input_wd_btn_selesai.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        editTextNamaRuangan.setVisibility(View.GONE);
        textViewRuangan.setText(R.string.no_connection);
        progressBarIsiForm.setVisibility(View.GONE);
    }

    private void displaySuccess() {
        input_wd_btn_selesai.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        editTextNamaRuangan.setVisibility(View.VISIBLE);
        textViewRuangan.setText(R.string.isi_form_ruangan_title);
        progressBarIsiForm.setVisibility(View.GONE);
    }

    private void displayLoading() {
        input_wd_btn_selesai.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        editTextNamaRuangan.setVisibility(View.GONE);
        textViewRuangan.setText(R.string.isi_form_ruangan_title);
        progressBarIsiForm.setVisibility(View.VISIBLE);
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
            JSONObject jsonObject = dt_mk.getJSONObject(position);
            kodeMK = jsonObject.getString(Config.TAG_KODERUANGAN);
            displaySuccess();
        } catch (JSONException e) {
            displayFailed();
            e.printStackTrace();
        }
        return kodeMK;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String test = spinner.getSelectedItem().toString();
        if (test.equals("Pilih Ruangan ...")){
            editTextNamaRuangan.setText("");
        } else {
            editTextNamaRuangan.setText(getKodeMK(position));
        }
        displaySuccess();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        editTextNamaRuangan.setText("");
    }


}
