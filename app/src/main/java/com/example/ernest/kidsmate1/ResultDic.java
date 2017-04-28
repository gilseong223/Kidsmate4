package com.example.ernest.kidsmate1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.ernest.kidsmate1.MainActivity.mDBHelper;

/**
 * Created by User on 2017-04-27.
 */

public class ResultDic extends AppCompatActivity {
    private TextView wordText;
    private TextView meanText;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_dic);

        wordText = (TextView) findViewById(R.id.word);
        meanText = (TextView) findViewById(R.id.mean);
        Intent intent = getIntent();
        String word = intent.getStringExtra("word");
        wordText.setText(word);

        String mean = null;

        try{db = mDBHelper.openDatabase();}catch (Exception e){e.printStackTrace();}
        try {Cursor cursor = db.rawQuery("SELECT mean FROM dic WHERE word = '" + word + "' COLLATE NOCASE", null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                mean = cursor.getString(0);
            }
            else {
                mean = "결과가 없습니다.";
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        mDBHelper.close();

        meanText.setText(mean);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
