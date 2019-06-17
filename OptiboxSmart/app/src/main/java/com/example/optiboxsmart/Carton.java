package com.example.optiboxsmart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Carton {

    private double l_x;
    private double l_y;
    private double l_z;
    private int type;
    private List<Integer> rgb;
    private boolean orientationLibre;


    /**
     *
     * @param l_x
     * @param l_y
     * @param l_z
     * @param type
     * @param orientationLibre
     */
    public Carton(double l_x, double l_y, double l_z, int type, boolean orientationLibre, List<Integer> rgb){
        this.l_x = l_x;
        this.l_y = l_y;
        this.l_z = l_z;
        this.type = type;
        this.orientationLibre = orientationLibre;
        this.rgb = rgb;
    }

    public double getL_x() {
        return l_x;
    }

    public double getL_y() {
        return l_y;
    }

    public double getL_z() {
        return l_z;
    }

    public int getType() {
        return type;
    }

    /**
     *
     * @return true si le carton peut être tourné selon les 3 directions X, Y, Z. false s'il ne
     * peut être tourné que selon l'axe y.
     */
    public boolean isOrientationLibre() {
        return orientationLibre;
    }

    /**
     *
     * @return true si le carton est rotaté de 90° autour de l'axe X avec succès (sens horaire)
     */
    public boolean rotationX(){
        if (isOrientationLibre()) {
            double prevL_y = getL_y();
            this.l_y = getL_z();
            this.l_z = prevL_y;
            return true;
        } else return false;
    }

    /**
     *
     * @return true si le carton est rotaté de 90° autour de l'axe Y avec succès (sens horaire)
     */
    public boolean rotationY(){
        double prevL_z = getL_z();
        this.l_z = getL_x();
        this.l_x = prevL_z;
        return true;
    }

    /**
     *
     * @return true si le carton est rotaté de 90° autour de l'axe Z avec succès (sens horaire)
     */
    public boolean rotationZ(){
        if (isOrientationLibre()){
            double prevL_y = getL_y();
            this.l_y = getL_x();
            this.l_x = prevL_y;
            return true;
        } else return false;
    }


    @Override
    public String toString() {
        return "Carton{" +
                "l_x=" + l_x +
                ", l_y=" + l_y +
                ", l_z=" + l_z +
                ", type=" + type +
                ", rgb=" + rgb +
                ", orientationLibre=" + orientationLibre +
                '}';
    }

    public static void main(String[] args) {
        List<Integer> rgb = new ArrayList<>(Arrays.asList(50,50,50));
        Carton carton = new Carton(1,2,3,0, true, rgb);
        System.out.println("carton: " + carton);
        carton.rotationX();
        System.out.println("carton: " + carton);
    }
}
