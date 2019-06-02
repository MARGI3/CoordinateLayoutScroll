package com.demo.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_scroll_flag).setOnClickListener(this);
        findViewById(R.id.button_sticky_bottom).setOnClickListener(this);
        findViewById(R.id.button_weird_sticky).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_scroll_flag:
                intent = new Intent(this, ScrollBehaviorActivity.class);
                startActivity(intent);
                break;
            case R.id.button_sticky_bottom:
                intent = new Intent(this, StickyBottomActivity.class);
                startActivity(intent);
                break;
            case R.id.button_weird_sticky:
                intent = new Intent(this, WeirdStickyBottomActivity.class);
                startActivity(intent);
                break;
        }
    }
}
