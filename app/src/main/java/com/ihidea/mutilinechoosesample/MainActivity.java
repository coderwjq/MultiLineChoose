package com.ihidea.mutilinechoosesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihidea.multilinechooselib.MultiLineChooseLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private List<String> mDataList = new ArrayList<>();
    
    private MultiLineChooseLayout singleChoose;
    
    private MultiLineChooseLayout multiChoose;
    
    private MultiLineChooseLayout flowLayout;
    
    private TextView singleChooseTv;
    
    private TextView multiChooseTv;
    
    private TextView flowLayoutTv;
    
    private Button button;
    
    List<String> multiChooseResult = new ArrayList<>();
    
    List<String> flowLayoutResult = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleChoose = (MultiLineChooseLayout) findViewById(R.id.singleChoose);
        multiChoose = (MultiLineChooseLayout) findViewById(R.id.multiChoose);
        flowLayout = (MultiLineChooseLayout) findViewById(R.id.flowLayout);
        
        singleChooseTv = (TextView) findViewById(R.id.singleChooseTv);
        multiChooseTv = (TextView) findViewById(R.id.multiChooseTv);
        flowLayoutTv = (TextView) findViewById(R.id.flowLayoutTv);
        
        initData();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleChoose.cancelAllSelectedItems();
                multiChoose.cancelAllSelectedItems();
                flowLayout.cancelAllSelectedItems();
            }
        });
        
        //单选
        singleChoose.setOnItemClickListener(new MultiLineChooseLayout.onItemClickListener() {
            @Override
            public void onItemClick(int position, String text, boolean isChecked) {
                singleChooseTv.setText("结果：position: " + position + "   " + text);
            }
        });
        
        //多选
        multiChoose.setOnItemClickListener(new MultiLineChooseLayout.onItemClickListener() {
            @Override
            public void onItemClick(int position, String text, boolean isChecked) {
                multiChooseResult = multiChoose.getAllItemSelectedTextWithListArray();
                if (multiChooseResult != null && multiChooseResult.size() > 0) {
                    String textSelect = "";
                    for (int i = 0; i < multiChooseResult.size(); i++) {
                        textSelect += multiChooseResult.get(i) + " , ";
                    }
                    multiChooseTv.setText("结果：" + textSelect);
                }
            }
        });
        
        //流式布局
        flowLayout.setOnItemClickListener(new MultiLineChooseLayout.onItemClickListener() {
            @Override
            public void onItemClick(int position, String text, boolean isChecked) {
                flowLayoutResult = flowLayout.getAllItemSelectedTextWithListArray();
                if (flowLayoutResult != null && flowLayoutResult.size() > 0) {
                    String textSelect = "";
                    for (int i = 0; i < flowLayoutResult.size(); i++) {
                        textSelect += flowLayoutResult.get(i) + " , ";
                    }
                    flowLayoutTv.setText("结果：" + textSelect);
                }
            }
        });
        
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
        singleChoose.setList(mDataList);
        multiChoose.setList(mDataList);
        flowLayout.setList(mDataList);
        
    }
}
