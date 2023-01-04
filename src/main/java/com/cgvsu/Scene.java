package com.cgvsu;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.CurrentModel;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;

import java.util.HashMap;

public class Scene {
    String currentModel;

    private HashMap<String, CurrentModel> loadedModels = new HashMap<>();

    private Camera camera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    public String getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(String currentModel) {
        this.currentModel = currentModel;
    }

    public HashMap<String, CurrentModel> getLoadedModels() {
        return loadedModels;
    }

    public void setLoadedModels(HashMap<String, CurrentModel> loadedModels) {
        this.loadedModels = loadedModels;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
