package com.example.nullpointer.wifip2ptutorial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by piyush.pal on 27-12-2017.
 */

public class ReceiverAsyncTask extends AsyncTask<String, Integer, String> {
    DatagramSocket socket;
    private InetAddress clientAddr, listenIP;
    Activity activity;
    private int PORT;
    private String str;

    //Constructors
    ReceiverAsyncTask(){}

    ReceiverAsyncTask(int port, Activity activity){
        this.PORT = port;
        this.activity = activity;
    }

    ReceiverAsyncTask(InetAddress address, int port, Activity activity){
        this.listenIP = address;
        this.PORT = port;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        Log.d("AsyncTask","PRE EXECUTE");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("AsyncTask","BACKGROUND");
        try{
            if(socket == null){
                socket = new DatagramSocket(this.PORT, listenIP); //Initial Connectinon Socket
            }
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);

            Log.d("[+]","Socket Connected at "+socket.getLocalSocketAddress()+". Waiting for the packet ... ");
            socket.receive(dp);

            str = new String(dp.getData(), 0, dp.getLength());
            clientAddr = dp.getAddress();
            if(clientAddr!=null && str!=null){
                //start Sending packets
                Log.d("[!]","From client : "+str+" | Client Address : "+clientAddr);
                sendResponse(clientAddr, PORT, str);
                if(!MainActivity.peerList.contains(clientAddr.getHostAddress())){
                    MainActivity.peerList.add(clientAddr.getHostAddress());
                }
            }
            socket.close();
            socket = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        String temp="";
        Log.d("AsyncTask","POST EXECUTE");

        TextView view = (TextView)activity.findViewById(R.id.response);
        if(!str.contains("[ACK]")){
            temp = clientAddr+" > [REQ] "+str;
        }else{
            temp = clientAddr+" : "+str;
        }

        //Populating the Adapter with messages and setting the ListView
        MainActivity.msgs.add(temp);
        if(!str.contains("[ACK]")){
            MainActivity.msgs.add("Sending ack for "+str+" to "+clientAddr);
        }else{
            Toast.makeText(activity, "Response : "+str, Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, MainActivity.msgs);
        ListView deviceLV = (ListView) activity.findViewById(R.id.devicesLV);
        deviceLV.setAdapter(adapter);

        //send data from server to client
        //Populating the Adapter with client IPs and setting the ListView
        ArrayAdapter<String> adapterPeer = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, MainActivity.peerList);
        ListView peerLV = (ListView) activity.findViewById(R.id.peerLV);
        peerLV.setAdapter(adapterPeer);
        peerLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                View v = LayoutInflater.from(activity).inflate(R.layout.send_dialog_layout, null);
                final EditText ed = (EditText)v.findViewById(R.id.dialog_input_msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setTitle("Wi-Fi Direct send message")
                        .setView(v)
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendRequest(position, ed.getText().toString(), activity);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(activity, "Cancelled by sender", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        new ReceiverAsyncTask(listenIP,PORT,activity).execute();
        super.onPostExecute(s);
    }

    public void sendRequest(int position, String msg, Context context){
        Toast.makeText(context, MainActivity.peerList.get(position), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, ClientRequestService.class);
        i.putExtra("address",MainActivity.peerList.get(position));
        i.putExtra("port",MainActivity.reqResCommPort);
        i.putExtra("data",msg);
        context.startService(i);
    }

    //Form response and send over the socket stream
    public String sendResponse(InetAddress clientAddr, int port, String dataReceived){
        try{
            DatagramSocket socket = new DatagramSocket();
            String response = "[ACK] "+dataReceived+";"+clientAddr;
            DatagramPacket packet = new DatagramPacket(response.getBytes(), response.length(), clientAddr, port);
            Log.d("[ServerThread]","Sending response");
            socket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dataReceived;
    }
}
