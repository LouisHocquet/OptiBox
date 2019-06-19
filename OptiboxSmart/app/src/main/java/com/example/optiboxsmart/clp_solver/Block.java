package com.example.optiboxsmart.clp_solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntBinaryOperator;

public class Block {
    private Item mItem;
    private Space mSpace;
    private int[] mN;
    private double[] mPos;
    private double[] mCriteria;

    /* Constructors */

    public Block(Item item, int[] n, Space space){
        mItem = item;
        mSpace = space;
        mN = n;
        mPos = space.getPos();
        mCriteria = this.initCriteria();
    }

    private double[] initCriteria() {
        double[] criteria = new double[6];
        // Criteria 0 : normalized space utilization
        for (int i = 0; i < 3; i++) {
            double gap = mSpace.getDim(i) - this.getDim(i);
            double ad = gap == 0 ? 1 : 1/gap;
            criteria[0] += ad;
        }
        // Criteria 1 : (-) differnce between the heigth of the space and of the block
        criteria[1] = - (mSpace.getDim(2) - this.getDim(2));
        // Criteria 2 : volume utilization of the empey space
        criteria[2] = this.getVolume() / mSpace.getVolume();
        // Criteria 3 : base area
        criteria[3] = this.getDim(0) * this.getDim(1);
        // Criteria 4 : (-) lengthwise protusion
        criteria[4] = - (this.getDim(0) + mPos[0]);
        // Criteria 5 : (-) widthwise coordinate
        criteria[5] = - mPos[1];
        return criteria;
    }

    /* Getters & Setters */

    // public double getPos(int i) {
    // 	return mPos[i];
    // }

    public double getDim(int i) {
        return mN[i] * mItem.getDim(i);
    }

    public double[] getDim() {
        double[] dim = new double[3];
        for (int i = 0; i < 3; i++) {
            dim[i] = this.getDim(i);
        }
        return dim;
    }

    public int getN(int i){
        return mN[i];
    }

    public int[] getN(){
        return mN.clone();
    }

    public int getNtot() {
        return Arrays.stream(mN).reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int n1, int n2) {
                return n1 * n2;
            }
        }).orElse(0);
    }

    public void setN(int[] n){
        mN = n;
    }

    public double getVolume(){
        double v = 1;
        for (int i = 0; i < 3; i++) {
            v *= this.getDim(i);
        }
        return v;
    }

    public double[] getCriteria(){
        return mCriteria.clone();
    }

    public double getCriteria(int i){
        return mCriteria[i];
    }

    public Space getSpace(){
        return mSpace;
    }

    public Item getItem(){
        return mItem;
    }

    public Space[] splitSpace() {
        Space[] spaces = new Space[3];
        double[] bd = this.getDim();
        double[] sd = mSpace.getDim();
        double[] sp = mSpace.getPos();
        spaces[0] = new Space(
                new double[]{sp[0],	sp[1]+bd[1], sp[2]},
                new double[]{bd[0], sd[1]-bd[1], sd[2]});
        spaces[1] = new Space(
                new double[]{sp[0], sp[1], sp[2]+bd[2]},
                new double[]{bd[0], bd[1], sd[2]-bd[2]});
        spaces[2] = new Space(
                new double[]{sp[0]+bd[0], sp[1], sp[2]},
                new double[]{sd[0]-bd[0], sd[1], sd[2]});
        return spaces;
    }

    public int compareTo(Block other, int[] criteriaIndexes){
        for (int i : criteriaIndexes) {
            if (this.getCriteria(i) != other.getCriteria(i)) {
                return (this.getCriteria(i) > other.getCriteria(i)) ? 1 : -1;
            }
        }
        return 0;
    }

    public int compareTo(Block other){
        return this.compareTo(other, new int[] {0, 1, 2, 3, 4, 5});
    }

    public List<double[]> toArray(){
        List<double[]> l = new ArrayList<>();
        double[] iDim = mItem.getDim();
        for (int i = 0; i < mN[0]; i++) {
            for (int j = 0; j < mN[1]; j++) {
                for (int k = 0; k < mN[2]; k++) {
                    l.add(new double[] {
                            i*iDim[0] + mPos[0],
                            j*iDim[1] + mPos[1],
                            k*iDim[2] + mPos[2],
                            iDim[0],
                            iDim[1],
                            iDim[2],
                    });
                }
            }
        }
        return l;

    }

    @Override
    public String toString(){
        String s = "Block " + Arrays.toString(mN).replace(", ", "x");
        s += Arrays.toString(getDim());
        s += Arrays.toString(mPos);
        return s;
    }
}