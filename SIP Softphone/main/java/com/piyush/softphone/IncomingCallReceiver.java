package com.piyush.softphone;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by Nullpointer on 1/27/2018.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    SipAudioCall incomingCall = null;
    MainActivity activity = null;
    Vibrator v;

    @Override
    public void onReceive(final Context context, Intent intent) {
        activity = (MainActivity)context;

        final long[] pattern = {0, 100, 1000};
        v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        //SipAudioCall listener for Incoming call
        SipAudioCall.Listener listener = new SipAudioCall.Listener(){
            @Override
            public void onRinging(final SipAudioCall call, SipProfile caller) {
                Log.d("[onRinging]","Ringing");

                v.vibrate(pattern,0);

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Incoming call")
                        .setMessage("Call from "+caller.getUriString())
                        .setPositiveButton("Pick up", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    incomingCall.answerCall(36);
                                } catch (SipException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNeutralButton("Hang up", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    call.endCall();
                                    v.cancel();
                                } catch (SipException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setCancelable(false);
                builder.show();
            }

            @Override
            public void onCallEstablished(SipAudioCall call) {
                Log.d("[onCallEstablished]", "Incoming Call established with " + call.getPeerProfile().getUriString());
                incomingCall.setSpeakerMode(true);
                incomingCall.startAudio();
                activity.sipAudioCall = call;
                v.cancel();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(StaticValues.INCOMING_CALL_ESTABLISHED,"incoming call established");
                msg.setData(bundle);
                activity.handler.sendMessage(msg);
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                Log.d("[onError]",errorMessage);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                Log.d("[onCallEnded]","from BroadcastReceiver");
            }
        };

        try {
            incomingCall = activity.manager.takeAudioCall(intent, listener);
            listener.onRinging(incomingCall, incomingCall.getPeerProfile());
        } catch (SipException e) {
            e.printStackTrace();
        }
    }
}
