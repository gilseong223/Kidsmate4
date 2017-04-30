package com.example.ernest.kidsmate1;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by User on 2017-04-15.
 */

public class WordChain extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_chain);

        listView = (ListView) findViewById(R.id.listview_WC);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
