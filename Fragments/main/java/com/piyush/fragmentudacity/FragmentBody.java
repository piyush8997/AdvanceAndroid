package com.piyush.fragmentudacity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBody extends Fragment {
    private static final String TAG = "[FragmentBody]";

    private List<Integer> bodyList;
    private int index;

    public FragmentBody() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if previous state exists, load it
        if(savedInstanceState!=null){
            bodyList = savedInstanceState.getIntegerArrayList("BodyList");
            index = savedInstanceState.getInt("BodyIndex");
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_body, null);
        final ImageView iv = v.findViewById(R.id.bodyImage);
        if(bodyList!=null) {
            iv.setImageResource(bodyList.get(index));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(index < bodyList.size()-1){
                        index++;
                    }else {
                        index = 0;
                    }
                    iv.setImageResource(bodyList.get(index));
                }
            });
        }else {
            Log.d(TAG,"Image List is null");
        }
        return v;
    }

    public void setBodyList(List<Integer> list){
        this.bodyList = list;
    }
    public void setIndex(int index){
        this.index = index;
    }

    //Saving current state of the fragment

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList("BodyList",(ArrayList<Integer>)bodyList);
        outState.putInt("BodyIndex",index);
    }
}
