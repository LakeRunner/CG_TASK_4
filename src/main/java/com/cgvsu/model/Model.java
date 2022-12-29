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

    public void calcNormals() {
        for (Polygon polygon : polygons) {
            Vector3f a = vertices.get(polygon.getVertexIndices().get(0));
            Vector3f b = vertices.get(polygon.getVertexIndices().get(1));
            Vector3f c = vertices.get(polygon.getVertexIndices().get(2));
            Vector3f ab = Vector3f.subtraction(b, a);
            Vector3f ac = Vector3f.subtraction(c, a);
            Vector3f normal = Vector3f.crossProduct(ab, ac);
            normal.normalize();
            normals.add(normal);
            polygon.getNormalIndices().add(normals.size() - 1);
            normals.add(normal);
            polygon.getNormalIndices().add(normals.size() - 1);
            normals.add(normal);
            polygon.getNormalIndices().add(normals.size() - 1);
        }
    }
}
