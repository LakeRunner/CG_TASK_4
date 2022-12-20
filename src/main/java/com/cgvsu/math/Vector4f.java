package com.cgvsu.math;

public class Vector4f extends Vector {

    public Vector4f(double a, double b, double c, double d) {
        super(a, b, c, d);
    }

    public Vector4f(){
        super(4);
    }

    public double getZ() {
        return getCoords()[2];
    }

    public double getW() {
        return getCoords()[3];
    }

    public static Vector4f addition(Vector4f v1, Vector4f v2) {
        return new Vector4f(v1.getCoords()[0] + v2.getCoords()[0], v1.getCoords()[1] + v2.getCoords()[1], v1.getCoords()[2] + v2.getCoords()[2], v1.getCoords()[3] + v2.getCoords()[3]);
    }

    public static Vector4f subtraction(Vector4f v1, Vector4f v2) {
        return new Vector4f(v1.getCoords()[0] - v2.getCoords()[0], v1.getCoords()[1] - v2.getCoords()[1], v1.getCoords()[2] - v2.getCoords()[2], v1.getCoords()[3] - v2.getCoords()[3]);
    }

    public static Vector4f multiplier(Vector4f v, double n) {
        return new Vector4f(v.getCoords()[0] * n, v.getCoords()[1] * n, v.getCoords()[2] * n, v.getCoords()[3] * n);
    }

    public static Vector4f separation(Vector4f v, double n) {
        return new Vector4f(v.getCoords()[0] / n, v.getCoords()[1] / n, v.getCoords()[2] / n, v.getCoords()[3] / n);
    }

    public static double dotProduct(Vector4f v1, Vector4f v2) {
        return v1.getCoords()[0] * v2.getCoords()[0] + v1.getCoords()[1] * v2.getCoords()[1] + v1.getCoords()[2] * v2.getCoords()[2] + v1.getCoords()[3] * v2.getCoords()[3];
    }

    public void normalize() {
        Vector4f vector = new Vector4f(super.getX() / length(), super.getY() / length(), getZ() / length(), getW() / length());
        super.setCoords(vector);
    }
}
