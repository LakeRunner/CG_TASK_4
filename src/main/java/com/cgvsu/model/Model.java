package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.objreader.ReaderExceptions;

import java.util.*;

public class Model {
    private List<Vector3f> vertices;
    private List<Vector2f> textureVertices;
    private List<Vector3f> normals;
    private List<Polygon> polygons;

    public Model(final List<Vector3f> vertices, final List<Vector2f> textureVertices,
                 final List<Vector3f> normals, final List<Polygon> polygons) {
        this.vertices = vertices;
        this.textureVertices = textureVertices;
        this.normals = normals;
        this.polygons = polygons;
    }

    public Model() {
        vertices = new ArrayList<>();
        textureVertices = new ArrayList<>();
        normals = new ArrayList<>();
        polygons = new ArrayList<>();
    }

    public Model(Model model) {
        if (model != null) {
            vertices = model.vertices;
            textureVertices = model.textureVertices;
            normals = model.normals;
            polygons = model.polygons;
        } else {
            vertices = new ArrayList<>();
            textureVertices = new ArrayList<>();
            normals = new ArrayList<>();
            polygons = new ArrayList<>();
        }
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Vector2f> getTextureVertices() {
        return textureVertices;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public void setVertices(final List<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public void setTextureVertices(final List<Vector2f> vertices) {
        this.textureVertices = vertices;
    }

    public void setNormals(final List<Vector3f> vertices) {
        this.normals = vertices;
    }

    public void setPolygons(final List<Polygon> vertices) {
        this.polygons = vertices;
    }

    public boolean checkConsistency() {
        for (int i = 0; i < polygons.size(); i++) {
            List<Integer> vertexIndices = polygons.get(i).getVertexIndices();
            List<Integer> textureVertexIndices = polygons.get(i).getTextureVertexIndices();
            List<Integer> normalIndices = polygons.get(i).getNormalIndices();
            if (vertexIndices.size() != textureVertexIndices.size()
                    && vertexIndices.size() != 0 && textureVertexIndices.size() != 0) {
                throw new ReaderExceptions.NotDefinedUniformFormatException(
                        "The unified format for specifying polygon descriptions is not defined.");
            }
            if (vertexIndices.size() != normalIndices.size()
                    && vertexIndices.size() != 0 &&  normalIndices.size() != 0) {
                throw new ReaderExceptions.NotDefinedUniformFormatException(
                        "The unified format for specifying polygon descriptions is not defined.");
            }
            if (normalIndices.size() != textureVertexIndices.size()
                    && normalIndices.size() != 0 && textureVertexIndices.size() != 0) {
                throw new ReaderExceptions.NotDefinedUniformFormatException(
                        "The unified format for specifying polygon descriptions is not defined.");
            }
            for (int j = 0; j < vertexIndices.size(); j++) {
                if (vertexIndices.get(j) >= vertices.size()) {
                    throw new ReaderExceptions.FaceException(
                            "Polygon description is wrong.", i + 1);
                }
            }
            for (int j = 0; j < textureVertexIndices.size(); j++) {
                if (textureVertexIndices.get(j) >= textureVertices.size()) {
                    throw new ReaderExceptions.FaceException(
                            "Polygon description is wrong.", i + 1);
                }
            }
            for (int j = 0; j < normalIndices.size(); j++) {
                if (normalIndices.get(j) >= normals.size()) {
                    throw new ReaderExceptions.FaceException(
                            "Polygon description is wrong.", i + 1);
                }
            }
        }
        return true;
    }



    public void triangulate() {
        List<Polygon> triangulatedPolygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            if (vertexIndices.size() > 3) {
                for (int i = 2; i < vertexIndices.size(); i++) {
                    Polygon triangle = new Polygon();
                    triangle.getVertexIndices().add(vertexIndices.get(0));
                    triangle.getVertexIndices().add(vertexIndices.get(i - 1));
                    triangle.getVertexIndices().add(vertexIndices.get(i));
                    triangle.getTextureVertexIndices().add(textureVertexIndices.get(0));
                    triangle.getTextureVertexIndices().add(textureVertexIndices.get(i - 1));
                    triangle.getTextureVertexIndices().add(textureVertexIndices.get(i));
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

