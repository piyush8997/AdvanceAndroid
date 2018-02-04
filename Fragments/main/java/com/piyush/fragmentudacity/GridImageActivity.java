package com.piyush.fragmentudacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GridImageActivity extends AppCompatActivity implements MasterListFragment.OnImageClickListener{
    private int headIndex;
    private int bodyIndex;
    private int legsIndex;

    Button btnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_image);
        btnClick = findViewById(R.id.btnSend);
    }

    @Override
    public void onImageSelected(int pos) {
        //store the selected list index for head, body and legs based on the index
        //there are total 36 images (0-11 head, 12-23 body, 24-35 legs| each 12)
        //bodyPartNumber for head will be 0, body will be 1 and legs will be 2
        int bodyPartNumber = pos/12;
        int listIndex = pos - 12*bodyPartNumber;
        switch (bodyPartNumber){
            case 0 : headIndex = listIndex;
                break;
            case 1 : bodyIndex = listIndex;
                break;
            case 2 : legsIndex = listIndex;
                break;
            default:break;
        }
        //Create a bundle. Put these values and send it to MainActivity
        Bundle b = new Bundle();
        b.putInt("headIndex",headIndex);
        b.putInt("bodyIndex",bodyIndex);
        b.putInt("legsIndex",legsIndex);

        final Intent i = new Intent(GridImageActivity.this, MainActivity.class);
        i.putExtras(b);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
            }
        });

        Toast.makeText(this, "BodyPartNumber : "+bodyPartNumber+"\nListIndex : "+listIndex, Toast.LENGTH_SHORT).show();
    }
}
