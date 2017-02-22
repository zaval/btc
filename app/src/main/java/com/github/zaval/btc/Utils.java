package com.github.zaval.btc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Utils {

    private final static String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

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
            if (con != null)
                con.disconnect();
        }

        return  page;
    }

    public static boolean ValidateBitcoinAddress(String addr) {
        if (addr.length() < 26 || addr.length() > 35) return false;
        byte[] decoded = DecodeBase58(addr, 58, 25);
        if (decoded == null) return false;

        byte[] hash = Sha256(decoded, 0, 21, 2);

        return hash != null && Arrays.equals(Arrays.copyOfRange(hash, 0, 4), Arrays.copyOfRange(decoded, 21, 25));

    }

    private static byte[] DecodeBase58(String input, int base, int len) {
        byte[] output = new byte[len];
        for (int i = 0; i < input.length(); i++) {
            char t = input.charAt(i);

            int p = ALPHABET.indexOf(t);
            if (p == -1) return null;
            for (int j = len - 1; j > 0; j--, p /= 256) {
                p += base * (output[j] & 0xFF);
                output[j] = (byte) (p % 256);
            }
            if (p != 0) return null;
        }

        return output;
    }

    private static byte[] Sha256(byte[] data, int start, int len, int recursion) {
        if (recursion == 0) return data;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Arrays.copyOfRange(data, start, start + len));
            return Sha256(md.digest(), 0, 32, recursion - 1);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();

    }

    static void sleep(long millisec){
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
