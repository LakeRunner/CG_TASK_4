package com.cgvsu;

import com.cgvsu.math.*;
import com.cgvsu.model.LodedModel;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.GraphicConveyor;
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
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;

public class GuiController {

    private float TRANSLATION = 1.5F ;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane saveSelection;

    @FXML
    private AnchorPane modelTrans;

    @FXML
    private AnchorPane renderingModels;

    @FXML
    private AnchorPane loadedModels;

    @FXML
    private AnchorPane loadedTextures;

    @FXML
    private Canvas canvas;

    @FXML
    private ListView<String> listViewModels;
    @FXML
    private ListView<String> listViewTextures;

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
    private Slider transSlider;

    @FXML
    private Slider fovSlider;

    @FXML
    private Slider aspectSlider;

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
    private CheckBox drawFilling;

    @FXML
    private ColorPicker polygonFillColor;

    @FXML
    private Slider rotateSpeed;

    private Timeline timeline;
    private Robot robot;
    private boolean modelIsSelected;
    private List<TextField> list;
    private List <String> selectedModels = new ArrayList<>();
    private List <String> selectedTextures = new ArrayList<>();
    private Map<String, Image> textures = new HashMap<>();
    private boolean onActionTransform;
    private boolean onActionListModels;
    private boolean onActionModes;
    private boolean onActionListTextures;
    private Model mesh = null;
    private Scene scene = new Scene();
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private double xAngle;
    private double yAngle;
    private double angleListenerValue = 0.05;
    Image img = null;

