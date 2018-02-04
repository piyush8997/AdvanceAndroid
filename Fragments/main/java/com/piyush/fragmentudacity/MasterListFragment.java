package com.piyush.fragmentudacity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by Nullpointer on 2/4/2018.
 */

public class MasterListFragment extends Fragment{

    //For communicating Fragment and its host activity
    //Creating an listener
    OnImageClickListener callback;
    public interface OnImageClickListener {
        //action
        void onImageSelected(int pos);
    }

    public MasterListFragment(){
    }

    //This is where a fragment attaches itself to its host activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //This will make sure that the host activity has implemented the listener or not
        try{
            callback = (OnImageClickListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement the OnImageClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_master_list,null);
        GridView gv = v.findViewById(R.id.imageGridView);
        MasterListAdapter adapter = new MasterListAdapter(ImageResourceUtil.getAll(), getContext());
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback.onImageSelected(i);
            }
        });
        return v;
    }
}
