package com.cgvsu.math;

public class Matrix {
    private double[][] matrix;

    protected Matrix(double[][] matrix){
        this.matrix = matrix;
    }

    protected Matrix(double[] curMatrix, double num){
        double[][] matrix = new double[(int) num][(int) num];
        for (int i = 0; i<curMatrix.length; i++){
            int page = (int) Math.floor(i/num);
            assert false;
            matrix[page][(int) (i-page*num)] = curMatrix[i];
        }
        this.matrix=matrix;
    }

    protected Matrix() {

    }

    protected Matrix(int num) {
        this.matrix = getZeroMatrix(num).getMatrixArray();
    }

    protected Matrix(Matrix matrix){
        this.matrix = matrix.matrix;
    }

    public double[][] getMatrixArray() {
        return matrix;
    }

    public Matrix transpose(){
        double[][] m = new double[3][3];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                m[j][i] = matrix[i][j];
            }
        }
        return new Matrix(m);
    }

    public static Matrix getZeroMatrix(int size) {
        double[][] m = new double[size][size];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = 0;
            }
        }
        return new Matrix(m);
    }

    public static Matrix getOneMatrix(int size){
        double[][] m = new double[size][size];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if(i == j){
                    m[i][j] = 1;
                }else {
                    m[i][j] = 0;
                }
            }
        }
        return new Matrix(m);
    }
}
