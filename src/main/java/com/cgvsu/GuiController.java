package com.cgvsu;

import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField rotateX;

    @FXML
    private TextField rotateY;

    @FXML
    private TextField rotateZ;

    @FXML
    private TextField scaleX;

    @FXML
    private TextField scaleY;

    @FXML
    private TextField scaleZ;

    @FXML
    private TextField translateX;

    @FXML
    private TextField translateY;

    @FXML
    private TextField translateZ;

    @FXML
    private Button triangulationButton;

    private Model mesh = null;

    private Vector3f rotateV = new Vector3f(0, 0,0);

    private Vector3f scaleV = new Vector3f(1, 1,1);

    private Vector3f translateV = new Vector3f(0, 0,0);

    private Camera camera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;
    private Robot robot;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));
            rotateV = new Vector3f(Double.parseDouble(rotateX.getText()), Double.parseDouble(rotateY.getText()), Double.parseDouble(rotateZ.getText()));
            scaleV = new Vector3f(Double.parseDouble(scaleX.getText()), Double.parseDouble(scaleY.getText()), Double.parseDouble(scaleZ.getText()));
            translateV = new Vector3f(-Double.parseDouble(translateX.getText()), Double.parseDouble(translateY.getText()), Double.parseDouble(translateZ.getText()));
            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, rotateV, scaleV, translateV);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            mesh.triangulate();
            mesh.calcNormals();
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    @FXML
    public void triangulation() {
        mesh.triangulate();
    }
}