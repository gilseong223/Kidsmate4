package com.example.ernest.kidsmate1;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Created by User on 2017-04-29.
 */

public class MyHandler extends Handler {
    private final WeakReference<AppCompatActivity> mActivity;  //* Main으로 옮길 예정 or 클래스 분할

    MyHandler(AppCompatActivity activity) {
        mActivity = new WeakReference<AppCompatActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        AppCompatActivity activity = mActivity.get();
        if (activity != null) {
            if(activity instanceof Dictionary)
                ((Dictionary)activity).handleMessage(msg);
            else if(activity instanceof TodayWord)
                ((TodayWord)activity).handleMessage(msg);
            else if(activity instanceof Image)
                ((Image)activity).handleMessage(msg);
        }
    }
}
