package com.example.optiboxsmart.clp_solver;

import java.util.Arrays;

public class Item {
    private double[] mDim;
    private Box mBox;

    public Item(Box box){
        mDim = box.getDim();
        mBox = box;
    }

    public void rotate(char axis){
        double temp;
        switch(axis) {
            case 'x':
                temp = mDim[2];
                mDim[2] = mDim[1];
                mDim[1] = temp;
                break;
            case 'y':
                temp = mDim[2];
                mDim[2] = mDim[0];
                mDim[0] = temp;
                break;
            case 'z':
                temp = mDim[0];
                mDim[0] = mDim[1];
                mDim[1] = temp;
                break;
            default :
                System.out.println("wrong axis : " + axis);
        }

    }

    public double[] getDim(){
        return mDim.clone();
    }

    public double getDim(int i){
        return mDim[i];
    }

    public Box getBox(){
        return mBox;
    }

    @Override
    public String toString() {
        return "BoxItem" + "(" + Arrays.toString(mDim) +")";
    }
}