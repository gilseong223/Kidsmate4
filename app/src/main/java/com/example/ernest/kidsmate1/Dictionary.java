package com.example.ernest.kidsmate1;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by User on 2017-04-15.
 */

public class Dictionary extends AppCompatActivity {
    private TextView txtResult;
    private String mResult;
    private Button btnStart;
    private EditText dicInput;
    private MyHandler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dic);

        dicInput = (EditText) findViewById(R.id.dicInput);

        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);
        myHandler = new MyHandler(this);
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {                         //샘플 코드 복사 한 것입니다
            case R.id.endRecognize:
                Log.v("dic", "endRecognize");
                mResult = Database.getmmResult();
                dicInput.setText(mResult);
                break;
        }
    }

    protected void onStartClicked(View v) {
        Database.myRecognize(txtResult, btnStart, myHandler);
    }

    protected void searClicked(View v) {                    //단어 입력 후 검색 버튼 -> 결과화면으로
        Intent intent = new Intent(Dictionary.this, ResultDic.class);
        intent.putExtra("word", dicInput.getText().toString());     //입력한 단어를 결과 Activity로 넘겨줌
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {       //뒤로가기 눌렀을 때 현재 Activity 제거
        super.onBackPressed();
        finish();
    }
}
