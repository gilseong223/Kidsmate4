package com.example.ernest.kidsmate1;

import android.app.Application;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by User on 2017-04-29.
 */

public class Database extends Application {
    private static Database Database;
    private static DatabaseHelper mDBHelper;
    private static SQLiteDatabase DB;
    public static final String DBLOCATION = "/data/data/com.example.ernest.kidsmate1/databases/";
    public static final String DBNAME = "testDB.db";


    /////////////////////////////////////////////////////////////////////     API
    private static final String TAG = Database.class.getSimpleName();
    private static final String CLIENT_ID = "4iZdE_YGdmxI9QVHDmDm";
    private static NaverRecognizer naverRecognizer;
    private RecognitionHandler handler;
    private AudioWriterPCM writer;
    private static String mResult;
    private static Button btnStart;
    private static TextView txtResult;
    private static Handler mhandler;
    private Message myMessage;
    private static String mmResult;
    private static String[] resultchk = new String[]{"", "", "", "", ""};
    /////////////////////////////////////////////////////////////////////////

    public static SQLiteDatabase getDB() {
        return DB;
    }

    public static void closeDB() {
        if(DB != null)
            DB.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Database = this;
        Database.initializeInstance();
        /////////////////////////////////////////////////////////        API
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        //////////////////////////////////////////////////////////////////
    }

    protected void initializeInstance() {
        mDBHelper = new DatabaseHelper(this);
        File database = new File(DBLOCATION + DBNAME);
        if(false == database.exists()) {
            try{mDBHelper.getReadableDatabase();}catch (Exception e){e.printStackTrace();}

            if(copyDatabase(this)) {
                Toast.makeText(this, "Copy database succes", Toast.LENGTH_SHORT).show();
            }
        }
        DB = mDBHelper.openDatabase();
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DBNAME);
            String outFileName = DBLOCATION + DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.v("MainActivity", "DB copied");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    ////////////////////////////////////////////////////   API
    static class RecognitionHandler extends Handler {       //API용 핸들, 메시지를 받아와서 현재 Activity에 전달
        private final WeakReference<Database> mActivity;  //* Main으로 옮길 예정 or 클래스 분할

        RecognitionHandler(Database activity) {
            mActivity = new WeakReference<Database>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Database activity = mActivity.get();
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
                txtResult.setText(mResult);          //이부분은 샘플에서 수정 한 부분으로 결과 5개 중에서 1번째 값을
                int i = 0;
                for (String result: resultchk) {
                    i++;
                    if (!result.equals("")) {
                        mmResult = result.toLowerCase();
                        break;
                    }
                }
                myMessage = Message.obtain(mhandler, R.id.endRecognize);
                myMessage.sendToTarget();
                break;                                      //사용하여 사전 입력으로 사용하고 있는 것입니다

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                myMessage = Message.obtain(mhandler, R.id.endRecognize);
                myMessage.sendToTarget();
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                //myMessage = Message.obtain(mhandler, R.id.endRecognize);
                //myMessage.sendToTarget();
                break;
        }
    }

    public static void naverRecognizerInitialize() {
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    public static void myRecognize(TextView textResult, Button buttonStart, Handler myHandler) {                             //마이크 버튼 눌렀을 때
        txtResult = textResult;
        btnStart = buttonStart;
        mhandler = myHandler;
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

            //naverRecognizer.getSpeechRecognizer().stop();
        }
    }
    public static String getmmResult() {
        return mmResult;
    }
    public static String[] getResultCHK() {
        return resultchk;
    }
    ///////////////////////////////////////////////////////////////////////
}
