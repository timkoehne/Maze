package com.example.maze;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;

import java.io.File;
import java.io.IOException;

public class MainLayoutController {
    @FXML
    public Canvas mainCanvas;
    @FXML
    public AnchorPane leftPane;
    @FXML
    public ChoiceBox<String> choicebox;
    @FXML
    public CheckBox showSolution;
    @FXML
    public Spinner<Integer> generatingDelayInput;
    @FXML
    public Spinner<Integer> solvingDelayInput;
    @FXML
    public Spinner<Integer> mazeWidthInput;
    @FXML
    public Spinner<Integer> mazeHeightInput;

    public ApplicationHandler applicationHandler;

    public void toggleSolution(ActionEvent actionEvent) {
        if (applicationHandler.mazeGenerator != null) {
            applicationHandler.mazeGenerator.showSolution.set(showSolution.isSelected());
            applicationHandler.mazeGenerator.redrawMaze();
        }
    }

    public void generateNewMaze(ActionEvent actionEvent) {
        if (applicationHandler != null)
            applicationHandler.generateNewMaze();
    }

    public void saveAsFile(ActionEvent actionEvent) throws IOException {
        FileChooser savefile = new FileChooser();
        savefile.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        savefile.setInitialFileName("maze.png");
        savefile.setTitle("Save File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files", "*.png");
        savefile.getExtensionFilters().add(extFilter);
        File file = savefile.showSaveDialog(applicationHandler.stage);
        if(file != null){
            WritableImage image = new WritableImage((int) applicationHandler.canvas.getWidth(), (int) applicationHandler.canvas.getHeight());
            applicationHandler.canvas.snapshot(null, image);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(renderedImage, "png", file);
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        if (applicationHandler != null && applicationHandler.mazeGenerator.mazeRef.get().finishedGenerating)
            applicationHandler.solveMaze();
    }
}
