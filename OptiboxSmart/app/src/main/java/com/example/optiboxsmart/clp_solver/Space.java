package com.example.optiboxsmart.clp_solver;

import java.util.Arrays;

public class Space {
    private double[] mDim;
    private double[] mPos;
    private boolean mIsUsable;

    public Space(double[] pos, double[] dim) {
        mDim = dim;
        mPos = pos;
        mIsUsable = true;
    }

    public double[] getDim(){
        return mDim.clone();
    }

    public double getDim(int i){
        return mDim[i];
    }

    public double[] getPos(){
        return mPos; // TODO clone ??
    }

    public double getPos(int i){
        return mPos[i];
    }

    public double getVolume(){
        double v = 1;
        for (int i = 0; i < 3; i++) {
            v *= this.getDim(i);
        }
        return v;
    }

    public boolean isBigEnough(Item item){
        for (int i = 0; i < 3; i++){
            if (mDim[i] < item.getDim(i)) {
                return false;
            }
        }
        return true;

    }

    public void setIsUsable(boolean b){
        mIsUsable = b;
    }

    public boolean isUsable(){
        return mIsUsable;
    }

    @Override
    public String toString() {
        String s = "Space ";
        s+= Arrays.toString(mDim);
        s+= Arrays.toString(mPos);
        return s;
    }
}