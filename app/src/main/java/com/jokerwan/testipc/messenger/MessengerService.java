package com.jokerwan.testipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.jokerwan.testipc.MyConstants;

public class MessengerService extends Service {

    private static final String TAG = MessengerService.class.getSimpleName();

    public MessengerService() {
    }

    private static class MessengerHandeler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_CLIENT :
//                    Log.i(TAG,"33333333333333333333333333333");
//                    Log.i(TAG,"receive msg from client :" + ((User)msg.getData().getParcelable("user")).getName());
                    Log.i(TAG,"receive msg from client :" + msg.getData().getString("msg"));

                    //回复客户端
                    Messenger client = msg.replyTo;
                    Message message = Message.obtain(null,MyConstants.MSG_FROM_SERVICE);
                    Bundle data = new Bundle();
                    data.putString("reply","嗯，已收到您的消息，稍后回复您。");
                    message.setData(data);
                    try {
                        client.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    //Messenger的作用，将客户端发送的消息传递给MessengerHandler处理
    private final Messenger messenger = new Messenger(new MessengerHandeler());

    @Override
    public IBinder onBind(Intent intent) {
        //返回messenger里面的Binder对象
        return messenger.getBinder();
    }
}
