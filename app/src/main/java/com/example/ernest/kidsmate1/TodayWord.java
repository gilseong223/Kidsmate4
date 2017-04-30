package com.example.ernest.kidsmate1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by User on 2017-04-15.
 */

public class TodayWord extends AppCompatActivity {
    TextView tw_word;
    TextView tw_mean;
    ImageButton mic;
    private String mResult;
    private TextView txtResult;
    private Button btnStart;
    private String[] todayWord;
    private TextView test;
    private MyHandler myHandler;
    private String[] resultchk = new String[]{"","","","",""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_word);

        tw_word = (TextView) findViewById(R.id.tw_word);
        tw_mean = (TextView) findViewById(R.id.tw_mean);
        mic = (ImageButton) findViewById(R.id.mic1);
        txtResult = (TextView) findViewById(R.id.textView3);
        btnStart = (Button) findViewById(R.id.button3);
        test = (TextView) findViewById(R.id.textView4);

        myHandler = new MyHandler(this);
        todayWord = getWord();
        tw_word.setText(todayWord[0]);
        tw_mean.setText(todayWord[1]);
    }

    protected void onClicked(View v) {
        String[] todayWord = getWord();
        tw_word.setText(todayWord[0]);
        tw_mean.setText(todayWord[1]);
    }

    protected void onStartClicked_TW(View v) {
        Database.myRecognize(txtResult, btnStart, myHandler);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String[] getWord() {
        SQLiteDatabase DB;
        Cursor cursor;
        int id = (int)(Math.random() * Integer.MAX_VALUE) % 3017 + 1;
        String[] todayWord = new String[]{"", ""};

        DB = Database.getDB();
        cursor = DB.rawQuery("SELECT word, mean FROM dic WHERE id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            todayWord[0] = cursor.getString(0);
            todayWord[1] = cursor.getString(1);
        }
        cursor.close();

        return todayWord;
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
                    test.append("\"" + result + "\" - \"" + tw_word.getText() + "\"\n");
                    if(result.toLowerCase().equals(tw_word.getText().toString().toLowerCase())) {
                        test.append("잘했습니다");
                        todayWord = getWord();
                        tw_word.setText(todayWord[0]);
                        tw_mean.setText(todayWord[1]);
                        break;
                    }
                    else {
                        if(i == 5)
                            test.append("다시 발음해 보세요");
                    }
                }
                break;
        }
    }
}
