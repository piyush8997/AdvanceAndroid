package com.example.nullpointer.wifip2ptutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener , WifiP2pManager.ConnectionInfoListener{
    IntentFilter filter = new IntentFilter();
    IntentFilter filterData = new IntentFilter();

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    WifiP2pReceiver receiver;
    ClientDataBR br;

    List<WifiP2pDevice> devices = new ArrayList<>();
    List<String> deviceNames = new ArrayList<>();

    static List<String> msgs = new ArrayList<>();
    static List<String> peerList = new ArrayList<>();

    private WifiP2pInfo info;
    static int initialConnectionPort = 8888;
    static int reqResCommPort = 1234;

    private String IP,localip;
    private boolean isHost = false;

    private boolean isP2pEnable;
    public void setP2pEnable(boolean p2pEnable) {
        isP2pEnable = p2pEnable;
    }

    ListView devicesLV, peerLV;
    Button btnsend,btnclose;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesLV = (ListView)findViewById(R.id.devicesLV);
        peerLV = (ListView)findViewById(R.id.peerLV);
        btnsend = (Button)findViewById(R.id.send);
        tv = (TextView)findViewById(R.id.response);
        btnclose = (Button)findViewById(R.id.close);

        //Intent filters
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        ////// Receive the BroadCast wifip2ptutorial.receiveData //////
        filterData.addAction("wifip2ptutorial.receiveData");
        filterData.addCategory(Intent.CATEGORY_DEFAULT);
        ///////////////////////////////////////////////////////////////

        //WifiP2pManager and Channel
        manager = (WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        channel = manager.initialize(MainActivity.this, getMainLooper(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiP2pReceiver(manager, channel, this);
        registerReceiver(receiver, filter);

        br = new ClientDataBR(MainActivity.this);
        registerReceiver(br, filterData);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(br);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        devices.clear();
        deviceNames.clear();
        devices.addAll(wifiP2pDeviceList.getDeviceList());
        for (WifiP2pDevice dev : devices){
            deviceNames.add(dev.deviceName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, deviceNames);
        devicesLV.setAdapter(adapter);
        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, ""+devices.get(i), Toast.LENGTH_SHORT).show();
                //Connecting with the remote device
                WifiP2pConfig config = new WifiP2pConfig();
                config.groupOwnerIntent=1;
                config.deviceAddress = devices.get(i).deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("[WifiP2pManager]","Connection success");
                        Log.d("[WifiP2pManager]","WifiP2pInfo - "+IP);
                    }
                    @Override
                    public void onFailure(int i) {
                        Log.d("[WifiP2pManager]","Connection failed - "+i);
                    }
                });
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        this.info = wifiP2pInfo;
        try{
            IP = info.groupOwnerAddress.getHostAddress();
            if(IP!=null){btnclose.setVisibility(View.VISIBLE);}
            Log.d("[Owner IP]", InetAddress.getByName(IP)+" | "+info.groupOwnerAddress.getHostAddress());

            if (info.groupFormed && info.isGroupOwner) {
                //HOST
                peerLV.setVisibility(View.VISIBLE);
                tv.setText(IP+" - SERVER");
                Log.d("[+]","[HOST] Group formed : "+info.groupFormed+" | Group owner : "+info.isGroupOwner);
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

                //start the initial connection Server
                new ReceiverAsyncTask(InetAddress.getByName(IP),initialConnectionPort, MainActivity.this).execute();
            }
            else{
                //CLIENT
                peerLV.setVisibility(View.VISIBLE);
                Log.d("[+]", "[Client]");

                //Getting the local IP address.
                localip = getClientIP();
                tv.setText(localip+" - CLIENT");
                Log.d("[Client Info]", localip);

                //Send initial request to tell the server the IP address of client
                Intent i = new Intent(MainActivity.this, ClientIntentService.class);
                i.putExtra("address",IP);
                i.putExtra("port",initialConnectionPort);
                i.putExtra("data",localip);
                startService(i);

                MainActivity.peerList.add(IP);
                peerLV.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, MainActivity.peerList));
                peerLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.send_dialog_layout, null);
                        final EditText ed = (EditText)v.findViewById(R.id.dialog_input_msg);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Wi-Fi Direct send message")
                                .setView(v)
                                .setCancelable(false)
                                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(MainActivity.this, ClientIntentService.class);
                                        i.putExtra("address",IP);
                                        i.putExtra("port",initialConnectionPort);
                                        i.putExtra("data",ed.getText().toString());
                                        startService(i);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "Cancelled by sender", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Getting IP address assigned to P2P
    String getClientIP(){
        String addr = null;
        try{
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for(;n.hasMoreElements();){
                NetworkInterface networkInterface = n.nextElement();
                if(networkInterface.getName().contains("p2p")){
                    Enumeration<InetAddress> add = networkInterface.getInetAddresses();
                    for(;add.hasMoreElements();){
                        InetAddress InetAddr = add.nextElement();
                        addr = InetAddr.getHostAddress();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return addr;
    }

    //Java reflection ... copy paste only :p
    void deletePersistentGroups(){
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(manager, channel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
