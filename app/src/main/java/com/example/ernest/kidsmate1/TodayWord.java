package com.example.ernest.kidsmate1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by User on 2017-04-15.
 */

public class TodayWord extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_word);
    }

    protected void micClicked(View v) {
        Toast.makeText(getApplicationContext(), "음성 인식을 시작합니다", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
