package com.example.nullpointer.wifip2ptutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by piyush.pal on 03-01-2018.
 */

public class ClientReceiverTask extends AsyncTask<String, Integer, String> {
    InetAddress addr;
    int port;
    DatagramSocket socket;
    DatagramPacket dp;
    String str,res;
    MainActivity activity;

    ClientReceiverTask(InetAddress address, int port, MainActivity activity){
        this.addr = address;
        this.port = port;
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        Log.d("[ClientReceiverTask]","onPreExecute");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("[ClientReceiverTask]","doInBackground");
        try{
            if(socket == null){
                socket = new DatagramSocket(port, addr);
            }
            byte[] buf = new byte[1024];
            dp = new DatagramPacket(buf, buf.length);

            Log.d("[+]","Socket Connected at "+socket.getLocalSocketAddress()+". Waiting for the packet ... ");
            socket.receive(dp);

            str = new String(dp.getData(), 0, dp.getLength());
            Log.d("[ClientReceiverTask]", str);

            if(str!=null && dp.getAddress()!=null){
                res = new ReceiverAsyncTask().sendResponse(dp.getAddress(),MainActivity.initialConnectionPort, str);
            }

            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("[ClientReceiverTask]","onPostExecute");
        Toast.makeText(activity, ""+str, Toast.LENGTH_SHORT).show();

        MainActivity.msgs.add(dp.getAddress()+" > [REQ] "+str);
        if(res!=null){
            MainActivity.msgs.add("Sending response for "+res+" to "+dp.getAddress());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, MainActivity.msgs);
        ListView deviceLV = (ListView) activity.findViewById(R.id.devicesLV);
        deviceLV.setAdapter(adapter);

        new ClientReceiverTask(addr ,port,activity).execute();
        super.onPostExecute(s);
    }
}
