package com.example.optiboxsmart.clp_solver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Loader {
    /* Initialization of the Container */
    private double[] mDimContainer;
    private HashMap<Box, Integer> mCargo; // Box Type -> nb of Boxes
    /* Search parameters -- change loader Behaviourr */
    final int TAU; // number of the best selected blocks
    final double RHO; // [0, 1],
    final int N1; // coeff used in the bias function if volume of the current solution < mRho * mBestVol
    final int N2; // coeff used in the bias function otherwise
    final int ITER; // nb of generated Container
    final int K; // every K iter the selection probabilities are reevaluated
    final int PHI; // constant for the reevaluation of selection probabilities of an evaluation set
    /* Iteration variables */
    private double mBestVol; // volume of the best container solution

    /* Contructors */

    public Loader(double[] dim, HashMap<Box, Integer> boxes){
        // Container propreties
        mDimContainer = dim;
        mCargo = boxes;
        // Seach parameters
        TAU = 10;
        RHO = 0.8;
        N1 = boxes.size() <= 8 ? 2 : 3;
        N2 = boxes.size() <= 8 ? 3 : 4;
        ITER = 250;
        K = 100;
        PHI = 10;
        // Initialization of 'iteration' variables
        mBestVol = 0;
    }

    /* Getters and Setters */

    public double[] getDimContainer(){
        return mDimContainer.clone();
    }

    public HashMap<Box, Integer> getBoxes() {
        return new HashMap<Box, Integer>(mCargo);
    }


    /* Private Mathods */

    /**
     * x -> x^(-n)
     * where n = N1 if volume < RHO * mBestVol
     * else n = N2
     */
    private double bias(int x, double volume){
        int n = volume < RHO * mBestVol ? N1 : N2;
        return Math.pow(x, -n);
    }
    /**
     * Return a normalized array of length length of probabilties using the bias function (which uses the volume of the current container)
     */
    private double[] getProbability(int length, double volume) {
        double[] proba = new double[length];
        for (int n = 0; n < length; n++) {
            proba[n] = bias(n + 1, volume);
        }
        final double sum = Arrays.stream(proba).sum(); // sum(proba)
        proba = Arrays.stream(proba).map(new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double n) {
                return n / sum;
            }
        }).toArray(); // normalization of proba
        return proba;
    }

    /**
     * random return an index i of proba with a probabilty of proba[i]
     */
    private static int chooseRandomIndexInProba(double[] proba) {
        if (Math.abs(Arrays.stream(proba).sum() - 1) > 1e-10)
            throw new IllegalArgumentException("sum of all items of proba must be equal to 1.");

        double r = Math.random();
        double cumulation = 0;
        for (int i = 0; i < proba.length; i++) {
            cumulation += proba[i];
            if (r < cumulation){
                return i;
            }
        }
        throw new InternalError("this method does not have the expected behaviour ...");
    }

    private int chooseRandomIndex(int length, double volume) {
        return chooseRandomIndexInProba(getProbability(length, volume));
    }

    /**
     * return a loaded Container
     * at each step :
     *	1. generate all possible max blocks
     * 	2. the mTau blocks that maximize each criterion (whose index is contained in the table evakuationSet) are selected
     * 	3. a block is then selected with probability that is based on its rank and a bias function
     *  4. finally the number of boxes of the selected block is then reduced probabilistically along the three dimensions, using the same bias function
     * 	5. the block is added in the container and the spaces of the container are updated
     */
    public Container randomContruction(final int[] evaluationSet){
        Container container = new Container(this.getDimContainer(), this.getBoxes());
        ArrayList<Block> blocks;
        List<Object> bestBlocks;
        Block selectedBlock;
        while (!container.isFullyLoaded() && !container.isFull()) {
            final double volume = container.getFilledVolume();
            // 1. generate all possible max blocks
            blocks = container.findMaxBlocks();
            if (blocks.size() == 0){
                if (container.isEmpty()){
                    throw new AssertionError("The container is probably too small ...");
                }
                return container;
            }
            // 2. sorting blocks and selecting mTau best blocks
            bestBlocks = blocks.stream()
                    .sorted(new Comparator<Block>() {
                        @Override
                        public int compare(Block b1, Block b2) {
                            return b2.compareTo(b1, evaluationSet);
                        }
                    }) // !! reverse sorting : b2.compareTo(b1)
                    .limit(Math.min(TAU, blocks.size())) // select mTau best blocks
                    .collect(Collectors.toList());
            // 3. choose a block probabilistically
            int index = chooseRandomIndex(bestBlocks.size(), volume);
            selectedBlock = (Block) bestBlocks.get(index);
            // 4. reduction of the number of boxes of selectedBlock
            int[] reductedN = Arrays.stream(selectedBlock.getN())
                    .map(new IntUnaryOperator() {
                        @Override
                        public int applyAsInt(int rd) {
                            return rd - Loader.this.chooseRandomIndex(rd, volume);
                        }
                    })
                    .toArray();
            selectedBlock.setN(reductedN);
            // 5. add block in the container
            container.addBlock(selectedBlock);
        }
        return container;
    }

    public Container solve(){
        int[][] allEvalSets = {{0, 1, 4, 5}, {2, 3, 4, 5}, {0, 1, 3, 5}, {2, 1, 3, 5}};
        int nSets = allEvalSets.length;
        double[] proba = new double[nSets];
        for (int i = 0; i < nSets; i++){proba[i] = 1.0/nSets;}
        double[] evalCounter= new double[nSets];
        double[] volumePerEval = new double[nSets]; // avg volume of eval i is volumerPerEval[i]/evalCounter[i]
        double[] lambda = new double[nSets];
        Container bestContainer = null;

        for (int iter = 0; iter < ITER; iter ++){

            if (iter != 0 && iter % K == 0) { // reevaluation of the selection probabilities proba
                for (int i = 0; i < nSets; i++) {
                    lambda[i] = Math.pow(volumePerEval[i]/(Math.max(1, evalCounter[i])*mBestVol), PHI);
                }
                double sum = Arrays.stream(lambda).sum();
                for (int i = 0; i < nSets; i++) {
                    proba[i] = lambda[i] / sum;
                }
            }

            int evalIndex = chooseRandomIndexInProba(proba);
            int [] evalSet = allEvalSets[evalIndex];
            Container container = randomContruction(evalSet);
            volumePerEval[evalIndex] += container.getFilledVolume();
            evalCounter[evalIndex] += 1;

            if (bestContainer == null || container.compareTo(bestContainer) >= 1){
                bestContainer = container;
                mBestVol = container.getFilledVolume();
            }
        }
        return bestContainer;
    }

    public List<double[]> solveToArray(){
        Container container = solve();
        List<double[]> l = new ArrayList<>();
        for (double[] d : container.toArray()){
//            System.out.println("avant" + Arrays.toString(d));
            double [] pos = new double[]{d[0], d[1], d[2]};
            double [] dim = new double[]{d[3], d[4], d[5]};
//            System.out.println(dim[0]);
            // changement de repère (x,y,z) -> (y, z, x-X) (où X est la largeur du container
            double[] new_pos = new double[]{pos[1], pos[2], pos[0] - container.getDim(0)};
            double[] pointsAG = new double[]{
                    new_pos[0], //Ax
                    new_pos[1], //Ay
                    -(new_pos[2] + dim[0]), //Az
                    new_pos[0] + dim[1], //Gx
                    new_pos[1] + dim[2], //Gy
                    -new_pos[2] //Gz
            };
//            System.out.println("après" + Arrays.toString(pointsAG));
            l.add(pointsAG);
        }
        return l;
    }

    /* Public Method */

    /* toString & Main */
    @Override
    public String toString() {
        return "CLP" + mCargo;
    }


    public static void main(String [] args) {
        Box i1 = new Box(new double[]{10, 12, 9});
        Box i2 = new Box(24, 4.5, 20, new byte[]{1, 1, 1});
        Box i3 = new Box(10, 12, 7, new byte[]{1, 1, 1});
        Box i4 = new Box(15, 12, 7, new byte[]{1, 1, 1});

        HashMap<Box, Integer> boxes = new HashMap<Box, Integer>();
        boxes.put(i1, 4);
        boxes.put(i2, 3);
        boxes.put(i3, 12);

        Loader clp = new Loader(new double[]{42, 25, 25}, boxes);
        // int[] evaluationSet = new int[]{0, 1, 4, 5};
        // Container container = clp.randomContruction(evaluationSet);
        // System.out.println(container);


        /* TEST*/
		/*
		Container container = new Container(clp.mDimContainer, clp.mCargo);
		ArrayList<Block> blocks = container.findMaxBlocks();
		// System.out.println(blocks);

		List<Block> bestBlocks = blocks.stream()
			.sorted((b1, b2) -> b2.compareTo(b1, evaluationSet)) // !! reverse sorting : b2.compareTo(b1)
			.limit(Math.min(10, blocks.size())) // select mTau best blocks
			.collect(Collectors.toList());


		Block block = bestBlocks.get(0);
		// System.out.println(block);

		int[] newN = Arrays.stream(block.getN()).map(rd -> rd - clp.chooseRandomIndex(rd, 0)).toArray();
		// System.out.println(Arrays.toString(newN));
		block.setN(newN);
		// System.out.println(block);
		// System.out.println(container.hasSpace(block.getSpace()));


		container.addBlock(block);
		// System.out.println(container.hasSpace(block.getSpace()));
		System.out.println(block.getSpace());
		System.out.println(container);
		System.out.println(clp);*/

        /* TEST */
        clp.solve();



    }
}
