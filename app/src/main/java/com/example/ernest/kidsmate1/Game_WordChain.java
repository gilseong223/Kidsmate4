package com.example.ernest.kidsmate1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;

public class Game_WordChain extends AppCompatActivity {
    private VoiceRecognizer mVoiceRecognizer;
    private EventHandler mEventHandler;

    private String correctAnswer;
    private String correctAnsersMean;
    private boolean isRightAnswer;

    private TextView textView_word;
    private TextView textView_mean;
    private TextView textView_debug;

    private Button button_start;
    private Button button_next;

    private String[] getWord() {
        SQLiteDatabase DB;
        Cursor cursor;
        int id = (int)(Math.random() * Integer.MAX_VALUE) % 3017 + 1; // 기왕이면 디비에서 전체 단어 갯수를 가지고 와서 나머지를 구하도록 하는게 좋지 않을까?
        String[] result = new String[]{"", ""};

        DB = Database.getDB(); //Database를 이용
        cursor = DB.rawQuery("SELECT word, mean FROM dic WHERE id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result[0] = cursor.getString(0);
            result[1] = cursor.getString(1);
        }
        cursor.close();

        return result;
    }

    private boolean makeQuiz(){
        String[] todayWord = getWord();
        correctAnswer = todayWord[0];
        correctAnsersMean = todayWord[1];
        isRightAnswer = false;
        textView_word.setText(correctAnswer);
        textView_mean.setText(correctAnsersMean);
        return true;
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                button_start.setText("연결됨");
                break;
            case R.id.audioRecording:
                break;
            case R.id.partialResult:
                String partialResult = (String) msg.obj;
                textView_debug.append(partialResult+" ");
                if(!isRightAnswer && partialResult.toLowerCase().equals(correctAnswer.toLowerCase())){
                    isRightAnswer = true;
                    textView_debug.append("정답입니다.\n");
                }
                textView_debug.append("\n");
                break;
            case R.id.endPointDetected:
                break;
            case R.id.finalResult:
                List<String> results = ((SpeechRecognitionResult)(msg.obj)).getResults();
                for (String result: results) {
                    if (isRightAnswer) break;
                    textView_debug.append(result+" ");
                    if(result.toLowerCase().equals(correctAnswer.toLowerCase())) {
                        isRightAnswer = true;
                        textView_debug.append("정답입니다.\n");
                    }
                }
                textView_debug.append("\n");
                if (isRightAnswer) {
                    makeQuiz();
                }else{
                    textView_debug.append("다시 발음 해 보세요.\n");
                }
                break;
            case R.id.recognitionError:
                MessageDialogFragment.newInstance("Error code : " + msg.obj.toString());
                button_start.setText("시작");
                button_start.setEnabled(true);
                break;
            case R.id.endPointDetectTypeSelected:
                break;
            case R.id.clientInactive:
                button_start.setText("시작");
                button_start.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventHandler = new EventHandler(this);

        setContentView(R.layout.game_basic);

        mVoiceRecognizer = VoiceRecognizer.getInstance(this);

        textView_word = (TextView) findViewById(R.id.textView_word);
        textView_mean = (TextView) findViewById(R.id.textView_mean);
        textView_debug = (TextView) findViewById(R.id.textView_debug);

        button_next = (Button) findViewById(R.id.button_next);
        button_start = (Button) findViewById(R.id.button_start);

        button_next.setEnabled(true);
        button_start.setEnabled(true);

        button_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                makeQuiz();
            }
        });

        button_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!mVoiceRecognizer.isRunning()) {
                    button_start.setText("연결중");
                    mVoiceRecognizer.recognize();
                } else {
                    button_start.setEnabled(false);
                    mVoiceRecognizer.stop();
                }
            }
        });

        makeQuiz();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVoiceRecognizer.initialize(mEventHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVoiceRecognizer.release();
    }

    public static class EventHandler extends Handler {
        private final WeakReference<Game_WordChain> mActivity;
        EventHandler(Game_WordChain activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            Game_WordChain activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}