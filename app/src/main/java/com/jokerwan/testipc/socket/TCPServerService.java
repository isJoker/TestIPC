package com.jokerwan.testipc.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * 服务端代码
 */
public class TCPServerService extends Service {

    private static final String TAG = TCPServerService.class.getSimpleName();
    private boolean mIsServiceDestoryed;
    private String[] mDedinedMessages = new String[]{
            "你好啊",
            "请问你叫什么名字呀？",
            "今天三亚天气真不错",
            "你知道吗？我可是可以和多个人同时聊天的哦",
            "给你讲个笑话吧：据说爱笑的人运气不会太差，不知道真假"
    };

    public TCPServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TCPServer()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }

    private class TCPServer implements Runnable{

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                //监听本地8688端口
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                Log.d(TAG,"establish tcp server failed ,port:8688");
                e.printStackTrace();
                return;
            }

            while(!mIsServiceDestoryed) {
                try {
                    //接受客户端请求
                    final Socket client = serverSocket.accept();
                    Log.d(TAG, "accept");
                    new Thread(){
                        public void run(){
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        //用于接受客户端消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //用于向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        out.println("欢迎来到聊天室");
        while(!mIsServiceDestoryed) {
            String str = in.readLine();
            Log.d(TAG, "msg from client : " + str);
            if(str == null) {
                //客户端断开链接
                break;
            }

            int i = new Random().nextInt(mDedinedMessages.length);
            String message = mDedinedMessages[i];
            out.println(message);
            Log.d(TAG, "send : " + message);
        }

        Log.d(TAG, "client quit.");
        //关闭流
        out.close();
        in.close();
        client.close();
    }
}
