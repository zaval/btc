package com.github.zaval.btc;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
//import android.widget.Toast;

public class Permissions extends Activity {
    private static final int MY_PERMISSIONS_INTERNET = 1;
    private Context context;

    public Permissions(Context context) {
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkWriteExternalStoragePermission() {

        context.checkSelfPermission(Manifest.permission.INTERNET);

        if (context.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_INTERNET);
        }
        else {
//            Toast.makeText(this.context, "already have permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_INTERNET: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
