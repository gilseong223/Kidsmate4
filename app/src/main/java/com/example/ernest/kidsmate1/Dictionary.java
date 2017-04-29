package com.example.ernest.kidsmate1;

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
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "4iZdE_YGdmxI9QVHDmDm";
    private NaverRecognizer naverRecognizer;
    private RecognitionHandler handler;
    private TextView txtResult;
    private AudioWriterPCM writer;
    private String mResult;
    private Button btnStart;
    private ImageButton imageButton;
    private EditText dicInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dic);
        dicInput = (EditText) findViewById(R.id.dicInput);
        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);
        imageButton = (ImageButton) findViewById(R.id.mic2);

        handler = new RecognitionHandler(this);         //API용 handle
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);    //API 객체
        imageButton.setOnClickListener(new View.OnClickListener() {

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

    @Override
    protected void onStart() {          //Activity start 할때
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize(); //API 객체 초기화 * Main으로 옮길 예정
    }

    static class RecognitionHandler extends Handler {       //API용 핸들, 메시지를 받아와서 현재 Activity에 전달
        private final WeakReference<Dictionary> mActivity;  //* Main으로 옮길 예정 or 클래스 분할

        RecognitionHandler(Dictionary activity) {
            mActivity = new WeakReference<Dictionary>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Dictionary activity = mActivity.get();
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
                dicInput.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                txtResult.setText(mResult);
                dicInput.setText(results.get(1));           //이부분은 샘플에서 수정 한 부분으로 결과 5개 중에서 1번째 값을
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
