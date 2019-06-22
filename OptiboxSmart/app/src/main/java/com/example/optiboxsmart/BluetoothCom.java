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
    private String result = "";
    private MyBluetoothService bs;
    private List<int[]> listeCartons = new ArrayList<>();

    public BluetoothCom(Handler handler,BluetoothSocket socket){
        bs = new MyBluetoothService(handler,socket);

    }
//    public void addCarton(Double[] posCarton){
//        listeCartons.add(posCarton);
//    }
    public void setCartons(List<int[]> listeCartons) {this.listeCartons = listeCartons;}
    public void send() throws IOException {
        Gson gson = new GsonBuilder().create();
        String s = gson.toJson(listeCartons);
        bs.write(s.getBytes());
    }
    public void run(){bs.run();}
    public String getResult(){
        return result;
    }
    }

