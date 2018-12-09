package com.yandi.yarud.scannerabsensiupi.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yandi.yarud.scannerabsensiupi.FormRuanganActivity;
import com.yandi.yarud.scannerabsensiupi.MainActivity;
import com.yandi.yarud.scannerabsensiupi.ScanQRActivity;

import java.util.TimerTask;

public class NetworkStatus extends TimerTask {
    private Context context;
    private String halaman;
    public NetworkStatus(Context context, String halaman){
        this.context = context;
        this.halaman = halaman;
    }
    @Override
    public void run() {
        if (CheckConnection.apakahTerkoneksiKeInternet(context)){
            switch (halaman){
                case "main":
                    Log.w("YARUD", "ADA KONEKSI INTERNET");
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.displaySuccess();
                    break;
                case "scan":
                    Log.w("SCAN", "ADA KONEKSI INTERNET");
                    break;
                case "form":
                    Log.w("FORM", "ADA KONEKSI INTERNET");
                    break;
            }
        } else {
            switch (halaman){
                case "main":
                    Log.e("YARUD", "TIDAK ADA KONEKSI INTERNET");
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.displayFailed();
                    break;
                case "scan":
                    Log.e("SCAN", "TIDAK ADA KONEKSI INTERNET");
                    ScanQRActivity scanQRActivity = (ScanQRActivity) context;
                    scanQRActivity.finish();
                    break;
                case "form":
                    Log.e("FORM", "TIDAK ADA KONEKSI INTERNET");
                    FormRuanganActivity formRuanganActivity = (FormRuanganActivity) context;
                    formRuanganActivity.displayFailed();
                    break;
            }
        }
    }
}
