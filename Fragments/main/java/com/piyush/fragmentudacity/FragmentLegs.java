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
public class FragmentLegs extends Fragment {
    private static final String TAG = "[FragmentLegs]";

    private List<Integer> legsList;
    private int index;

    public FragmentLegs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if previous state available, Load it
        if(savedInstanceState!=null){
            legsList = savedInstanceState.getIntegerArrayList("LegsList");
            index = savedInstanceState.getInt("LegsIndex");
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_legs, null);
        final ImageView iv = v.findViewById(R.id.legsImage);
        if(legsList!=null){
            iv.setImageResource(legsList.get(index));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(index < legsList.size()-1){
                        index++;
                    }else {
                        index = 0;
                    }
                    iv.setImageResource(legsList.get(index));
                }
            });
        }else{
            Log.d(TAG, "Image list is null");
        }
        return v;
    }

    public void setLegsList(List<Integer> list){
        this.legsList = list;
    }
    public void setIndex(int index){
        this.index = index;
    }

    //Saving the current state of the Fragment

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList("LegsList",(ArrayList<Integer>)legsList);
        outState.putInt("LegsIndex", index);
    }
}
