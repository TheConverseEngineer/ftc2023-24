package com.example.thundercore.math.matrix;

import androidx.annotation.NonNull;

import org.ejml.simple.SimpleMatrix;

/** Immutable wrapper for EJML's Matrix class with compile-time dimension check support.
 *
 * <p> Based off of wpilib's implementation
 */
@SuppressWarnings("unused")
public class Matrix <R extends Num, C extends Num> {
    private final SimpleMatrix matrix;

    /** Constructs a new empty matrix with the given dimensions */
    public Matrix(@NonNull Nat<R> rows, @NonNull Nat<C> columns) {
        this.matrix = new SimpleMatrix(rows.getNum(), columns.getNum());
    }

    /**
     * Constructs a new matrix with the given data. It is the user
     * responsibility to ensure that the inputted data matches the dimensions.
     *
     * @param rows      The number of rows of the matrix.
     * @param columns   The number of columns of the matrix.
     * @param storage   The data to populate the matrix with (in row major order)
     */
    public Matrix(@NonNull Nat<R> rows, @NonNull Nat<C> columns, double[] storage) {
        this.matrix = new SimpleMatrix(rows.getNum(), columns.getNum(), true, storage);
    }

    /** Alternate constructor that directly wraps a {@link SimpleMatrix}*/
    private Matrix(SimpleMatrix matrix) {
        this.matrix = matrix;
    }

    /** Returns the matrix entry at the given coordinates */
    public double get(int row, int column) {
        return matrix.get(row, column);
    }

    /** Returns the product of two matrices*/
    public <C2 extends Num> Matrix<R, C2> times(Matrix<C, C2> other) {
        return new Matrix<>(this.matrix.mult(other.matrix));
    }

    /** Returns the product of this matrix and a scalar */
    public Matrix<R, C> times(double scalar) {
        return new Matrix<>(this.matrix.scale(scalar));
    }

    /** Returns the sum of two matrices */
    public Matrix<R, C> plus(Matrix<R, C> other) {
        return new Matrix<>(this.matrix.plus(other.matrix));
    }

    /** Returns the difference of two matrices */
    public Matrix<R, C> minus(Matrix<R, C> other) {
        return new Matrix<>(this.matrix.minus(other.matrix));
    }

    /** Returns the transpose of this matrix */
    public Matrix<C, R> transpose() {
        return new Matrix<>(this.matrix.transpose());
    }

    /** Returns the inverse of this matrix
     *
     * @throws org.ejml.factory.SingularMatrixException   If no inverse exists
     */
    public Matrix<R, C> inverse() {
        return new Matrix<>(this.matrix.invert());
    }

    /** Checks to make sure all array elements are finite
     *
     * @throws IllegalArgumentException     If an infinite element exists
     * */
    public void assertFinite() {
        for (int row = 0; row < matrix.numRows(); row++) {
            for (int col = 0; col < matrix.numCols(); col++) {
                if (!Double.isFinite(matrix.get(row, col)))
                    throw new IllegalArgumentException("Array contained an infinite element");
            }
        }
    }
}
