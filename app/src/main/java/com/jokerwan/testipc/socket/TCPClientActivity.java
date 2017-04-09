package com.jokerwan.testipc.socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jokerwan.testipc.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MESSAGE_RECEIVER_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;
    private static final String TAG = TCPClientActivity.class.getSimpleName();
    private Button btn_send;
    private TextView msg_main;
    private EditText et_main;

    private PrintWriter mPrintWriter;
    private Socket mClientSocket;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVER_NEW_MSG :
                    msg_main.setText(msg_main.getText() + (String)msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED :
                    btn_send.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        btn_send = (Button) findViewById(R.id.btn_send);
        msg_main = (TextView) findViewById(R.id.msg_main);
        et_main = (EditText) findViewById(R.id.et_main);
        btn_send.setOnClickListener(this);

        Intent srevice = new Intent(this,TCPServerService.class);
        startService(srevice);
        new Thread(){
            public void run(){
                connectTCPServer();
            }
        }.start();
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null){
            try {
                socket = new Socket("localhost",8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                handler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "connectTCPServer: connect server success");
            } catch (IOException e) {
                e.printStackTrace();
                SystemClock.sleep(1000);
                Log.d(TAG, "connectTCPServer: connect tcp server failed,retry...");
            }
        }

        //接收服务器端的消息
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(!TCPClientActivity.this.isFinishing()) {
                String msg = reader.readLine();
                Log.d(TAG, "connectTCPServer: receive msg: " + msg);
                if(msg != null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showMsg = "server " + time + ":" + msg + "\n";
                    handler.obtainMessage(MESSAGE_RECEIVER_NEW_MSG,showMsg).sendToTarget();
                }
            }

            Log.d(TAG, "connectTCPServer: quit.");
            mPrintWriter.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btn_send) {
            final String msg = et_main.getText().toString();
            if(!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                mPrintWriter.println(msg);

                et_main.setText("");
                String time = formatDateTime(System.currentTimeMillis());
                final String showMsg = "self " + time + ":" + msg + "\n";
                msg_main.setText(msg_main.getText() + showMsg);
            }
        }
    }

    private String formatDateTime(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    @Override
    protected void onDestroy() {
        if(mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
