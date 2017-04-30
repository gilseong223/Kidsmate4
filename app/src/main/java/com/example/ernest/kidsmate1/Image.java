package com.example.ernest.kidsmate1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 2017-04-15.
 */

public class Image extends AppCompatActivity {
    private TextView img_textview;
    private String mResult;
    private TextView txtResult;
    private Button btnStart;
    private String img;
    private TextView test;
    private MyHandler myHandler;
    private String[] resultchk = new String[]{"","","","",""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        img_textview = (TextView) findViewById(R.id.img_textview);
        txtResult = (TextView) findViewById(R.id.img_result);
        btnStart = (Button) findViewById(R.id.img_start);
        test = (TextView) findViewById(R.id.img_test);

        myHandler = new MyHandler(this);
        img = getWord();
        img_textview.setText(img);
    }

    protected void onClicked_Img(View v) {
        String img = getWord();
        img_textview.setText(img);
    }

    protected void onStartClicked_Img(View v) {
        Database.myRecognize(txtResult, btnStart, myHandler);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String getWord() {
        SQLiteDatabase DB;
        Cursor cursor;
        int id = (int)(Math.random() * Integer.MAX_VALUE) % 3017 + 1;
        String img = "";

        DB = Database.getDB();
        cursor = DB.rawQuery("SELECT word FROM dic WHERE id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            img = cursor.getString(0);
        }
        cursor.close();

        return img;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {                         //샘플 코드 복사 한 것입니다
            case R.id.endRecognize:
                Log.v("dic", "endRecognize");
                resultchk = Database.getResultCHK();
                test.setText("");
                int i = 0;
                for (String result: resultchk) {
                    i++;
                    test.append("\"" + result + "\" - \"" + img_textview.getText() + "\"\n");
                    if(result.toLowerCase().equals(img_textview.getText().toString().toLowerCase())) {
                        test.append("맞았습니다");
                        img = getWord();
                        img_textview.setText(img);
                        break;
                    }
                    else {
                        if(i == 5)
                            test.append("틀렸습니다");
                    }
                }
                break;
        }
    }
}
