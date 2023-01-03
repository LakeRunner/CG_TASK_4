package com.cgvsu.render_engine;

import java.io.IOException;
import java.util.ArrayList;

import com.cgvsu.GuiController;
import com.cgvsu.math.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(final GraphicsContext graphicsContext, final Camera camera, final Model mesh,
                              final int width, final int height, final Vector3f rotateV, final Vector3f scaleV,
                              final Vector3f translateV, Color color) {

        Matrix4f modelMatrix = rotateScaleTranslate(rotateV, scaleV, translateV);
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(projectionMatrix);
        modelViewProjectionMatrix =  Matrix4f.matrixMultiplier(modelViewProjectionMatrix, viewMatrix);
        modelViewProjectionMatrix =  Matrix4f.matrixMultiplier(modelViewProjectionMatrix, modelMatrix);
        final int nPolygons = mesh.getPolygons().size();
        graphicsContext.setStroke(color);
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.getPolygons().get(polygonInd).getVertexIndices().size();

            ArrayList<Vector2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.getVertices().get(mesh.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                Vector4f vertexVecmath = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1);

                Vector2f resultPoint = vertexToPoint(multiplierMatrixToVector(modelViewProjectionMatrix, vertexVecmath), width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).getX(),
                        resultPoints.get(vertexInPolygonInd - 1).getY(),
                        resultPoints.get(vertexInPolygonInd).getX(),
                        resultPoints.get(vertexInPolygonInd).getY());
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).getX(),
                        resultPoints.get(nVerticesInPolygon - 1).getY(),
                        resultPoints.get(0).getX(),
                        resultPoints.get(0).getY());
        }
    }
}