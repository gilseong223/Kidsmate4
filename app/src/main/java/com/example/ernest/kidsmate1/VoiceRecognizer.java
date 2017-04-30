package com.example.ernest.kidsmate1;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechConfig.EndPointDetectType;
import com.naver.speech.clientapi.SpeechConfig.LanguageType;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

import java.lang.ref.WeakReference;
import java.util.List;

class VoiceRecognizer {
    private static VoiceRecognizer mVoiceRecognizer = null; // singleton

    private static Handler mHandler = null;
    private static SpeechRecognizer mRecognizer;

    private static RecogListener mRecogListener; // inner class

    private static AudioWriterPCM writer;

    private static final String TAG = VoiceRecognizer.class.getSimpleName();
    private static final String CLIENT_ID = "4iZdE_YGdmxI9QVHDmDm";

    private VoiceRecognizer(Context context) {
        try {
            mRecognizer = new SpeechRecognizer(context, CLIENT_ID); // generating client
        } catch (SpeechRecognitionException e) {
            // 예외가 발생하는 경우는 아래와 같습니다.
            //   1. activity 파라미터가 올바른 MainActivity의 인스턴스가 아닙니다.
            //   2. AndroidManifest.xml에서 package를 올바르게 등록하지 않았습니다.
            //   3. package를 올바르게 등록했지만 과도하게 긴 경우, 256바이트 이하면 좋습니다.
            //   4. clientId가 null인 경우
            //
            // 개발하면서 예외가 발생하지 않았다면 실서비스에서도 예외는 발생하지 않습니다.
            // 개발 초기에만 주의하시면 됩니다.
            e.printStackTrace();
        }
        mRecogListener = new RecogListener();
        mRecognizer.setSpeechRecognitionListener(mRecogListener);
    }

    public static VoiceRecognizer getInstance(Context context) { // constructor
        if(mVoiceRecognizer == null) {
            synchronized (VoiceRecognizer.class) {
                if(mVoiceRecognizer == null) {
                    mVoiceRecognizer = new VoiceRecognizer(context);
                }
            }
        }
        return mVoiceRecognizer;
    }


    public static void setHandler(Handler handler) {
        /*
        음성인식을 사용하는 액티비티의 핸들러를 저장하는 메소드. 싱글톤 클래스 보이스 레코그나이저를 이용하기 위해서는 반드시 액티비티의 핸들러를 넘겨야함.
         */
        mHandler = handler;
    }

    public static void recognize() {
        try {
            mRecognizer.recognize(new SpeechConfig(LanguageType.ENGLISH, EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }

    public static void initialize(){
        /*
        음성인식 전에 반드시 이 메소드를 실행시켜야 함.
         */
        mRecognizer.initialize();
    }

    public static void release(){
        /*
        음성인식 후에 반드시 이 메소드를 실행시켜야 함.
         */
        mRecognizer.release();
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady: // 음성인식 준비 가능
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            case R.id.partialResult:
                //mResult = (String) (msg.obj);
                //txtResult.setText(mResult);
                break;
            case R.id.finalResult: // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                //mResult = strBuf.toString();
                //txtResult.setText(mResult);
                break;
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                //mResult = "Error code : " + msg.obj.toString();
                //txtResult.setText(mResult);
                //btnStart.setText(R.string.str_start);
                //btnStart.setEnabled(true);
                break;
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                //btnStart.setText(R.string.str_start);
                //btnStart.setEnabled(true);
                break;
        }
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<VoiceRecognizer> mVoiceRecognizer;

        RecognitionHandler(VoiceRecognizer recognizer) {
            mVoiceRecognizer = new WeakReference<VoiceRecognizer>(recognizer);
        }
        @Override
        public void handleMessage(Message msg) {
            VoiceRecognizer recognizer = mVoiceRecognizer.get();
            if (recognizer != null) {
                recognizer.handleMessage(msg);
            }
        }
    }

    static class RecogListener implements SpeechRecognitionListener {
        @Override
        @WorkerThread
        public void onInactive() {
            Log.d(TAG, "Event occurred : Inactive");
            Message msg = Message.obtain(mHandler, R.id.clientInactive);
            msg.sendToTarget();
        }

        @Override
        @WorkerThread
        public void onReady() {
            Log.d(TAG, "Event occurred : Ready");
            Message msg = Message.obtain(mHandler, R.id.clientReady);
            msg.sendToTarget();
        }

        @Override
        @WorkerThread
        public void onRecord(short[] speech) {
            Log.d(TAG, "Event occurred : Record");
            Message msg = Message.obtain(mHandler, R.id.audioRecording, speech);
            msg.sendToTarget();
        }

        @Override
        @WorkerThread
        public void onPartialResult(String result) {
            Log.d(TAG, "Partial Result!! (" + result + ")");
            Message msg = Message.obtain(mHandler, R.id.partialResult, result);
            msg.sendToTarget();
        }

        @Override
        @WorkerThread
        public void onEndPointDetected() {
            Log.d(TAG, "Event occurred : EndPointDetected");
        }

        @Override
        @WorkerThread
        public void onResult(SpeechRecognitionResult result) {
            Log.d(TAG, "Final Result!! (" + result.getResults().get(0) + ")");
            Message msg = Message.obtain(mHandler, R.id.finalResult, result);
            msg.sendToTarget();
        }

        @Override
        @WorkerThread
        public void onError(int errorCode) {
            Log.d(TAG, "Error!! (" + Integer.toString(errorCode) + ")");
            Message msg = Message.obtain(mHandler, R.id.recognitionError, errorCode);
            msg.sendToTarget();
        }

        @Override
        @WorkerThread
        public void onEndPointDetectTypeSelected(EndPointDetectType epdType) {
            Log.d(TAG, "EndPointDetectType is selected!! (" + Integer.toString(epdType.toInteger()) + ")");
            Message msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType);
            msg.sendToTarget();
        }
    }
}