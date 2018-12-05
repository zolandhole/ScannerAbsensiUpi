package com.yandi.yarud.scannerabsensiupi.utils;

import android.content.Context;
import android.util.Log;

import com.yandi.yarud.scannerabsensiupi.MainActivity;

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
            }
        } else {
            switch (halaman){
                case "main":
                    Log.e("YARUD", "TIDAK ADA KONEKSI INTERNET");
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.displayFailed();
                    break;
            }
        }
    }
}
