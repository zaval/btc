package com.github.zaval.btc;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Implementation of App Widget functionality.
 */
public class BtcWidget extends AppWidgetProvider {

    public static final String SYNC_BUTTON = "sync_button";
    public static final String SETTINGS_BUTTON = "settings_button";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (SYNC_BUTTON.equals(action)){
            int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, BtcWidget.class));
            for (int appId: ids) {
                syncValue(context, appId);
            }
        }
        else if (SETTINGS_BUTTON.equals(action)){
            Intent i = new Intent(context, SettingsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.btc_widget);

        Intent intent = new Intent(context, BtcWidget.class);
        intent.setAction(SYNC_BUTTON);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        views.setOnClickPendingIntent(R.id.syncButton, PendingIntent.getBroadcast(context, 0, intent, 0));

        Intent settingsIntent = new Intent(context, BtcWidget.class);
        settingsIntent.setAction(SETTINGS_BUTTON);

        views.setOnClickPendingIntent(R.id.settingsButton, PendingIntent.getBroadcast(context, 0, settingsIntent, 0));
        views.setTextViewText(R.string.app_name, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            syncValue(context, appWidgetId);
//            Toast.makeText(context, "UPDATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Permissions permissions = new Permissions(context);
            permissions.checkWriteExternalStoragePermission();
        }

//TODO: clipboard listener
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean needClipboard = pref.getBoolean("watch_tx", true);
//
//        final ClipboardManager cb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        cb.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
//            @Override
//            public void onPrimaryClipChanged() {
//                ClipData clip = cb.getPrimaryClip();
//                if (clip == null){
//                    return;
//                }
//                ClipData.Item it = clip.getItemAt(0);
//                Log.e("BTC", "" + it.getText());
//            }
//        });


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    protected void syncValue(final Context context, final int appWidgetId){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String page = Utils.httpGet("https://blockchain.info/ru/ticker");
                JSONObject obj = null;
                final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.btc_widget);
                try {
                    obj = new JSONObject(page);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    String currency = pref.getString("currency", "USD");
                    String value = obj.getJSONObject(currency).getString("15m") + obj.getJSONObject(currency).getString("symbol");
                    views.setTextViewText(R.id.textView, value);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("BTC", "" + e.getMessage());
                }
            }
        });

        t.start();
    }
}

