package com.jokerwan.testipc.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jokerwan.testipc.MyConstants;
import com.jokerwan.testipc.R;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = MessengerActivity.class.getSimpleName();
    private Messenger mService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = new android.os.Messenger(iBinder);
            Message message = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            //测试“自定义的parcelable对象无法通过object传输”
//            message.obj = new User("JokerWan");
            Bundle data = new Bundle();
//            data.putParcelable("user",new User("JokerWan"));
            data.putString("msg","hello , this is client.");
            message.setData(data);

            //把接收服务端的Messenger传递给服务器
            message.replyTo = getReplyMessenger;

            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private Messenger getReplyMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_SERVICE :
                    Log.i(TAG,"receive msg from service" + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaaenger);

        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
