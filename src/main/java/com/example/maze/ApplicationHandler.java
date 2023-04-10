package com.example.maze;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class ApplicationHandler extends Application {

    GraphicsContext g;
    AnchorPane leftPane;
    AtomicReference<Float> squareSize;
    ChoiceBox<String> choiceBox;
    CheckBox showSolution;
    Spinner<Integer> generatingDelayInput;
    Spinner<Integer> solvingDelayInput;
    Spinner<Integer> mazeWidthInput;
    Spinner<Integer> mazeHeightInput;
    Canvas canvas;
    MazeGenerator mazeGenerator;

    Stage stage;


    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader();

        Parent page = loader.load(getClass().getResource("mainLayout.fxml").openStream());
        Scene scene = new Scene(page);
        MainLayoutController mainLayoutController = loader.getController();
        mainLayoutController.applicationHandler = this;

        this.canvas = mainLayoutController.mainCanvas;
        leftPane = mainLayoutController.leftPane;

        this.choiceBox = mainLayoutController.choicebox;
        this.choiceBox.setItems(FXCollections.observableArrayList(Maze.availableAlgorithms));
        this.choiceBox.getSelectionModel().select(0);

        this.showSolution = mainLayoutController.showSolution;

        this.generatingDelayInput = mainLayoutController.generatingDelayInput;
        this.solvingDelayInput = mainLayoutController.solvingDelayInput;
        this.mazeWidthInput = mainLayoutController.mazeWidthInput;
        this.mazeHeightInput = mainLayoutController.mazeHeightInput;

        this.g = canvas.getGraphicsContext2D();

        stage.setTitle("Maze!");
        stage.setScene(scene);
        stage.show();

        canvas.setWidth(leftPane.getWidth() - 5);
        canvas.setHeight(leftPane.getHeight() - 5);
    }

    public static void main(String[] args) {
        launch();
    }

    public void generateNewMaze() {
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());


        if (mazeWidthInput.getValue() % 2 == 0)
            mazeWidthInput.getValueFactory().setValue(mazeWidthInput.getValue()+1);
        if (mazeHeightInput.getValue() % 2 == 0)
            mazeHeightInput.getValueFactory().setValue(mazeHeightInput.getValue()+1);

        this.squareSize = new AtomicReference<>((float) Math.floor(Math.min(leftPane.getWidth() / mazeWidthInput.getValue(), leftPane.getHeight() / mazeHeightInput.getValue())));
        canvas.setWidth(this.squareSize.get()*mazeWidthInput.getValue());
        canvas.setHeight(this.squareSize.get()*mazeHeightInput.getValue());

        if (this.mazeGenerator != null)
            this.mazeGenerator.stopThreads.set(true);
        this.mazeGenerator = new MazeGenerator(showSolution.isSelected());
        this.mazeGenerator.generate(this.g, this.choiceBox, this.squareSize, mazeWidthInput.getValue(), mazeHeightInput.getValue(), generatingDelayInput, solvingDelayInput);
    }

    public void solveMaze() {
        try {
            this.mazeGenerator.solveMaze();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}