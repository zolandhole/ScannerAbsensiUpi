package com.yandi.yarud.scannerabsensiupi.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yandi.yarud.scannerabsensiupi.FormRuanganActivity;
import com.yandi.yarud.scannerabsensiupi.networks.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetTokenUPI {
    private String token;
    private String bagian;
    private Context context;

    public GetTokenUPI(Context context, String bagian){
        this.context = context;
        this.bagian = bagian;
    }

    //AMBIL TOKEN UNTUK GET
    public void getToken (final String username, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            token = jsonObject.getString("token");
                            switch (bagian){
                                case "FormRuangan":
                                    FormRuanganActivity ruanganActivity = (FormRuanganActivity) context;
                                    ruanganActivity.RunningPage(token);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "ELLOR", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (bagian) {
                            case "FormRuangan": {
//                                LoginActivity activity = (LoginActivity) context;
//                                activity.displaySuccess();
                                Toast.makeText(context, "Tidak ada Koneksi Internet", Toast.LENGTH_LONG).show();
                                Log.e("USERNAME", username);
                                Log.e("PASSWORD", password);
                                break;
                            }
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
