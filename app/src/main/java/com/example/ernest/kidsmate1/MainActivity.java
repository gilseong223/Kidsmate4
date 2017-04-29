package com.example.ernest.kidsmate1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    public static DatabaseHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new DatabaseHelper(this);       //DB를 관리하는 Helper 객체 생성

        File database = new File(DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME);    //기기에서 DB파일 open
        if(false == database.exists()) {                                                    //DB파일 존재여부 확인
            try{mDBHelper.getReadableDatabase();}catch (Exception e){e.printStackTrace();}  //이건 뭔지 모르겠네요
            //Copy db                                                                               //copy를 하고 해야될 것 같은데..
            if(copyDatabase(this)) {                                                        //기기에 DB파일 없을 때 assets의 DB파일 복사
                Toast.makeText(this, "Copy database succes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {       //뒤로가기 버튼 작동 중지
    }

    protected void mainStartClicked(View v) {               //메뉴 선택화면으로 이동
        Intent intent = new Intent(MainActivity.this, SelectContents.class);
        startActivity(intent);
        finish();
    }

    protected void mainOffClicked(View v) {             //종료 버튼
        finish();
        System.exit(0);
    }

    @Override
    protected void onStart() {              //Activity start 될 때
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)      //녹음, 저장 권한 확인 후 설정
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "음성 인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void showPermissionMessageDialog() {        //권한이 없을 때 dialog를 fragment로 띄우기 위해서 메시지 전달
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    @Override
    public void onMessageDialogDismissed() {        //권한 없음 경고 dialog가 종료 될 때 권한 설정을 위한 팝업
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private boolean copyDatabase(Context context) {     //assets의 DB 파일을 기기에 복사하는 실제 코드
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
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
}
