package com.example.ernest.kidsmate1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

/**
 * Created by User on 2017-04-15.
 */

public class WordChain extends AppCompatActivity {
    TextView first, second, third, texttest;
    Button buttontest;
    ImageButton mic;
    private SQLiteDatabase DB;
    // private RecognitionHandler handler;
    //  private NaverRecognizer naverRecognizer;
    //  private AudioWriterPCM writer;
    private static final String CLIENT_ID = "123123123";
    private Cursor cursor;
   // private String mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_chain);

        first = (TextView) findViewById(R.id.First);
        second = (TextView) findViewById(R.id.Second);
        third = (TextView) findViewById(R.id.Third);
        texttest = (TextView) findViewById(R.id.Texttest);
        buttontest = (Button) findViewById(R.id.Buttontest);
        mic = (ImageButton) findViewById(R.id.mic5);

        firstWordShow();

      /*  handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    naverRecognizer.recognize();
                } else {
                    mic.setEnabled(false);
                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        }); */
    }
    /*
        private void handleMessage(Message msg) {
            switch(msg.what) {
                case R.id.clientReady:
                    third.setText("Connected");
                    writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                    writer.open("Test");
                    break;
                case R.id.audioRecording:
                    writer.write((short[]) msg.obj);
                    break;
                case R.id.partialResult:
                    mResult = (String) (msg.obj);
                    break;
                case R.id.finalResult:
                    SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                    List<String> results = speechRecognitionResult.getResults();
                    StringBuilder strBuf = new StringBuilder();
                    for (String result : results) {
                        strBuf.append(result);
                        strBuf.append("\n");
                    }
                    mResult = strBuf.toString();
                    third.setText(mResult);
                case R.id.recognitionError:
                    if (writer != null) {
                        writer.close();
                    }
                    mResult = "Error code : " + msg.obj.toString();
                    third.setText(mResult);
                    mic.setEnabled(true);
                    break;
                case R.id.clientInactive:
                    if (writer != null) {
                        writer.close();
                    }
                    mic.setEnabled(true);
                    break;
            }
        }
    *//*
    static class RecognitionHandler extends Handler {
        private final WeakReference<WordChain> wChain;
        RecognitionHandler(WordChain activity) {
            wChain = new WeakReference<WordChain> (activity);
        }

        public void handleMessage(Message msg) {
            WordChain activity = wChain.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
*/
    protected void onStart() {
        super.onStart(); // 음성인식 서버 초기화는 여기서
        //    naverRecognizer.getSpeechRecognizer().initialize();
    }
/*
            public void onClickedTest(View v) {
                if (isFirstAlphabet(second, texttest) != true ) // isFirstAlphabet이 안됨....
                    Toast.makeText(getApplicationContext(), "다시 써", Toast.LENGTH_SHORT).show();
                else {
                    third.setText(texttest.getText());
                    texttest.setText("");
                    try {
                        Thread.sleep(1000);
                        showToThird();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }*/

    public void onClickedTest(View v) {
        decideWord();
        texttest.setText("");

    }

    public void firstWordShow() { // 첫 단어를 데이터베이스에서 무작위로 가져온다
        Random random = new Random();

        String word = null;
        //String word2 = "apple";

        DB = Database.getDB();
        cursor = DB.rawQuery("SELECT mean FROM dic WHERE word = '" + random.nextInt(100)+1 + "' COLLATE NOCASE", null);
        cursor.moveToFirst();                                           //db를 이용해서 질의하고 커서를 이용해서 활용 하는 부분
        if (!cursor.isAfterLast()) {
            word = cursor.getString(0);
        }
        cursor.close();
        second.setText(word);
        findLastAlphabet();
    }

    public void findLastAlphabet() { // Second에 있는 단어의 마지막 알파벳을 찾고 Third에 표시한다.
        String temp;
        temp = second.getText().toString();
        third.setText(temp.charAt(temp.length()-1) + "   ");
    }

    public boolean isFirstAlphabet() {    // 사용자가 말한 단어의 첫 알파벳이 Second에 있는 단어의 마지막 알파벳과 같은지 확인
        String temp1, temp2;
        temp1 = second.getText().toString();
        temp2 = texttest.getText().toString();

        if (temp1.charAt(temp1.length()-1) == temp2.charAt(0) )
            return true; // TRUE

        return false; // FALSE
    }

    /* public void decideWord() {

         if(isFirstAlphabet()) {
             third.setText(texttest.getText());
             showToThird();
             findLastAlphabet();
         }
         else findLastAlphabet();

     }
     */
    public void decideWord() {
        DB = Database.getDB();
        cursor = DB.rawQuery("SELECT word FROM dic WHERE word = '" + texttest.getText().toString() + "' COLLATE NOCASE", null);
        cursor.moveToFirst();                                           //db를 이용해서 질의하고 커서를 이용해서 활용 하는 부분
        if (!cursor.isAfterLast() && isFirstAlphabet()) {
            third.setText(cursor.getString(0));
            showToThird();
            findLastAlphabet();
        }
        else findLastAlphabet();
        cursor.close();
    }

    public void showToThird() { // 한칸씩 위로 시프트
        first.setText(second.getText());
        second.setText(third.getText());
        findLastAlphabet();
    }

    public void timeOver() { // 타임오버

    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}