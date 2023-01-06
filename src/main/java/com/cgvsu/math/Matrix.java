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

    public static Matrix transpose(double[][] matrix, int n){
        double[][] m = new double[n][n];
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

    public static Matrix inversion(double[][] matrixArray, int n) {
        double temp;
        float[][] E = new float[n][n];


        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                E[i][j] = 0f;

                if (i == j)
                    E[i][j] = 1f;
            }

        for (int k = 0; k < n; k++) {
            temp = matrixArray[k][k];

            for (int j = 0; j < n; j++) {
                matrixArray[k][j] /= temp;
                E[k][j] /= temp;
            }

            for (int i = k + 1; i < n; i++) {
                temp = matrixArray[i][k];

                for (int j = 0; j < n; j++) {
                    matrixArray[i][j] -= matrixArray[k][j] * temp;
                    E[i][j] -= E[k][j] * temp;
                }
            }
        }

        for (int k = n - 1; k > 0; k--) {
            for (int i = k - 1; i >= 0; i--) {
                temp = matrixArray[i][k];

                for (int j = 0; j < n; j++) {
                    matrixArray[i][j] -= matrixArray[k][j] * temp;
                    E[i][j] -= E[k][j] * temp;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrixArray[i][j] = E[i][j];
            }
        }
        return new Matrix4f(matrixArray);
    }
}
