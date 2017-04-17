package com.example.ernest.kidsmate1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
    }

    protected void mainStartClicked(View v) {
        Intent intent = new Intent(MainActivity.this, SelectContents.class);
        startActivity(intent);
        finish();
    }

    protected void mainOffClicked(View v) {
        finish();
        System.exit(0);
    }
}
