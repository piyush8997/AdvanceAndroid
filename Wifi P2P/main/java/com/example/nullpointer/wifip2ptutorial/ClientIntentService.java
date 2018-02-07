package com.example.nullpointer.wifip2ptutorial;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by piyush.pal on 27-12-2017.
 */

public class ClientIntentService extends IntentService {
    static final String TAG = "[ClientIntentService]";

    DatagramSocket socket;
    int port;
    InetAddress address;
    String dataToSend;

    byte[] recArr = new byte[1024];

    public ClientIntentService(String name) {
        super(name);
    }
    public ClientIntentService(){
        super("ClientIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try{
            //Sending data to the server after a successful connection
            port = intent.getIntExtra("port",-1);
            address = InetAddress.getByName(intent.getStringExtra("address"));
            dataToSend = intent.getStringExtra("data");

            Log.d(TAG,address+":"+port+" "+dataToSend);
            if(socket == null){
                socket = new DatagramSocket(port);
            }
            DatagramPacket packet = new DatagramPacket(dataToSend.getBytes(), dataToSend.length(), address, port);
            socket.send(packet);
            //Adding SENDING <DATA> to <ADDRESS> to msgs arraylist
            MainActivity.msgs.add("Sending "+dataToSend+" to "+address);

            DatagramPacket packet1 = new DatagramPacket(recArr, recArr.length);
            socket.receive(packet1);
            String str = new String(packet1.getData(), 0, packet1.getLength());
            str = packet1.getAddress()+" > "+str;
            Log.d(TAG,str);

            //for printing ... Broadcasting an intent with response string and setting adapter to the listview
            Intent i = new Intent();
            i.setAction("wifip2ptutorial.receiveData");
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.putExtra("response", str);
            sendBroadcast(i);

            socket.close();
            socket = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
