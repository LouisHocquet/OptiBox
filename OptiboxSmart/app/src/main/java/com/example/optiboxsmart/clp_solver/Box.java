package com.example.optiboxsmart.clp_solver;

import java.util.ArrayList;
import java.util.Arrays;

public class Box {
    private double[] mDim;
    private ArrayList<Item> mPermutedItems;
    private byte[] mPermutations;

    public Box (double[] dim, byte[] orientations) {
        mDim = dim;
        mPermutations = orientations;
        mPermutedItems = new ArrayList<Item>();

        // TODO : use permutation instead
        byte counter = 0;
        Item item;
        for (int i = 0; i <= mPermutations[0]; i++) {
            for (int j = 0; j <= mPermutations[1]; j++) {
                for (int k = 0; k <= mPermutations[2]; k++) {
                    counter ++;
                    if (counter <= 6) {
                        // System.out.println(i + " " + j + " " + k);
                        item = new Item(this);
                        if (i==1) item.rotate('x');
                        if (j==1) item.rotate('y');
                        if (k==1) item.rotate('z');
                        mPermutedItems.add(item);
                    }
                }
            }
        }
    }


    public Box (double[] dim){
        this(dim, new byte[]{0, 0, 1});
    }

    public Box (double x, double y, double z) {
        this(new double[]{x,y,z});
    }

    public Box (double x, double y, double z, byte[] orientations){
        this(new double[]{x,y,z}, orientations);
    }

    public double[] getDim(){
        return mDim.clone();
    }

    public double getDim(int i){
        return mDim[i];
    }

    public ArrayList<Item> getPermutedItems(){
        return mPermutedItems;
    }

    @Override
    public String toString() {
        return "Box" + "(" + Arrays.toString(mDim) +")";

    }
}
