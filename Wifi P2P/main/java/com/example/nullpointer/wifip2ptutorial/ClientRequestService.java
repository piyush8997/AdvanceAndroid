package com.example.nullpointer.wifip2ptutorial;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by piyush.pal on 03-01-2018.
 */

public class ClientRequestService extends IntentService {
    DatagramSocket socket = null;
    int port;
    InetAddress address;
    String dataToSend;

    public ClientRequestService(String name) {
        super(name);
    }
    public ClientRequestService() {
        super("ClientRequestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try{
            port = intent.getIntExtra("port",-1);
            address = InetAddress.getByName(intent.getStringExtra("address"));
            dataToSend = intent.getStringExtra("data");
            Log.d("ClientRequestService", address+":"+port+" | "+dataToSend);
            if(socket == null){
                socket = new DatagramSocket(port);
                DatagramPacket packet = new DatagramPacket(dataToSend.getBytes(), dataToSend.length(), address, port);
                socket.send(packet);
                MainActivity.msgs.add("Sending "+dataToSend+" to "+address);
            }
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
