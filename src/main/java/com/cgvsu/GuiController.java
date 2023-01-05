package com.cgvsu;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;
import com.cgvsu.model.CurrentModel;
import com.cgvsu.model.Polygon;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.awt.Button;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class GuiController {

    final private float TRANSLATION = 1.5F ;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane selection;

    @FXML
    private AnchorPane modelTrans;

    @FXML
    private AnchorPane renderingModels;

    @FXML
    private AnchorPane activeModels;

    @FXML
    private Canvas canvas;

    @FXML
    private ListView<String> listView;

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
    private Slider xSlider;

    @FXML
    private Slider ySlider;

    @FXML
    private Slider zSlider;

    @FXML
    private RadioMenuItem light;

    @FXML
    private RadioMenuItem dark;

    @FXML
    private CheckBox drawPolygonMesh;

    @FXML
    private CheckBox drawTextures;

    @FXML
    private CheckBox drawLighting;

    @FXML
    private ColorPicker polygonFillColor;

    private Timeline timeline;
    private Robot robot;
    private boolean modelIsSelected;
    private List<TextField> list;
    private List <String> selectedModels = new ArrayList<>();
    private boolean onActionParams;
    private boolean onActionList;
    private boolean onActionModes;
    private Model mesh = null;
    private Scene scene = new Scene();

    @FXML
    private void initialize() {
        double[][] zBuffer = new double[(int) canvas.getWidth()][(int) canvas.getHeight()];
        for (int i = 0; i < zBuffer.length; i++) {
            for (int j = 0; j < zBuffer[0].length; j++) {
                zBuffer[i][j] = Double.NEGATIVE_INFINITY;
            }
        }
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        scene.getLoadedModels().put("Mesh", new CurrentModel(mesh));
        scene.currentModel = "Mesh";
        list = getTextFields();
        List<Slider> sliders = Arrays.asList(xSlider, ySlider, zSlider);
        xSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String degrees = String.format(Locale.ENGLISH, "%(.2f", sliders.get(0).getValue());
                if (degrees.contains("(")) {
                    degrees = degrees.substring(1, degrees.length()-1);
                    degrees = "-" + degrees;
                }
                list.get(0).setText(degrees);
            }
        });
        ySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String degrees = String.format(Locale.ENGLISH, "%(.2f", sliders.get(1).getValue());
                if (degrees.contains("(")) {
                    degrees = degrees.substring(1, degrees.length()-1);
                    degrees = "-" + degrees;
                }
                list.get(1).setText(degrees);
            }
        });
        zSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                String degrees = String.format(Locale.ENGLISH, "%(.2f", sliders.get(2).getValue());
                if (degrees.contains("(")) {
                    degrees = degrees.substring(1, degrees.length()-1);
                    degrees = "-" + degrees;
                }
                list.get(2).setText(degrees);
            }
        });

        TranslateTransition transitionRightUpSide = new TranslateTransition();
        TranslateTransition transitionRightDownSide = new TranslateTransition();
        TranslateTransition transitionLeftSide = new TranslateTransition();
        transitionLeftSide.setNode(activeModels);
        transitionRightUpSide.setNode(modelTrans);
        transitionRightDownSide.setNode(renderingModels);
        transitionRightUpSide.setByX(270);
        transitionRightDownSide.setByX(270);
        transitionLeftSide.setByX(-230);
        transitionLeftSide.play();
        transitionRightUpSide.play();
        transitionRightDownSide.play();
        anchorPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
        public void handle(MouseEvent mouseEvent) {
            double rightBorder = activeModels.getLayoutX();
            double posRightBorder = activeModels.getLocalToParentTransform().getTx();
            double leftUpBorder = modelTrans.getLayoutX() + 260;
            double posLeftUpBorder = modelTrans.getLocalToParentTransform().getTx();
            double leftDownBorder = renderingModels.getLayoutX() + 260;
            double posLeftDownBorder = renderingModels.getLocalToParentTransform().getTx();
            if (posLeftUpBorder == modelTrans.getLayoutX() || posLeftUpBorder == modelTrans.getLayoutX() + 270) {
                if (modelIsSelected && !onActionParams && mouseEvent.getX() > leftUpBorder && mouseEvent.getX() < leftUpBorder + 7 && mouseEvent.getY() < 565) {
                    transitionRightUpSide.setByX(-270);
                    transitionRightUpSide.play();
                    onActionParams = true;
                    disable(false);
                } else if (onActionParams && (mouseEvent.getX() < leftUpBorder - 260 || mouseEvent.getY() > 565)) {
                    transitionRightUpSide.setByX(270);
                    transitionRightUpSide.play();
                    onActionParams = false;
                    disable(true);
                }
            }
            if (posLeftDownBorder == renderingModels.getLayoutX() || posLeftDownBorder == renderingModels.getLayoutX() + 270) {
                if (modelIsSelected && !onActionModes && mouseEvent.getX() > leftDownBorder && mouseEvent.getX() < leftDownBorder + 7 && mouseEvent.getY() < 720 && mouseEvent.getY() > 568) {
                    transitionRightDownSide.setByX(-270);
                    transitionRightDownSide.play();
                    onActionModes = true;
                } else if (onActionModes && (mouseEvent.getX() < leftDownBorder - 260 || mouseEvent.getY() < 568)) {
                    transitionRightDownSide.setByX(270);
                    transitionRightDownSide.play();
                    onActionModes = false;
                }
            }
            if (posRightBorder == activeModels.getLayoutX() || posRightBorder == activeModels.getLayoutX() - 230) {
                if (modelIsSelected && !onActionList && mouseEvent.getX() > rightBorder && mouseEvent.getX() < rightBorder + 5 && mouseEvent.getY() > 32 && mouseEvent.getY() < 330) {
                    transitionLeftSide.setByX(230);
                    transitionLeftSide.play();
                    onActionList = true;
                } else if (onActionList && (mouseEvent.getX() > rightBorder + 225 || mouseEvent.getY() > 330 || mouseEvent.getY() <= 32)) {
                    transitionLeftSide.setByX(-230);
                    transitionLeftSide.play();
                    onActionList = false;
                }
            }
        }
    });

        anchorPane.setOnScroll(new EventHandler<>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                double deltaY = scrollEvent.getDeltaY();
                if (deltaY < 0){
                    scene.getCamera().movePosition(new Vector3f(0, 0, TRANSLATION));
                } else {
                    scene.getCamera().movePosition(new Vector3f(0, 0, -TRANSLATION));
                }
            }
        });

        /*anchorPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double deltaX = mouseEvent.getX();
                double deltaY = mouseEvent.getY();
                double deltaZ = mouseEvent.getZ();
                camera.setTarget(new Vector3f(50, 50, deltaZ));
            }
        });*/

        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        modelTrans.setStyle("-fx-background-color: lightgray;");
        activeModels.setStyle("-fx-background-color: lightgray;");
        selection.setStyle("-fx-background-color: lightgray;");
        renderingModels.setStyle("-fx-background-color: lightgray;");
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            if (TransformFieldsNotNull() && scene.currentModel != null) {
                double width = canvas.getWidth();
                double height = canvas.getHeight();
                canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
                Color meshColor = dark.isSelected() ? Color.LIGHTGRAY : Color.BLACK;
                scene.getCamera().setAspectRatio((float) (width / height));
                scene.getLoadedModels().get(scene.currentModel).setRotateV(new Vector3f(Double.parseDouble(rotateX.getText()), Double.parseDouble(rotateY.getText()), Double.parseDouble(rotateZ.getText())));
                scene.getLoadedModels().get(scene.currentModel).setScaleV(new Vector3f(Double.parseDouble(scaleX.getText()), Double.parseDouble(scaleY.getText()), Double.parseDouble(scaleZ.getText())));
                scene.getLoadedModels().get(scene.currentModel).setTranslateV(new Vector3f(-Double.parseDouble(translateX.getText()), Double.parseDouble(translateY.getText()), Double.parseDouble(translateZ.getText())));
                if (scene.getLoadedModels().get(scene.currentModel) != null) {
                    RenderEngine.render(canvas.getGraphicsContext2D(), scene.getCamera(), scene.getLoadedModels().get(scene.currentModel), (int) width, (int) height,
                            scene.getLoadedModels().get(scene.currentModel).getRotateV(), scene.getLoadedModels().get(scene.currentModel).getScaleV(), scene.getLoadedModels().get(scene.currentModel).getTranslateV(), meshColor, drawPolygonMesh.isSelected(),
                            drawTextures.isSelected(), drawLighting.isSelected(), polygonFillColor.getValue(), zBuffer);
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenLoadModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path path = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(path);
            Model model = ObjReader.read(fileContent, true);
            model.triangulate();
            //model.calcNormals();
            scene.getLoadedModels().put(file.getName(), new CurrentModel(model));
            scene.currentModel = file.getName();
            listView.getItems().add(scene.currentModel);
            listView.scrollTo(scene.currentModel);
            cleanTransform();
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
        modelIsSelected = true;
    }
    public void onOpenSaveUnchangedModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());

        try {
            ArrayList<String> fileContent2 = ObjWriter.write(scene.getLoadedModels().get(scene.currentModel));
            FileWriter writer = new FileWriter(file);
            for (String s : fileContent2) {
                writer.write(s + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
        selection.setVisible(false);
    }

    public void onOpenSaveWithChangesModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        try {
            Model changedModel = new Model(scene.getLoadedModels().get(scene.currentModel).modifiedVertecies(), scene.getLoadedModels().get(scene.currentModel).getTextureVertices(), scene.getLoadedModels().get(scene.currentModel).getNormals(), scene.getLoadedModels().get(scene.currentModel).getPolygons());
            ArrayList<String> fileContent2 = ObjWriter.write(changedModel);
            FileWriter writer = new FileWriter(file);
            for (String s : fileContent2) {
                writer.write(s + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
        selection.setVisible(false);
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        scene.getCamera().movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        scene.getCamera().movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        scene.getCamera().movePosition(new Vector3f(TRANSLATION, 0, 0));
        //scene.getCamera().setTarget(new Vector3f(scene.getCamera().getTarget().getX() + TRANSLATION, scene.getCamera().getTarget().getY(), scene.getCamera().getTarget().getZ()));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        scene.getCamera().movePosition(new Vector3f(-TRANSLATION, 0, 0));
        //camera.setTarget(new Vector3f(camera.getTarget().getX() - TRANSLATION, camera.getTarget().getY(), camera.getTarget().getZ()));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {scene.getCamera().movePosition(new Vector3f(0, TRANSLATION, 0));}

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        scene.getCamera().movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
    @FXML
    public void triangulation() {
        mesh.triangulate();
    }

    public List<TextField> getTextFields () {
        List<TextField> list = new ArrayList<>();
        list.add(rotateX);
        list.add(rotateY);
        list.add(rotateZ);
        list.add(scaleX);
        list.add(scaleY);
        list.add(scaleZ);
        list.add(translateX);
        list.add(translateY);
        list.add(translateZ);
        return list;
    }
    public void disable (boolean disable) {
        for (TextField field : list) {
            field.setDisable(disable);
        }
        xSlider.setDisable(disable);
        ySlider.setDisable(disable);
        zSlider.setDisable(disable);
    }
    public boolean TransformFieldsNotNull() {
        String pattern = "^(-?\\d{1,3}(\\.\\d*))$";
        for (TextField field : list) {
            Matcher matcher = Pattern.compile(pattern).matcher(field.getText());
            if (!matcher.find()) {
                return false;
            }
        }
        return true;
    }
    public void cleanTransform() {
        list.get(0).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getRotateV().getX()));
        list.get(1).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getRotateV().getY()));
        list.get(2).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getRotateV().getZ()));
        list.get(3).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getScaleV().getX()));
        list.get(4).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getScaleV().getY()));
        list.get(5).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getScaleV().getZ()));
        list.get(6).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getTranslateV().getX()));
        list.get(7).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getTranslateV().getY()));
        list.get(8).setText(String.valueOf(scene.getLoadedModels().get(scene.currentModel).getTranslateV().getZ()));
        xSlider.setValue(scene.getLoadedModels().get(scene.currentModel).getRotateV().getX());
        ySlider.setValue(scene.getLoadedModels().get(scene.currentModel).getRotateV().getY());
        zSlider.setValue(scene.getLoadedModels().get(scene.currentModel).getRotateV().getZ());
    }
    public void modelSelected (MouseEvent event) throws IOException {
        if (Objects.equals(event.getButton().toString(), "PRIMARY")) {
            checkSelectedModels();
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                String name = listView.getItems().get(index);
                scene.currentModel = name;
                cleanTransform();
            }
            anchorPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.DELETE) {
                        for (String str : selectedModels) {
                            listView.getItems().remove(str);
                            scene.getLoadedModels().remove(str);
                            scene.currentModel = listView.getItems().get(listView.getItems().size()-1);
                        }
                    }
                }
            });
        }
    }
    public void checkSelectedModels () {
        selectedModels = new ArrayList<>();
        List<Integer> list = listView.getSelectionModel().getSelectedIndices();
        for (Integer i : list) {
            selectedModels.add(listView.getItems().get(i));
        }
    }
    public void darkTheme () {
        if (!dark.isSelected()) {
            dark.setSelected(true);
        }
        light.setSelected(false);
        anchorPane.setStyle("-fx-background-color: black;");
    }
    public void lightTheme () {
        dark.setSelected(false);
        anchorPane.setStyle("-fx-background-color: white;");
    }
    public void saveSelection () {
        if (modelIsSelected) {
            selection.setVisible(true);
        }
    }
    public void closeSaveSelection () {
        selection.setVisible(false);
    }

}