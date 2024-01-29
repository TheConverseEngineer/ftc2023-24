package org.firstinspires.ftc.teamcode.common.simulation;

import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;

/** A simple representation of an immutable matrix in java */
public class Matrix {
    double[][] matrix;

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double get(int row, int column) {
        return matrix[row][column];
    }

    public int getDim1() { return matrix.length; }
    public int getDim2() { return matrix[0].length; }

    public Matrix times(double[][] other) {
        if (other.length != matrix[0].length)
            throw new UnsupportedOperationException("Matrices of size " + matrix.length + "x"
                    + matrix[0].length + " and " + other.length + "x" + other[0].length + " cannot be multiplied.");

        double[][] product = new double[matrix.length][other[0].length];
        for (int i = 0; i < product.length; i++) {
            for (int j = 0; j < product[0].length; j++) {
                product[i][j] = 0;
                for (int k = 0; k < other.length; k++) {
                    product[i][j] += matrix[i][k] * other[k][j];
                }
            }
        }

        return new Matrix(product);
    }

    public Matrix plus(Matrix other) {
        if (other.getDim2() != getDim2() || other.getDim1() != getDim1())
            throw new UnsupportedOperationException("Matrices of size " + matrix.length + "x"
                    + matrix[0].length + " and " + other.getDim1() + "x" + other.getDim2() + " cannot be added.");

        double[][] sum = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < sum.length; i++) {
            for (int j = 0; j < sum[0].length; j++) {
                sum[i][j] = get(i, j) + other.get(i, j);
            }
        }

        return new Matrix(sum);
    }

    public Matrix times(double other) {
        double[][] product = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                product[i][j] = matrix[i][j] * other;
            }
        }

        return new Matrix(product);
    }

    public static Matrix toGlobal(double time, Matrix vel, Matrix state) {
        double dTheta = vel.get(2, 0) * time;
        double dX = vel.get(0, 0) * time;
        double dY = vel.get(1, 0) * time;

        double thetaG = state.get(2, 0);


        return new Matrix(new double[][]{
                {Math.cos(thetaG), -Math.sin(thetaG), 0},
                {Math.sin(thetaG), Math.cos(thetaG), 0},
                {0, 0, 1}
        }).times(new double[][]{{dX}, {dY}, {dTheta}});

    }
}
