package com.xjj.pulltorefresh.demo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xjj.pulltorefresh.demo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goRcv(View v){
        Intent intent = new Intent(this,RcvActivity.class);
        startActivity(intent);
    }

    public void goLv(View v){
        Intent intent = new Intent(this,LvActivity.class);
        startActivity(intent);
    }
}