    @FXML
    private void initialize() {
        listViewModels.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        scene.getLoadedModels().put("Mesh", new LodedModel(mesh));
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


        transSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                TRANSLATION = (float) (transSlider.getValue()*4);
            }
        });
        fovSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                scene.getCamera().setFov((float) (fovSlider.getValue()*2));
            }
        });
        aspectSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                scene.getCamera().setAspectRatio((float) (aspectSlider.getValue()*20));
            }
        });

        rotateSpeed.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                angleListenerValue = rotateSpeed.getValue();
            }
        });
        TranslateTransition transitionRightUpSide = new TranslateTransition();
        TranslateTransition transitionRightDownSide = new TranslateTransition();
        TranslateTransition transitionLeftUpSide = new TranslateTransition();
        TranslateTransition transitionLeftDownSide = new TranslateTransition();
        transitionLeftUpSide.setNode(loadedModels);
        transitionRightUpSide.setNode(modelTrans);
        transitionRightDownSide.setNode(renderingModels);
        transitionLeftDownSide.setNode(loadedTextures);
        transitionRightUpSide.setByX(260);
        transitionRightDownSide.setByX(260);
        transitionLeftUpSide.setByX(-235);
        transitionLeftDownSide.setByX(-235);
        transitionLeftUpSide.play();
        transitionRightUpSide.play();
        transitionRightDownSide.play();
        transitionLeftDownSide.play();
        anchorPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double rightUpBorder = loadedModels.getLayoutX();
                double posRightUpBorder = loadedModels.getLocalToParentTransform().getTx();
                double rightDownBorder = loadedTextures.getLayoutX();
                double posRightDownBorder = loadedTextures.getLocalToParentTransform().getTx();
                double leftUpBorder = modelTrans.getLayoutX();
                double posLeftUpBorder = modelTrans.getLocalToParentTransform().getTx();
                double leftDownBorder = renderingModels.getLayoutX();
                double posLeftDownBorder = renderingModels.getLocalToParentTransform().getTx();
                if (posLeftUpBorder == modelTrans.getLayoutX() || posLeftUpBorder == modelTrans.getLayoutX() + 260) {
                    if (modelIsSelected && !onActionTransform && mouseEvent.getX() > leftUpBorder + 260 && mouseEvent.getX() < leftUpBorder + 285 && mouseEvent.getY() < 520) {
                        transitionRightUpSide.setByX(-260);
                        transitionRightUpSide.play();
                        onActionTransform = true;
                        disable(false);
                    } else if (onActionTransform && (mouseEvent.getX() < leftUpBorder || mouseEvent.getY() > 520)) {
                        transitionRightUpSide.setByX(260);
                        transitionRightUpSide.play();
                        onActionTransform = false;
                        disable(true);
                    }
                }
                if (posLeftDownBorder == renderingModels.getLayoutX() || posLeftDownBorder == renderingModels.getLayoutX() + 260) {
                    if (modelIsSelected && !onActionModes && mouseEvent.getX() > leftDownBorder + 260 && mouseEvent.getX() < leftDownBorder + 285 && mouseEvent.getY() > 525) {
                        transitionRightDownSide.setByX(-260);
                        transitionRightDownSide.play();
                        onActionModes = true;
                    } else if (onActionModes && (mouseEvent.getX() < leftDownBorder|| mouseEvent.getY() < 525)) {
                        transitionRightDownSide.setByX(260);
                        transitionRightDownSide.play();
                        onActionModes = false;
                    }
                }
                if (posRightUpBorder == loadedModels.getLayoutX() || posRightUpBorder == loadedModels.getLayoutX() - 235) {
                    if (modelIsSelected && !onActionListModels && mouseEvent.getX() > rightUpBorder && mouseEvent.getX() < rightUpBorder + 25 && mouseEvent.getY() > 30 && mouseEvent.getY() < 375) {
                        transitionLeftUpSide.setByX(235);
                        transitionLeftUpSide.play();
                        onActionListModels = true;
                    } else if (onActionListModels && (mouseEvent.getX() > rightUpBorder + 235 || mouseEvent.getY() > 375 || mouseEvent.getY() < 30)) {
                        transitionLeftUpSide.setByX(-235);
                        transitionLeftUpSide.play();
                        onActionListModels = false;
                    }
                }
                if (posRightDownBorder == loadedTextures.getLayoutX() || posRightDownBorder == loadedTextures.getLayoutX() - 235) {
                    if (modelIsSelected && !onActionListTextures && mouseEvent.getX() > rightDownBorder && mouseEvent.getX() < rightDownBorder + 25 && mouseEvent.getY() > 385) {
                        transitionLeftDownSide.setByX(235);
                        transitionLeftDownSide.play();
                        onActionListTextures = true;
                    } else if (onActionListTextures && (mouseEvent.getX() > rightDownBorder + 235 || mouseEvent.getY() < 385)) {
                        transitionLeftDownSide.setByX(-235);
                        transitionLeftDownSide.play();
                        onActionListTextures = false;
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

        anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                startX = mouseEvent.getX();
                startY = mouseEvent.getY();
            }
        });

        anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                endX = mouseEvent.getX();
                endY = mouseEvent.getY();
                // startX = anchorPane.getWidth() / 2;
                //startY = anchorPane.getHeight() / 2;
                //double x = ((endX*2)-(anchorPane.getWidth()-1))/(anchorPane.getWidth()-1);
                //double y = -(((endY*2)-(anchorPane.getHeight() - 1))/(anchorPane.getHeight() - 1));
                /*Vector3f first = new Vector3f(scene.getCamera().getPosition().getX() - scene.getCamera().getTarget().getX(), scene.getCamera().getPosition().getY() - scene.getCamera().getTarget().getY(), scene.getCamera().getPosition().getZ() - scene.getCamera().getTarget().getZ());
                Vector3f second = new Vector3f(scene.getCamera().getPosition().getX() - x, scene.getCamera().getPosition().getY() - y, scene.getCamera().getPosition().getZ());
                double cos = (Vector3f.dotProduct(first, second)/ (first.length() * second.length()));
                double sin = Math.sqrt(1-(cos * cos));
                текущий з домножаем на лук эт поворачиваем с помощью матрицы поворота с помощью обратного лук эт в мировое простаранство и з прибавляем к позиции*/
                //oldCameraTargetVis.normalize();
                //double angle = Math.toDegrees(Math.atan2(y - startY, x - startX));
                //double deltaX = (endX - startX)/anchorPane.getWidth();
                //double deltaY = (endY - startY)/anchorPane.getHeight();
                //xAngle = deltaX * 0.1;
                //yAngle = deltaY * 0.1;
                // Vector3f cameraTargetVis = GraphicConveyor.multiplierMatrixToVector(GraphicConveyor.rotate(new Vector3f(yAngle, xAngle, 0)), new Vector4f(curZ.getX(), curZ.getY(), curZ.getZ(), 1));
                //Vector3f cameraTargetCam = GraphicConveyor.multiplierMatrixToVector(scene.getCamera().getViewMatrix(), new Vector4f(cameraTargetVis.getX(), cameraTargetVis.getY(), cameraTargetVis.getZ(), 1));
                //Vector3f cameraTargetWindow = GraphicConveyor.multiplierMatrixToVector(scene.getCamera().getProjectionMatrix(), new Vector4f(cameraTargetVis.getX(), cameraTargetVis.getY(), cameraTargetVis.getZ(), 1));
                if (!onActionModes && !onActionListModels && !onActionTransform) {
                    Vector3f curZ = Vector3f.subtraction(scene.getCamera().getTarget(), scene.getCamera().getPosition());
                    Vector3f oldCameraTargetVis = GraphicConveyor.multiplierMatrixToVector(scene.getCamera().getViewMatrix(), new Vector4f(curZ.getX(), curZ.getY(), curZ.getZ(), 1));
                    if (endX > startX) {
                        yAngle = angleListenerValue;
                    } else if (endX < startX) {
                        yAngle = -angleListenerValue;
                    } else {
                        yAngle = 0;
                    }
                    if (endY > startY) {
                        xAngle = angleListenerValue;
                    } else if (endY < startY) {
                        xAngle = -angleListenerValue;
                    } else {
                        xAngle = 0;
                    }
                    Vector3f cameraTargetVis = GraphicConveyor.multiplierMatrixToVector(GraphicConveyor.rotate(new Vector3f(xAngle, yAngle, 0)), new Vector4f(oldCameraTargetVis.getX(), oldCameraTargetVis.getY(), oldCameraTargetVis.getZ(), 1));
                    Vector3f cameraTargetWorld = GraphicConveyor.multiplierMatrixToVector(scene.getCamera().getViewMatrix().inversion(), new Vector4f(cameraTargetVis.getX(), cameraTargetVis.getY(), cameraTargetVis.getZ(), 1));
                    scene.getCamera().setTarget(Vector3f.addition(cameraTargetWorld, scene.getCamera().getPosition()));
                    startY = endY;
                    startX = endX;
                }
            }
        });



        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        modelTrans.setStyle("-fx-background-color: lightgray;");
        loadedModels.setStyle("-fx-background-color: lightgray;");
        saveSelection.setStyle("-fx-background-color: lightgray;");
        renderingModels.setStyle("-fx-background-color: lightgray;");
        loadedTextures.setStyle("-fx-background-color: lightgray;");
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);


        try {
            img = new Image(new FileInputStream("src\\main\\resources\\textures\\NeutralWrapped.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Image finalImg = img;
        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            Color meshColor = dark.isSelected() ? Color.LIGHTGRAY : Color.BLACK;
            if (TransformFieldsNotNull() && scene.currentModel != null) {
                //scene.getCamera().setAspectRatio((float) (width / height));
                scene.getCamera().setAspectRatio((float) ((width / height) * aspectSlider.getValue()*2));
                scene.getLoadedModels().get(scene.currentModel).setRotateV(new Vector3f(Double.parseDouble(rotateX.getText()), Double.parseDouble(rotateY.getText()), Double.parseDouble(rotateZ.getText())));
                scene.getLoadedModels().get(scene.currentModel).setScaleV(new Vector3f(Double.parseDouble(scaleX.getText()), Double.parseDouble(scaleY.getText()), Double.parseDouble(scaleZ.getText())));
                scene.getLoadedModels().get(scene.currentModel).setTranslateV(new Vector3f(-Double.parseDouble(translateX.getText()), Double.parseDouble(translateY.getText()), Double.parseDouble(translateZ.getText())));
                for (String model : selectedModels) {
                    RenderEngine.render(canvas.getGraphicsContext2D(), scene.getCamera(),
                            scene.getLoadedModels().get(model), (int) width, (int) height,
                            scene.getLoadedModels().get(model).getRotateV(),
                            scene.getLoadedModels().get(model).getScaleV(),
                            scene.getLoadedModels().get(model).getTranslateV(),
                            meshColor, drawPolygonMesh.isSelected(), drawTextures.isSelected(),
                            drawLighting.isSelected(), drawFilling.isSelected(), polygonFillColor.getValue(),
                            finalImg);
                }
            }
        });

        /*KeyFrame frame2 = new KeyFrame(Duration.millis(1), event -> {
            if (!onActionModes && !onActionListModels && !onActionTransform) {
                Vector3f curZ = Vector3f.subtraction(scene.getCamera().getTarget(), scene.getCamera().getPosition());
                Vector3f oldCameraTargetVis = GraphicConveyor.multiplierMatrixToVector(scene.getCamera().getViewMatrix(), new Vector4f(curZ.getX(), curZ.getY(), curZ.getZ(), 1));
                if (endX > startX) {
                    yAngle = angleListenerValue;
                } else if (endX < startX) {
                    yAngle = -angleListenerValue;
                } else {
                    yAngle = 0;
                }
                if (endY > startY) {
                    xAngle = angleListenerValue;
                } else if (endY < startY) {
                    xAngle = -angleListenerValue;
                } else {
                    xAngle = 0;
                }
                Vector3f cameraTargetVis = GraphicConveyor.multiplierMatrixToVector(GraphicConveyor.rotate(new Vector3f(xAngle, yAngle, 0)), new Vector4f(oldCameraTargetVis.getX(), oldCameraTargetVis.getY(), oldCameraTargetVis.getZ(), 1));
                Vector3f cameraTargetWorld = GraphicConveyor.multiplierMatrixToVector(scene.getCamera().getViewMatrix().inversion(), new Vector4f(cameraTargetVis.getX(), cameraTargetVis.getY(), cameraTargetVis.getZ(), 1));
                scene.getCamera().setTarget(Vector3f.addition(cameraTargetWorld, scene.getCamera().getPosition()));
                startY = endY;
                startX = endX;
            }
        });*/

        timeline.getKeyFrames().add(frame);
       // timeline.getKeyFrames().add(frame2);
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
            String name = file.getName();
            name = checkContainsModel(name);
            scene.getLoadedModels().put(name, new LodedModel(model));
            scene.currentModel = name;
            listViewModels.getItems().add(scene.currentModel);
            listViewModels.scrollTo(scene.currentModel);
            selectedModels.add(scene.currentModel);
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
            List<String> fileContent2 = ObjWriter.write(scene.getLoadedModels().get(scene.currentModel));
            FileWriter writer = new FileWriter(file);
            for (String s : fileContent2) {
                writer.write(s + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
        saveSelection.setVisible(false);
    }

    public void onOpenSaveWithChangesModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        try {
            Model changedModel = new Model(scene.getLoadedModels().get(scene.currentModel).modifiedVertecies(), scene.getLoadedModels().get(scene.currentModel).getTextureVertices(), scene.getLoadedModels().get(scene.currentModel).getNormals(), scene.getLoadedModels().get(scene.currentModel).getPolygons());
            List<String> fileContent2 = ObjWriter.write(changedModel);
            FileWriter writer = new FileWriter(file);
            for (String s : fileContent2) {
                writer.write(s + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
        saveSelection.setVisible(false);
    }
    public void onOpenLoadTextureMenuItemClick () {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texture (*.jpg)", "*.jpg"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }
        String name = file.getName();
        name = checkContainsTexture(name);
        textures.put(name, new Image(new File(file.getAbsolutePath()).toURI().toString()));
        listViewTextures.getItems().add(name);
    }

    public void ctrlC(KeyEvent event) {
        if (event.isControlDown() && (event.getCode() == KeyCode.C)) {
            fovSlider.setValue(fovSlider.getValue()+0.1);
        }
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
    public String checkContainsModel (String str) {
        int count = 0;
        for (String name : scene.getLoadedModels().keySet()) {
            if (name.contains(str)) {
                count++;
            }
        }
        if (count > 0) {
            str += "(" + count + ")";
        }
        return str;
    }
    public String checkContainsTexture (String str) {
        int count = 0;
        for (String name : textures.keySet()) {
            if (name.contains(str)) {
                count++;
            }
        }
        if (count > 0) {
            str += "(" + count + ")";
        }
        return str;
    }
    public void modelSelected (MouseEvent event) throws IOException {
        if (Objects.equals(event.getButton().toString(), "PRIMARY")) {
            checkSelectedModels();
             int index = listViewModels.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                String name = listViewModels.getItems().get(index);
                scene.currentModel = name;
                cleanTransform();
            }
            anchorPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.DELETE) {
                        for (String str : selectedModels) {
                            listViewModels.getItems().remove(str);
                            scene.getLoadedModels().remove(str);
                            boolean flag = listViewModels.getItems().size() > 0;
                            scene.currentModel = flag ? listViewModels.getItems().get(listViewModels.getItems().size()-1) : null;
                        }
                    }
                }
            });
            anchorPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ENTER && selectedModels.size() > 1) {
                        Model changedModel = new Model();
                        String changeModelName = "";
                        for (String model : selectedModels) {
                            changedModel.getVertices().addAll(scene.getLoadedModels().get(model).modifiedVertecies());
                            changedModel.getTextureVertices().addAll(scene.getLoadedModels().get(model).getTextureVertices());
                            changedModel.getNormals().addAll(scene.getLoadedModels().get(model).getNormals());
                            changedModel.getPolygons().addAll(scene.getLoadedModels().get(model).getPolygons());
                            changeModelName += model.substring(0, 3);
                        }
                        changeModelName += ".obj";
                        List<String> data = ObjWriter.write(changedModel);
                        StringBuilder fileContent = new StringBuilder();
                        for (String s : data) {
                            fileContent.append(s).append("\n");
                        }
                        Model model = ObjReader.read(fileContent.toString(), true);
                        //model.calcNormals();
                        changeModelName = checkContainsModel(changeModelName);
                        scene.getLoadedModels().put(changeModelName, new LodedModel(model));
                        scene.currentModel = changeModelName;
                        listViewModels.getItems().add(scene.currentModel);
                        listViewModels.scrollTo(scene.currentModel);
                        cleanTransform();
                    }
                }
            });
        }
    }
    public void checkSelectedModels () {
        selectedModels = new ArrayList<>();
        List<Integer> list = listViewModels.getSelectionModel().getSelectedIndices();
        for (Integer i : list) {
            selectedModels.add(listViewModels.getItems().get(i));
        }
    }
    public void textureSelected (MouseEvent event) throws IOException {
        if (Objects.equals(event.getButton().toString(), "PRIMARY")) {
            checkSelectedTextures();
            int index = listViewTextures.getSelectionModel().getSelectedIndex();
            Image image = textures.get(listViewTextures.getItems().get(index));

            anchorPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.DELETE) {
                        for (String str : selectedTextures) {
                            listViewTextures.getItems().remove(str);
                            textures.remove(str);
                        }
                    }
                }
            });
        }
    }
    public void checkSelectedTextures () {
        selectedTextures = new ArrayList<>();
        List<Integer> list = listViewTextures.getSelectionModel().getSelectedIndices();
        for (Integer i : list) {
            selectedTextures.add(listViewTextures.getItems().get(i));
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
    public void openSaveSelection() {
        if (modelIsSelected) {
            saveSelection.setVisible(true);
        }
    }
    public void closeSaveSelection () {
        saveSelection.setVisible(false);
    }
    public static void exception(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Reader Exception");
        alert.setHeaderText(text);
        alert.setContentText("Please fix the input file.");
        alert.showAndWait();
    }

}