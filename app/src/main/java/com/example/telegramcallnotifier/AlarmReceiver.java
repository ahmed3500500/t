package com.example.telegramcallnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            CustomExceptionHandler.log(context, "AlarmReceiver fired");

            SharedPreferences prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE);
            int alarmCounter = prefs.getInt("alarm_counter", 0);
            alarmCounter++;

            boolean sendTelegram = false;
            if (alarmCounter >= 30) {
                sendTelegram = true;
                alarmCounter = 0;
            }

            prefs.edit().putInt("alarm_counter", alarmCounter).apply();

            TelegramSender sender = new TelegramSender(context);
            sender.sendPing();

            if (sendTelegram) {
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                sender.sendStatusMessage("⏰ Alarm report\nTime: " + time);
            }
        } catch (Exception e) {
            CustomExceptionHandler.log(context, "AlarmReceiver exception: " + e.getMessage());
        } finally {
            AlarmScheduler.scheduleNext(context, AlarmScheduler.TEST_INTERVAL_MS);
        }
    }
}
