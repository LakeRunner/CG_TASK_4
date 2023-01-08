package com.cgvsu;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.LoadedModel;
import com.cgvsu.render_engine.Camera;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    String currentModelName;
    String currentCameraName;

    private Map<String, LoadedModel> loadedModels = new HashMap<>();
    private Map<String, Camera> addedCameras = new HashMap<>();

    private final Camera mainCamera = new Camera(
            new Vector3f(0, 0, 200),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);
    private Camera currentCamera = mainCamera;

    public String getCurrentModelName() {
        return currentModelName;
    }

    public void setCurrentModelName(String currentModelName) {
        this.currentModelName = currentModelName;
    }

    public Map<String, LoadedModel> getLoadedModels() {
        return loadedModels;
    }

    public void setLoadedModels(HashMap<String, LoadedModel> loadedModels) {
        this.loadedModels = loadedModels;
    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }

    public Map<String, Camera> getAddedCameras() {
        return addedCameras;
    }

    public String getCurrentCameraName() {
        return currentCameraName;
    }

    public void setCurrentCamera(Camera currentCamera) {
        this.currentCamera = currentCamera;
    }

    public Camera getMainCamera() {
        return mainCamera;
    }
}
