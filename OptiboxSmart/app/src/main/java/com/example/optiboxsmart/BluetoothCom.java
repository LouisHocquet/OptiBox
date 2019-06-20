package com.example.optiboxsmart;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class BluetoothCom {
    private final int MESSAGE_READ = 0;
    private String s = "";
    private String result = "";
    private MyBluetoothService bs;
    private List<double[]> listeCartons = new ArrayList<>();
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.endsWith("#"))result=readMessage;
                    break;

            }
        }
    };
    public BluetoothCom(BluetoothSocket socket){

        bs = new MyBluetoothService(handler);

    }
//    public void addCarton(Double[] posCarton){
//        listeCartons.add(posCarton);
//    }
    public void setCartons(List<double[]> listeCartons) {this.listeCartons = listeCartons;}
    public void send() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        s = gson.toJson(listeCartons);
        bs.write(s.getBytes());
    }
    public void run(){bs.run();}
    public String getResult(){
        return result;
    }
    }

