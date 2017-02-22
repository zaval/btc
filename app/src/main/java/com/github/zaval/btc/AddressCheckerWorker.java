package com.github.zaval.btc;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressCheckerWorker implements Runnable {
    private final String TAG = "BTC Address Checker";

    private final String address;
    private final Context context;
    public AddressCheckerWorker(Context context, String address){
        this.context = context;
        this.address = address;
    }

    @Override
    public void run() {

        while (!Utils.isOnline(context)){
            Utils.sleep(30000);
        }


        long blockHeight = 0;
        int n_tx = 0;

        JSONObject addrObj = null;

        while (n_tx == 0) {

            Utils.sleep(5000);

            addrObj = getAddrInfo();
            if (addrObj == null)
                return;

            try {
                n_tx = addrObj.getInt("n_tx");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "address is invalid");
                return;
            }
        }

        long blockCount;
        while (true) {

            JSONArray txs = null;
            try {
                txs = addrObj.getJSONArray("txs");
                blockHeight = txs.getJSONObject(0).getLong("block_height");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            blockCount = getBlockCount();

            long confirms = blockCount - blockHeight + 1;

            Log.e(TAG, "Confirmations: " + confirms);


            Utils.sleep(20000);

            addrObj = getAddrInfo();
            if (addrObj == null){
                return;
            }

        }



    }

    private JSONObject getAddrInfo(){
        String page = Utils.httpGet("https://blockchain.info/address/" + address + "?format=json");
        if (page == null || page.isEmpty()){
            return null;
        }

        JSONObject addrObj = null;
        try {
            addrObj = new JSONObject(page);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

//        Log.e(TAG, page);
        return addrObj;

    }

    private long getBlockCount(){
        long count = 0;

        String page = Utils.httpGet("https://blockchain.info/q/getblockcount");
        if (page == null || page.isEmpty()){
            return count;
        }

        try {
            count = Long.parseLong(page);
        }
        catch (Exception e){
            return 0;
        }

        return count;
    }
}
