package com.yandi.yarud.scannerabsensiupi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yandi.yarud.scannerabsensiupi.MainActivity;

public class StartAppOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
        }
    }
}
