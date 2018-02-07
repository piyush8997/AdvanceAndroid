package com.piyush.softphone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SipRegistrationListener{
    SipManager manager;
    SipProfile localProfile;
    SipAudioCall sipAudioCall;
    IncomingCallReceiver callReceiver;

    EditText loginSipUser, loginSipPass, localSipDomain, calleeAddress;
    Button loginBtn, callBtn, endBtn;

    Handler handler;

    String[] permission = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.USE_SIP,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();

        loginSipUser = findViewById(R.id.localSipUser);
        loginSipPass = findViewById(R.id.localSipPass);
        localSipDomain = findViewById(R.id.localSipDomain);
        calleeAddress = findViewById(R.id.peerSipAddress);
        loginBtn = findViewById(R.id.loginBtn);
        callBtn = findViewById(R.id.callBtn);
        endBtn = findViewById(R.id.endBtn);

        if(manager == null){
            manager = SipManager.newInstance(MainActivity.this);
        }

        //Registering receiver to receive incoming call intent
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticValues.INCOMING_CALL_INTENT);
        callReceiver = new IncomingCallReceiver();
        registerReceiver(callReceiver, filter);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onResume() {
        super.onResume();

        //SipAudioCall Listener
        final SipAudioCall.Listener listener = new SipAudioCall.Listener(){
            @Override
            public void onCalling(SipAudioCall call) {
                Log.d("[onCalling]","Calling "+call.getPeerProfile().getUriString());
            }

            @Override
            public void onCallEstablished(SipAudioCall call) {
                Log.d("[onCallEstablished]","Call established with "+call.getPeerProfile().getUriString());
                call.setSpeakerMode(true);
                call.startAudio();
                sipAudioCall = call;

                //Posting message to handler
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(StaticValues.CALL_ESTABLISHED,"In Call");
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(StaticValues.CALL_ENDED,"Call ended");
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                Log.d("[onError]",errorMessage);
            }
        };

        //Run code iff all permissions are granted
        if(areAllPermissionsGranted()){
            //Register a handler
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    Bundle b = msg.getData();
                    //////////////////////REGISTRATION_SUCCESS handler
                    if(b.containsKey(StaticValues.REGISTRATION_SUCCESS)){
                        String successMsg = b.getString(StaticValues.REGISTRATION_SUCCESS);

                        //Disable fields
                        loginBtn.setText(successMsg);
                        loginBtn.setEnabled(false);
                        loginSipUser.setEnabled(false);
                        loginSipPass.setEnabled(false);
                        localSipDomain.setEnabled(false);

                        //Making call
                        calleeAddress.setEnabled(true);
                        callBtn.setEnabled(true);
                        callBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Outgoing call logic goes here
                                try {
                                    manager.makeAudioCall(
                                            localProfile.getUriString(),
                                            calleeAddress.getText().toString(),
                                            listener,
                                            36);
                                } catch (SipException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                    ////////////////////////////REGISTRATION_FAILED handler
                    else if(b.containsKey(StaticValues.REGISTRATION_FAILED)){
                        String errMsg = b.getString(StaticValues.REGISTRATION_FAILED);
                        Toast.makeText(MainActivity.this, ""+errMsg, Toast.LENGTH_LONG).show();

                        //Clear fields
                        loginSipUser.setText("");
                        loginSipPass.setText("");
                        localSipDomain.setText("");

                        //Disable call UI
                        callBtn.setEnabled(false);
                        calleeAddress.setEnabled(false);
                    }
                    /////////////////////////////CALL_ESTABLISHED handler
                    else if(b.containsKey(StaticValues.CALL_ESTABLISHED)){
                        endBtn.setEnabled(true);
                        callBtn.setEnabled(false);

                        //End Call
                        endBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    sipAudioCall.endCall();
                                    callBtn.setEnabled(true);
                                    endBtn.setEnabled(false);
                                    calleeAddress.setText("");
                                } catch (SipException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    ////////////////////////////CALL_ENDED handler
                    else if(b.containsKey(StaticValues.CALL_ENDED)){
                        endBtn.setEnabled(false);
                        callBtn.setEnabled(true);
                    }
                    ////////////////////////////INCOMING_CALL_ESTABLISHED handler
                    else if(b.containsKey(StaticValues.INCOMING_CALL_ESTABLISHED)){
                        endBtn.setEnabled(true);
                        callBtn.setEnabled(false);

                        endBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    sipAudioCall.endCall();
                                    endBtn.setEnabled(false);
                                    callBtn.setEnabled(true);
                                } catch (SipException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            };

            //Login button
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //create local user SIP profile
                    localProfile = createProfile(
                            loginSipUser.getText().toString(),
                            localSipDomain.getText().toString(),
                            loginSipPass.getText().toString()
                    );
                    if(localProfile!=null){
                        //Create a Sip Profile and register it with the server
                        registerSipProfile(localProfile, MainActivity.this);

                        Intent i = new Intent();
                        i.setAction(StaticValues.INCOMING_CALL_INTENT);
                        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, i, Intent.FILL_IN_DATA);
                        try {
                            manager.open(localProfile, pi, null);
                        } catch (SipException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    //Check all permissions are granted or not
    private boolean areAllPermissionsGranted(){
        int count=0;
        for(int i=0;i<permission.length;i++){
            if(ContextCompat.checkSelfPermission(MainActivity.this, permission[i])== PackageManager.PERMISSION_GRANTED){
                count++;
            }
        }
        return permission.length == count;
    }

    //Grant permissions
    private void checkAndRequestPermissions(){
        if(!areAllPermissionsGranted()){
            requestPermissions(permission, 1234);
        }else{
            Toast.makeText(this, "All permissions are granted", Toast.LENGTH_SHORT).show();
        }
    }

    //Register local profile
    private void registerSipProfile(SipProfile profile, SipRegistrationListener listener){
        try{
            manager.open(profile);
            manager.register(profile,36,listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Create Sip Profile
    private SipProfile createProfile(String sipUser ,String domain, String pass){
        SipProfile profile = null;
        try{
            SipProfile.Builder builder = new SipProfile.Builder(sipUser, domain);
            builder.setPassword(pass);
            profile = builder.build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return profile;
    }

    //SipRegistrationListener
    @Override
    public void onRegistering(String s) {
        Log.d("[onRegistering]",s);
    }

    @Override
    public void onRegistrationDone(String s, long l) {
        Log.d("[onRegistrationDone]",s);
        Bundle bundle = new Bundle();
        bundle.putString(StaticValues.REGISTRATION_SUCCESS,"Registration done");
        Message msg = new Message();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void onRegistrationFailed(String s, int i, String s1) {
        Log.d("[onRegistrationFailed]",s);
        Bundle bundle = new Bundle();
        bundle.putString(StaticValues.REGISTRATION_FAILED,"Registration Failed : "+s);
        Message msg = new Message();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

}
