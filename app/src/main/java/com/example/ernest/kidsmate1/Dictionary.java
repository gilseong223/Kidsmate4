package com.example.ernest.kidsmate1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 2017-04-15.
 */

public class Dictionary extends AppCompatActivity {
    TextView dicInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dic);
        dicInput = (TextView) findViewById(R.id.dicInput);
    }

    protected void micClicked(View v) {
        Toast.makeText(getApplicationContext(), "음성 인식을 시작합니다", Toast.LENGTH_SHORT).show();
    }

    protected void searClicked(View v) {
        Intent intent = new Intent(Dictionary.this, ResultDic.class);
        intent.putExtra("word", dicInput.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
