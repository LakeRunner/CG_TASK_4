package com.cgvsu.model;

import com.cgvsu.math.Vector3f;

public class CurrentModel extends Model{
    private Vector3f rotateV;
    private Vector3f scaleV;
    private Vector3f translateV;

    public CurrentModel(Model model){
        super(model);
        rotateV = new Vector3f(0, 0, 0);
        scaleV = new Vector3f(1, 1, 1);
        translateV = new Vector3f(0, 0, 0);
    }

    public CurrentModel(CurrentModel model){
        super(model);
        rotateV = model.getRotateV();
        scaleV = model.getScaleV();
        translateV = model.getTranslateV();
    }

    public CurrentModel(Model model, Vector3f rotateV, Vector3f scaleV, Vector3f translateV){
        super(model);
        rotateV = this.rotateV;
        scaleV = this.scaleV;
        translateV = this.translateV;
    }

    public Model toMesh(){
        return new Model(super.getVertices(), super.getTextureVertices(), super.getNormals(), super.getPolygons());
    }

    public Vector3f getRotateV() {
        return rotateV;
    }

    public void setRotateV(Vector3f rotateV) {
        this.rotateV = rotateV;
    }

    public Vector3f getScaleV() {
        return scaleV;
    }

    public void setScaleV(Vector3f scaleV) {
        this.scaleV = scaleV;
    }

    public Vector3f getTranslateV() {
        return translateV;
    }

    public void setTranslateV(Vector3f translateV) {
        this.translateV = translateV;
    }
}
