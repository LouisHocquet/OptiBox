package com.example.optiboxglasses;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BluetoothCom {
    private final int MESSAGE_READ = 0;
    private String s = "";
    private String result = "";
    private com.example.optiboxglasses.MyBluetoothService bs;
    private List<double[]> listeCartons = new ArrayList<>();

    public BluetoothCom(Handler handler, BluetoothSocket socket){
        bs = new MyBluetoothService(handler,socket);
    }
    //    public void addCarton(Double[] posCarton){
//        listeCartons.add(posCarton);
//    }
    public void setCartons(List<double[]> listeCartons) {this.listeCartons = listeCartons;}
    public void send() throws IOException {
        Gson gson = new GsonBuilder().create();
        s = gson.toJson(listeCartons);
        bs.write(s.getBytes());
    }
    public void run(){bs.run();}
    public String getResult(){
        return result;
    }
    public boolean isDataReceived() {
        return this.bs.isDataReceived();
    }

    public void setDataReceived(boolean dataReceived) {
        this.bs.setDataReceived(dataReceived);
    }

    public void setResult(String readMessage) {
        result=readMessage;
    }
}

