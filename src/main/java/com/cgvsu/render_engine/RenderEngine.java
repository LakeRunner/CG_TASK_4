package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.*;
import com.cgvsu.model.CurrentModel;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(final GraphicsContext graphicsContext, final Camera camera, final CurrentModel model,
                              final int width, final int height, final Vector3f rotateV, final Vector3f scaleV,
                              final Vector3f translateV, final Color meshColor, final boolean drawPolygonMesh,
                              final boolean drawTextures, final boolean drawLighting, final Color polygonFillColor) {

        Matrix4f modelMatrix = rotateScaleTranslate(rotateV, scaleV, translateV);
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(projectionMatrix);
        modelViewProjectionMatrix =  Matrix4f.matrixMultiplier(modelViewProjectionMatrix, viewMatrix);
        modelViewProjectionMatrix =  Matrix4f.matrixMultiplier(modelViewProjectionMatrix, modelMatrix);
        final int nPolygons = model.getPolygons().size();

        for (int polygonInd = 0; polygonInd < nPolygons; polygonInd++) {
            Polygon polygon = model.getPolygons().get(polygonInd);
            List<Integer> vertexIndices = polygon.getVertexIndices();

            List<Vector2f> resultPoints = new ArrayList<>();
            for (Integer vertexIndex : vertexIndices) {
                Vector3f vertex = model.getVertices().get(vertexIndex);

                Vector4f vertexVecmath = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1);

                Vector2f resultPoint = vertexToPoint(multiplierMatrixToVector(modelViewProjectionMatrix, vertexVecmath), width, height);
                resultPoints.add(resultPoint);
            }

            if (!drawTextures) {
                graphicsContext.setStroke(polygonFillColor);
                fillPolygon(resultPoints, graphicsContext);
            }

            if (drawPolygonMesh) {
                graphicsContext.setStroke(meshColor);
                drawPolygon(resultPoints, graphicsContext);
            }
        }
    }

    private static void fillPolygon(final List<Vector2f> resultPoints, final GraphicsContext graphicsContext) {
        for (int i = 1; i < resultPoints.size(); i++) {
            Vector2f v1 = resultPoints.get(i);
            for (int j = i; j > 0; j--) {
                Vector2f v2 = resultPoints.get(j - 1);
                if (v1.getY() < v2.getY()) {
                    resultPoints.set(j, v2);
                    resultPoints.set(j - 1, v1);
                }
            }
        }
        final Vector2f p1 = resultPoints.get(0);
        final Vector2f p2 = resultPoints.get(1);
        final Vector2f p3 = resultPoints.get(2);
        final double dx12;
        final double dx23;
        final double dx13;
        if (Math.abs(p1.getY() - p2.getY()) > 1e-17) {
            dx12 = (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
        } else {
            dx12 = 0;
        }
        if (Math.abs(p2.getY() - p3.getY()) > 1e-17) {
            dx23 = (p3.getX() - p2.getX()) / (p3.getY() - p2.getY());
        } else {
            dx23 = 0;
        }
        if (Math.abs(p1.getY() - p3.getY()) > 1e-17) {
            dx13 = (p3.getX() - p1.getX()) / (p3.getY() - p1.getY());
        } else {
            dx13 = 0;
        }
        double leftDx;
        double rightDx;
        if (dx12 < dx13) {
            leftDx = dx12;
            rightDx = dx13;
        } else {
            leftDx = dx13;
            rightDx = dx12;
        }
        double leftX = p1.getX();
        double rightX = leftX;
        for (double i = p1.getY(); i < p2.getY(); i++) {
            for (double j = leftX; j < rightX; j++) {
                graphicsContext.strokeLine(j, i, j, i);
            }
            leftX += leftDx;
            rightX += rightDx;
        }
        if (dx23 > dx13) {
            leftDx = dx23;
            rightDx = dx13;
        } else {
            leftDx = dx13;
            rightDx = dx23;
        }
        leftX = p3.getX();
        rightX = leftX;
        for (double i = p3.getY(); i > p2.getY(); i--) {
            for (double j = leftX; j < rightX; j++) {
                graphicsContext.strokeLine(j, i, j, i);
            }
            leftX -= leftDx;
            rightX -= rightDx;
        }
    }

    private static void drawPolygon(final List<Vector2f> resultPoints, final GraphicsContext graphicsContext) {
        for (int i = 0; i < resultPoints.size(); i++) {
            final int j = (i + 1) % 3;
            graphicsContext.strokeLine(
                    resultPoints.get(i).getX(),
                    resultPoints.get(i).getY(),
                    resultPoints.get(j).getX(),
                    resultPoints.get(j).getY());
        }
    }
}