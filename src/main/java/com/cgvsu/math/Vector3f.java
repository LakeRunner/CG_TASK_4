package com.cgvsu.math;

public class Vector3f extends Vector{
    public Vector3f(double a, double b, double c){
        super(a, b, c);
    }

    public Vector3f(){
        super(3);
    }

    public double getZ() {return getCoords()[2];}

    public static Vector3f addition(Vector3f v1, Vector3f v2){
        return new Vector3f(v1.getCoords()[0] + v2.getCoords()[0], v1.getCoords()[1] + v2.getCoords()[1], v1.getCoords()[2] + v2.getCoords()[2]);
    }

    public static Vector3f subtraction(Vector3f v1, Vector3f v2){
        return new Vector3f(v1.getCoords()[0] - v2.getCoords()[0], v1.getCoords()[1] - v2.getCoords()[1], v1.getCoords()[2] - v2.getCoords()[2]);
    }

    public static Vector3f multiplier(Vector3f v, double n){
        return new Vector3f(v.getCoords()[0] * n, v.getCoords()[1] * n, v.getCoords()[2] * n);
    }

    public static Vector3f separation(Vector3f v, double n){
        return new Vector3f(v.getCoords()[0] / n, v.getCoords()[1] / n, v.getCoords()[2] / n);
    }

    public static double dotProduct(Vector3f v1, Vector3f v2){
        return v1.getCoords()[0] * v2.getCoords()[0] + v1.getCoords()[1] * v2.getCoords()[1] + v1.getCoords()[2] * v2.getCoords()[2];
    }

    public static Vector3f crossProduct(Vector v1, Vector v2){
        final double x  = v1.getCoords()[1] * v2.getCoords()[2] - v2.getCoords()[1] * v1.getCoords()[2];
        final double y  = -(v1.getCoords()[0] * v2.getCoords()[2] - v2.getCoords()[0] * v1.getCoords()[2]);
        final double z  = v1.getCoords()[0] * v2.getCoords()[1] - v2.getCoords()[0] * v1.getCoords()[1];
        return new Vector3f(x,y,z);
    }

    public boolean equals(Vector3f other) {
        return Math.abs(getX() - other.getX()) < EQUAL_EPS && Math.abs(getY() - other.getY()) < EQUAL_EPS && Math.abs(getZ() - other.getZ()) < EQUAL_EPS;
    }

    public void normalize() {
        Vector3f vector = new Vector3f(super.getX() / length(), super.getY() / length(), getZ() / length());
        super.setCoords(vector);
    }
}
