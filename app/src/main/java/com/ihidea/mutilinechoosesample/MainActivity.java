package com.ihidea.mutilinechoosesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ihidea.multilinechooselib.MultiLineChooseLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private List<String> mDataList = new ArrayList<>();
    
    private MultiLineChooseLayout multiCHoose;
    
    private Button button;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        multiCHoose = (MultiLineChooseLayout) findViewById(R.id.multiCHoose);
        button = (Button) findViewById(R.id.button);
        initData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                multiCHoose.cancelAllSelectedItems();
            }
        });
        
        //        multiCHoose.setOnItemClickLisener(new MultiLineChooseLayout.onItemClickListener() {
        //            @Override
        //            public void onItemClick(String tag) {
        //                Toast.makeText(MainActivity.this, "" + tag, Toast.LENGTH_SHORT).show();
        //            }
        //        });
        
    }
    
    private void initData() {
        
        mDataList.add("尽管她看");
        mDataList.add("尽管");
        mDataList.add("很热情");
        mDataList.add("我却从她");
        mDataList.add("看出了");
        mDataList.add("强作");
        mDataList.add("欢颜");
        mDataList.add("欢颜");
        mDataList.add("味道");
        multiCHoose.setList(mDataList);
    }
}
