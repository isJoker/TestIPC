package com.jokerwan.testipc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jokerwan.testipc.aidl.BookManagerActivity;
import com.jokerwan.testipc.contentprovider.ProviderActivity;
import com.jokerwan.testipc.messenger.MessengerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onButton1Click(View view){
        Intent intent = new Intent(this, MessengerActivity.class);
        startActivity(intent);
    }

    public void onButton2Click(View view){
        Intent intent = new Intent(this, BookManagerActivity.class);
        startActivity(intent);
    }

    public void onButton3Click(View view){
        Intent intent = new Intent(this, ProviderActivity.class);
        startActivity(intent);
    }
}
