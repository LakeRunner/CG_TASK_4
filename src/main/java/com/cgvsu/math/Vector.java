package com.cgvsu.math;

public class Vector {

    private double[] coords;

    protected final float EQUAL_EPS = 1e-7f;

    protected Vector(){
    }

    protected Vector(int num){
        double[] coords =  new double[num];
        for(int i = 0; i<num; i++){
            coords[i] = 0;
        }
        this.coords = coords;
    }

    protected Vector(double a, double b) {
        coords = new double[2];
        this.coords[0] = a;
        this.coords[1] = b;
    }

    protected Vector(double a, double b, double c) {
        coords = new double[3];
        this.coords[0] = a;
        this.coords[1] = b;
        this.coords[2] = c;
    }

    protected Vector(double a, double b, double c, double d) {
        coords = new double[4];
        this.coords[0] = a;
        this.coords[1] = b;
        this.coords[2] = c;
        this.coords[3] = d;
    }

    protected double[] getCoords() {
        return coords;
    }

    public void setCoords(double[] coords) {
        this.coords = coords;
    }

    public void setCoords(Vector vector) {
        this.coords = vector.getCoords();
    }

    public double getX() {return coords[0]; }

    public double getY() {return coords[1]; }

    public double length(){
        double cur = 0;
        for (double coord : coords) {
            cur += coord * coord;
        }
        return Math.sqrt(cur);
    }

    public Vector normal() {
        int length = coords.length;
        new Vector(length);
        return switch (length) {
            case 2 -> new Vector(coords[0] / length(), coords[1] / length());
            case 3 -> new Vector(coords[0] / length(), coords[1] / length(), coords[2] / length());
            case 4 -> new Vector(coords[0] / length(), coords[1] / length(), coords[2] / length(), coords[3] / length());
            default -> new Vector(length);
        };
    }
}

