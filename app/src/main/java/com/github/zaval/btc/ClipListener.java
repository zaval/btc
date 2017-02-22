package com.github.zaval.btc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClipListener implements ClipboardManager.OnPrimaryClipChangedListener {

    private Context context;
    public ClipListener(Context context){
        this.context = context;
    }

    @Override
    public void onPrimaryClipChanged() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean needClipboard = pref.getBoolean("watch_tx", true);
        if (!needClipboard){
            return;
        }

        ClipboardManager cb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = cb.getPrimaryClip();

        ClipData.Item item = clip.getItemAt(0);
        CharSequence data = item.getText();
        if (data == null){
            return;
        }

        String clipData = data.toString();

        Log.e("CLIP", clipData);

        if (!Utils.ValidateBitcoinAddress(clipData)){
            return;
        }

        AddressCheckerWorker worker = new AddressCheckerWorker(context, clipData);
        Thread t = new Thread(worker);
        t.start();
    }
}
