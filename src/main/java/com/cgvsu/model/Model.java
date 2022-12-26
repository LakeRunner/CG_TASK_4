package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import java.util.*;

public class Model {

    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector2f> textureVertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Polygon> polygons = new ArrayList<>();

    public void triangulate() {
        List<Polygon> triangulatedPolygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() > 3) {
                for (int i = 2; i < vertexIndices.size(); i++) {
                    Polygon triangle = new Polygon();
                    triangle.getVertexIndices().add(vertexIndices.get(0));
                    triangle.getVertexIndices().add(vertexIndices.get(i - 1));
                    triangle.getVertexIndices().add(vertexIndices.get(i));
                    triangulatedPolygons.add(triangle);
                }
            } else {
                triangulatedPolygons.add(polygon);
            }
        }
        polygons = triangulatedPolygons;
    }
}
