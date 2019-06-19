package com.example.optiboxsmart.clp_solver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class Container {
    private double[] mDim;
    private List<Space> mSpaces;
    private List<Block> mBlocks;
    private HashMap<Box, Integer> mCargo; // Box Type -> nb of Boxes

    /* Constructors */

    public Container (double[] dim, HashMap<Box, Integer> boxes) {
        mDim = dim.clone();
        mCargo = new HashMap<Box, Integer>(boxes);
        mSpaces = new LinkedList<Space>();
        mSpaces.add(new Space(new double[] {0,0,0}, this.getDim()));
        mBlocks = new ArrayList<Block>();
    }

    /* Getters and Setters */
    public List<Space> getSpaces(){
        return new ArrayList<Space>(mSpaces);
    }

    public List<Object> getUsableSpaces(){
        return mSpaces.stream().filter(new Predicate<Space>() {
            @Override
            public boolean test(Space s) {
                return s.isUsable();
            }
        }).collect(Collectors.toList());

    }

    public int getNbOfBlocks(){
        return mBlocks.size();
    }

    public int getNbOfBoxes(){
        int sum = 0;
        for (Integer i : mCargo.values()){
            sum += i;
        }
        return sum;
    }

    public double[] getDim() {
        return mDim.clone();
    }

    public double getFilledVolume() {
        return mBlocks.stream().mapToDouble(new ToDoubleFunction<Block>() {
            @Override
            public double applyAsDouble(Block b) {
                return b.getVolume();
            }
        }).sum();
    }

    public double getEmptyVolume() {
        return mSpaces.stream().mapToDouble(new ToDoubleFunction<Space>() {
            @Override
            public double applyAsDouble(Space s) {
                return s.getVolume();
            }
        }).sum();
    }

    public double getVolume(){
        return Arrays.stream(mDim).reduce(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double d1, double d2) {
                return d1 * d2;
            }
        }).orElse(0);
    }

    public boolean hasSpace(Space s) {
        return mSpaces.contains(s);
    }

    public boolean isFullyLoaded(){
        // there is no more box
        int sum = mCargo.values().stream().mapToInt(new ToIntFunction<Integer>() {
            @Override
            public int applyAsInt(Integer integer) {
                return integer.intValue();
            }
        }).sum();
        return sum == 0;
    }

    public boolean isFull(){
        // there is no more space
        return mSpaces.size() == 0;
    }

    public boolean isEmpty(){
        return mSpaces.size() == 1 && mBlocks.size() == 0;
    }

    public double getEvaluation(int i){
        switch (i) {
            case 0: return getFilledVolume();
            case 1: return - mBlocks.size(); // the fewer blocks there are, the better
        }
        throw new IllegalArgumentException ("There are only 2 evaluation criteria.");
    }

    public int compareTo(Container other){
        for (int i = 0; i < 2; i++){
            if (this.getEvaluation(i) != other.getEvaluation(i)){
                return (this.getEvaluation(i) > other.getEvaluation(i)) ? 1 : -1;
            }
        }
        return 0;
    }

    private ArrayList<Block> findMaxBlocksInSpace(Space space){
        ArrayList<Block> maxBlocks = new ArrayList<Block> ();
        for (Box box : mCargo.keySet()) {
            for (Item item : box.getPermutedItems()) {
                if (mCargo.get(box) !=0 && space.isBigEnough(item)) {
                    int[] Nmax = new int[3];
                    Nmax[2] = Math.min((int) (space.getDim(2) / item.getDim(2)), mCargo.get(box));
                    Nmax[1] = Math.min((int) (space.getDim(1) / item.getDim(1)), (int) (mCargo.get(box) / Nmax[2]));
                    Nmax[0] = Math.min((int) (space.getDim(0) / item.getDim(0)), (int) (mCargo.get(box) / (Nmax[2]*Nmax[1])));
                    // System.out.println(item + " " + Arrays.toString(Nmax));
                    Block block = new Block(item, Nmax, space);
                    maxBlocks.add(block);
                }
            }
        }
        if (maxBlocks.size() == 0) space.setIsUsable(false);
        return maxBlocks;
    }

    public ArrayList<Block> findMaxBlocks(){
        ArrayList<Block> blocks = new ArrayList<Block> ();
        for (Space space : mSpaces){
            if (space.isUsable()) {
                ArrayList<Block> b = this.findMaxBlocksInSpace(space);
                blocks.addAll(b);
            }
        }
        return blocks;
    }

    public boolean addBlock(Block b) {
        if (hasSpace(b.getSpace())) {
            // add new space
            // TODO : merge mergeable spaces !
            for (Space space : b.splitSpace()){
                if (space.getVolume() != 0) mSpaces.add(space);
            }
            // remove old space
            mSpaces.remove(b.getSpace());
            // add block
            mBlocks.add(b);
            // remove Ntot boxes in mCargo
            Box box = b.getItem().getBox();
            Integer oldT = mCargo.get(box);
            mCargo.replace(box, oldT - b.getNtot());
            return true;
        }
        return false;
    }

    public List<double[]> toArray(){
        List<double[]> l = new ArrayList<>();
        for (Block b : mBlocks){
            l.addAll(b.toArray());
        }
        return l;
    }



    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        String s = "Container" + " {\n";
        s+= "  → result = {" + df.format(100*getFilledVolume() / getVolume()) + " %, " + getNbOfBoxes() +" box(es) left}\n";
        s+= "  → cargo  = "  + mCargo + "\n";
        // s+= "  → vf = " + getFilledVolume();
        // s+= "  → ve = " + getEmptyVolume();
        // s+= "  → vtot = " + (getFilledVolume() + getEmptyVolume());
        // s+= "  → v = " + getVolume() + "\n";
        s+= "  → spaces = " + mSpaces + "\n";
        s+= "  → blocks = " + mBlocks + "\n";
        s+= "}";
        return s;
    }
}