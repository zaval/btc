package com.github.zaval.btc;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    public static String httpGet(String url){
        String page = "";
        try {
            URL address = new URL(url);
            page = httpGet(address);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return page;
    }

    public static String httpGet(URL url){
        String page = "";
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String data = "";
            StringBuilder sb = new StringBuilder();
            String line = "";
            Log.d("BTC", "OLOLO " + in.available());
            while ((line = br.readLine()) != null) {
                Log.d("BTC", line);
                sb.append(line);
            }
            in.close();
            page = sb.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            con.disconnect();
        }

        return  page;
    }
}
