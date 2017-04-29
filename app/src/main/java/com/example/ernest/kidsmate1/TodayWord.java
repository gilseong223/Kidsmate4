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
    private AudioWriterPCM writer;
    private String[] resultchk = new String[]{"", "", "", "", ""};
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "4iZdE_YGdmxI9QVHDmDm";
    private NaverRecognizer naverRecognizer;
    String[] todayWord;
    private TextView test;
    private TodayWord.RecognitionHandler handler;
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

        handler = new TodayWord.RecognitionHandler(this);         //API용 handle
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);    //API 객체

        todayWord = getWord();
        tw_word.setText(todayWord[0]);
        tw_mean.setText(todayWord[1]);
        mic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {                               //마이크 버튼 눌렀을 때
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {        //음성인식 시작, 여긴 샘플 코드 복사 한 것입니다
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    protected void onClicked(View v) {
        String[] todayWord = getWord();
        tw_word.setText(todayWord[0]);
        tw_mean.setText(todayWord[1]);
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

    static class RecognitionHandler extends Handler {       //API용 핸들, 메시지를 받아와서 현재 Activity에 전달
        private final WeakReference<TodayWord> mActivity;  //* Main으로 옮길 예정 or 클래스 분할

        RecognitionHandler(TodayWord activity) {
            mActivity = new WeakReference<TodayWord>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TodayWord activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {       //실제 API 메시지를 처리 하는 부분
        switch (msg.what) {                         //샘플 코드 복사 한 것입니다
            case R.id.clientReady:
                // Now an user can speak.
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(int i = 0; i< 5; i++) {
                    if(results.get(i) != null)
                        resultchk[i] = results.get(i);
                }
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                txtResult.setText(mResult);           //이부분은 샘플에서 수정 한 부분으로 결과 5개 중에서 1번째 값을
                test.setText("");
                int i = 0;
                for (String result: resultchk) {
                    i++;
                    test.append("\"" + result + "\" - \"" + tw_word.getText() + "\"\n");
                    if(result.equals(tw_word.getText())) {
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
                break;                                      //사용하여 사전 입력으로 사용하고 있는 것입니다

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }
}
