package com.example.ernest.kidsmate1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by User on 2017-04-15.
 */

public class SelectContents extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contents);
    }

    @Override
    public void onBackPressed() {
    }

    protected void todayWordClicked(View v) {
        Intent intent = new Intent(SelectContents.this, TodayWord.class);
        startActivity(intent);
    }

    protected void dicClicked(View v) {
        Intent intent = new Intent(SelectContents.this, Dictionary.class);
        startActivity(intent);
    }

    protected void blankClicked(View v) {
        Intent intent = new Intent(SelectContents.this, Blank.class);
        startActivity(intent);
    }

    protected void imageClicked(View v) {
        Intent intent = new Intent(SelectContents.this, Image.class);
        startActivity(intent);
    }

    protected void wordChainClicked(View v) {
        Intent intent = new Intent(SelectContents.this, WordChain.class);
        startActivity(intent);
    }

    protected void toMainClicked(View v) {
        Intent intent = new Intent(SelectContents.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
