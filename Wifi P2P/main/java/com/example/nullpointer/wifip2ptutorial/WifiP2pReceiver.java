package com.example.nullpointer.wifip2ptutorial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputContentInfo;
import android.widget.Toast;

/**
 * Created by Nullpointer on 12/21/2017.
 */

public class WifiP2pReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    WifiP2pReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity){
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    private void discoverPeers(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(int i) {
                Log.d("[Discover]", "Discovery failed - "+i);
            }
        });
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            //get wifi state
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            //compare and check whether p2p is enabled or not
            if(state ==  WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "P2P enable", Toast.LENGTH_SHORT).show();
                activity.setP2pEnable(true);
                discoverPeers();
            }
            else{
                Toast.makeText(context, "P2P disable", Toast.LENGTH_SHORT).show();
                activity.setP2pEnable(false);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager!=null){
                manager.requestPeers(channel, activity);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            Log.d("[+]","Network Info : "+networkInfo);
            if (networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, activity);
                activity.btnclose.setVisibility(View.VISIBLE);

                activity.btnclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("[SUCCESS]","Temporary group removed");
                                activity.deletePersistentGroups();
                                Log.d("[SUCCESS]","Permanent group removed");
                                removeMsgs();
                                activity.btnsend.setVisibility(View.INVISIBLE);
                                //activity.devicesLV.setVisibility(View.VISIBLE);
                                discoverPeers();
                                activity.tv.setText("");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("[FAIL]","Temoporary group removal failed");
                            }
                        });
                    }
                });
            }
            else if(!networkInfo.isConnected()){
                activity.btnsend.setVisibility(View.INVISIBLE);
                activity.btnclose.setVisibility(View.INVISIBLE);
                activity.devicesLV.setVisibility(View.VISIBLE);
                discoverPeers();
                activity.tv.setText("");
                activity.peerLV.setVisibility(View.INVISIBLE);
                removeMsgs();
                removeClients();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }

    void removeMsgs(){
        Log.d("[Size before removal]", ""+MainActivity.msgs.size());
        for(int i=MainActivity.msgs.size()-1; i>=0; i--){
            Log.d("[Removing]", MainActivity.msgs.get(i));
            MainActivity.msgs.remove(i);
        }
        Log.d("[Size after removal]", ""+MainActivity.msgs.size());
    }
    void removeClients(){
        Log.d("[Size before removal]", ""+MainActivity.peerList.size());
        for(int i=MainActivity.peerList.size()-1; i>=0; i--){
            Log.d("[Removing]", ""+MainActivity.peerList.get(i));
            MainActivity.peerList.remove(i);
        }
        Log.d("[Size after removal]", ""+MainActivity.peerList.size());
    }
}
