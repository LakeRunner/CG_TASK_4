package com.cgvsu.render_engine;

import com.cgvsu.math.*;

public class GraphicConveyor {

    public static Matrix4f rotate(Vector3f rotateV){
        double xAngle = Math.toRadians(rotateV.getX());
        double yAngle = Math.toRadians(rotateV.getY());
        double zAngle = Math.toRadians(rotateV.getZ());
        double[] firstLine = {Math.cos(yAngle) * Math.cos(zAngle), -Math.sin(zAngle) * Math.cos(yAngle), Math.sin(yAngle), 0};
        double[] secondLine = {Math.sin(xAngle) * Math.sin(yAngle) * Math.cos(zAngle) + Math.sin(zAngle) * Math.cos(xAngle), -Math.sin(xAngle) * Math.sin(yAngle) * Math.sin(zAngle) + Math.cos(xAngle) * Math.cos(zAngle), -Math.sin(xAngle) * Math.cos(yAngle), 0};
        double[] thirdLine = {Math.sin(xAngle) * Math.sin(zAngle) - Math.sin(yAngle) * Math.cos(xAngle) * Math.cos(zAngle), Math.sin(xAngle) * Math.cos(zAngle) + Math.sin(yAngle) * Math.sin(zAngle) * Math.cos(xAngle), Math.cos(xAngle) * Math.cos(yAngle), 0};
        double[] fourthLine = {0, 0, 0, 1};
        double[][] matrixA = {firstLine, secondLine, thirdLine, fourthLine};
        return new Matrix4f(matrixA);
    }

    public static Matrix4f scale(Vector3f scaleV){
        double xScale = scaleV.getX();
        double yScale = scaleV.getY();
        double zScale = scaleV.getZ();
        double[] firstLine = {xScale, 0, 0, 0};
        double[] secondLine = {0, yScale, 0, 0};
        double[] thirdLine = {0, 0, zScale, 0};
        double[] fourthLine = {0, 0, 0, 1};
        double[][] matrixA = {firstLine, secondLine, thirdLine, fourthLine};
        return new Matrix4f(matrixA);
    }

    public static Matrix4f translate(Vector3f translateV){
        double xTranslate = translateV.getX();
        double yTranslate = translateV.getY();
        double zTranslate = translateV.getZ();
        double[] firstLine = {1, 0, 0, 0};
        double[] secondLine = {0, 1, 0, 0};
        double[] thirdLine = {0, 0, 1, 0};
        double[] fourthLine = {xTranslate, yTranslate, zTranslate, 1};
        double[][] matrixA = {firstLine, secondLine, thirdLine, fourthLine};
        return new Matrix4f(matrixA);
    }

    public static Matrix4f rotateScaleTranslate(Vector3f rotateV, Vector3f scaleV, Vector3f translateV) {
        return Matrix4f.matrixMultiplier(Matrix4f.matrixMultiplier(rotate(rotateV), scale(scaleV)), translate(translateV));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultZ = Vector3f.subtraction(target, eye);
        Vector3f resultX = Vector3f.crossProduct(up, resultZ);
        Vector3f resultY = Vector3f.crossProduct(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        double[] matrix = new double[]{
                resultX.getX(), resultY.getX(), resultZ.getX(), 0,
                resultX.getY(), resultY.getY(), resultZ.getY(), 0,
                resultX.getZ(), resultY.getZ(), resultZ.getZ(), 0,
                -Vector3f.dotProduct(resultX, eye), -Vector3f.dotProduct(resultY, eye), -Vector3f.dotProduct(resultZ, eye), 1};
        return new Matrix4f(matrix, 4);
    }

    public static Matrix4f perspective(final float fov, final float aspectRatio,
                                       final float nearPlane, final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.getMatrixArray()[0][0] = tangentMinusOnDegree / aspectRatio;
        result.getMatrixArray()[1][1] = tangentMinusOnDegree;
        result.getMatrixArray()[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);
        result.getMatrixArray()[2][3] = 1.0F;
        result.getMatrixArray()[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector4f vertex) {
        final double x = (vertex.getX() * matrix.getMatrixArray()[0][0]) + (vertex.getY() * matrix.getMatrixArray()[1][0]) + (vertex.getZ() * matrix.getMatrixArray()[2][0]) + matrix.getMatrixArray()[3][0];
        final double y = (vertex.getX() * matrix.getMatrixArray()[0][1]) + (vertex.getY() * matrix.getMatrixArray()[1][1]) + (vertex.getZ() * matrix.getMatrixArray()[2][1]) + matrix.getMatrixArray()[3][1];
        final double z = (vertex.getX() * matrix.getMatrixArray()[0][2]) + (vertex.getY() * matrix.getMatrixArray()[1][2]) + (vertex.getZ() * matrix.getMatrixArray()[2][2]) + matrix.getMatrixArray()[3][2];
        final double w = (vertex.getX() * matrix.getMatrixArray()[0][3]) + (vertex.getY() * matrix.getMatrixArray()[1][3]) + (vertex.getZ() * matrix.getMatrixArray()[2][3]) + matrix.getMatrixArray()[3][3];
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.getX() * width + width / 2.0F, -vertex.getY() * height + height / 2.0F);
    }
}
