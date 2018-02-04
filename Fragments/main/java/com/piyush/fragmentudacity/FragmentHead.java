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
public class FragmentHead extends Fragment {
    private static final String TAG = "[FragmentHead]";

    private List<Integer> headList;
    private int index = 0;

    public FragmentHead() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //If previous state exists, load it
        if(savedInstanceState!=null){
            headList = savedInstanceState.getIntegerArrayList("HeadList");
            index = savedInstanceState.getInt("HeadIndex");
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_head, null);
        final ImageView iv = v.findViewById(R.id.headImage);
        if(headList!=null) {
            iv.setImageResource(headList.get(index));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(index < headList.size()-1){
                        index++;
                    }else{
                        index = 0;
                    }
                    iv.setImageResource(headList.get(index));
                }
            });
        }else {
            Log.d(TAG,"Image List is null");
        }
        return v;
    }

    public void setHeadList(List<Integer> list){
        this.headList = list;
    }
    public void setIndex(int index){
        this.index = index;
    }

    //Saving current state of the fragment
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList("HeadList",(ArrayList<Integer>)headList);
        outState.putInt("HeadIndex",index);
    }
}
