package com.example.telegramcallnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action)
                || "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED".equals(action)) {

            try {
                Intent callMonitorIntent = new Intent(context, CallMonitorService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(callMonitorIntent);
                } else {
                    context.startService(callMonitorIntent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to start CallMonitorService after boot", e);
            }

            AlarmScheduler.scheduleNext(context, AlarmScheduler.TEST_INTERVAL_MS);
            Log.d(TAG, "CallMonitorService + alarm scheduled after boot");
        }
    }
}
