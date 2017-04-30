package com.example.ernest.kidsmate1;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 2017-04-15.
 */

public class Blank extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }

    protected void onStartClicked(View v) {

    }

    protected void onTestClicked(View v) {
        LinearLayout manager = (LinearLayout) findViewById(R.id.layout);
        TextView textView = new TextView(this);
        textView.setText("a");
        manager.addView(textView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
