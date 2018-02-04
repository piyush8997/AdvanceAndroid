package com.piyush.fragmentudacity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    FragmentManager manager;
    Bundle prevSavedInstance, receivedBundle;

    private int headIndex, bodyIndex, legsIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.prevSavedInstance = savedInstanceState;
        receivedBundle = getIntent().getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //create fragments only if there is no previously saved fragments or states
        if(prevSavedInstance == null){
            //Get data from received bundle
            headIndex = receivedBundle.getInt("headIndex");
            bodyIndex = receivedBundle.getInt("bodyIndex");
            legsIndex = receivedBundle.getInt("legsIndex");

            //Instance of FragmentManager
            //Lets you add, remove, replace fragments at runtime
            manager = getSupportFragmentManager();

            //Fragment object
            FragmentHead head = new FragmentHead();
            FragmentBody body = new FragmentBody();
            FragmentLegs legs = new FragmentLegs();

            //set Image lists
            head.setHeadList(ImageResourceUtil.getHead());
            body.setBodyList(ImageResourceUtil.getBody());
            legs.setLegsList(ImageResourceUtil.getLegs());

            head.setIndex(headIndex);
            body.setIndex(bodyIndex);
            legs.setIndex(legsIndex);

            //Add fragment to the activity
            //Each task like adding ,removing or replacing a fragment is a transaction.
            manager.beginTransaction().add(R.id.headFragContainer, head).commit();
            manager.beginTransaction().add(R.id.bodyFragContainer, body).commit();
            manager.beginTransaction().add(R.id.legsFragContainer, legs).commit();
        }
    }
}
