package com.example.nullpointer.wifip2ptutorial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.net.InetAddress;

/**
 * Created by piyush.pal on 27-12-2017.
 */

public class ClientDataBR extends BroadcastReceiver {
    private MainActivity activity;
    ClientDataBR(MainActivity activity){
        this.activity = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            String s = intent.getStringExtra("response");
            if (s != null) {
                MainActivity.msgs.add(s);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, MainActivity.msgs);
                activity.devicesLV.setAdapter(adapter);

                new ClientReceiverTask(InetAddress.getByName(s.split(";")[1].substring(1)),1234,activity).execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
