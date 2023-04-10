package com.example.maze;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

class MazeGenerator {

    ChoiceBox<String> algoSelector;
    AtomicBoolean showSolution = new AtomicBoolean(true);
    AtomicReference<Maze> mazeRef = new AtomicReference<Maze>();
    GraphicsContext g;
    Thread mazeSolverThread;
    Thread mazeGeneratorThread;
    AtomicReference<SearchAlgorithm> solutionRef;

    AtomicBoolean stopThreads = new AtomicBoolean(false);
    Timeline drawSolution;
    Spinner<Integer> generatingDelayInput;
    Spinner<Integer> solvingDelayInput;

    public MazeGenerator(boolean showSolution) {
        this.showSolution.set(showSolution);
    }

    public void generate(GraphicsContext g, ChoiceBox<String> algoSelector, AtomicReference<Float> squareSize,
                         int mazeWidth, int mazeHeight, Spinner<Integer> generatingDelayInput, Spinner<Integer> solvingDelayInput) {
        this.g = g;
        this.algoSelector = algoSelector;
        this.generatingDelayInput = generatingDelayInput;
        this.solvingDelayInput = solvingDelayInput;

        solutionRef = new AtomicReference<SearchAlgorithm>();

        mazeGeneratorThread = new Thread(new MazeGeneratorRunnable(squareSize, showSolution, mazeWidth, mazeHeight, generatingDelayInput, mazeRef, this.stopThreads));
        mazeGeneratorThread.start();

        while (mazeRef.get() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Timeline drawMaze = new Timeline();
        drawSolution = new Timeline();

        drawMaze.getKeyFrames().add(
                new KeyFrame(Duration.millis(50),
                        event -> {
                            mazeRef.get().drawMaze(g);
                            if (mazeRef.get().finishedGenerating || stopThreads.get()) {
                                System.out.println("done");
                                drawMaze.stop();
                                mazeRef.get().drawMaze(g);
                            }
                        }));
        drawMaze.setCycleCount(Timeline.INDEFINITE);
        drawMaze.play();

        drawSolution.getKeyFrames().add(
                new KeyFrame(Duration.millis(50),
                        event -> {
                            mazeRef.get().drawMaze(g);
                            if (solutionRef.get().doneSolving || stopThreads.get()) {
                                System.out.println("done");
                                drawSolution.stop();
                            }
                        }));
        drawSolution.setCycleCount(Timeline.INDEFINITE);
    }

    public void redrawMaze() {
        mazeRef.get().drawMaze(g);
    }

    public void solveMaze() throws InterruptedException {
        mazeRef.get().removeSolution();

        stopThreads.set(true);
        while(mazeSolverThread != null && mazeSolverThread.isAlive())
            Thread.sleep(10);
        stopThreads.set(false);

        mazeSolverThread = new Thread(new MazeSolverRunnable(algoSelector.getValue(), mazeRef, solutionRef, solvingDelayInput, stopThreads));
        mazeSolverThread.start();
        drawSolution.play();
    }
}


class MazeGeneratorRunnable implements Runnable {
    AtomicReference<Float> squareSize;
    AtomicBoolean showSolution;
    int width;
    int height;
    Spinner<Integer> sleepGenerationTimer;
    AtomicBoolean stopThread;

    AtomicReference<Maze> mazeRef;

    public MazeGeneratorRunnable(AtomicReference<Float> squareSize, AtomicBoolean showSolution, int width, int height, Spinner<Integer> sleepGenerationTimer, AtomicReference<Maze> mazeRef, AtomicBoolean stopThread) {
        this.squareSize = squareSize;
        this.showSolution = showSolution;
        this.width = width;
        this.height = height;
        this.sleepGenerationTimer = sleepGenerationTimer;
        this.mazeRef = mazeRef;
        this.stopThread = stopThread;
    }

    @Override
    public void run() {
        Maze maze = new Maze(squareSize, showSolution, width, height, sleepGenerationTimer.getValue());
        this.mazeRef.set(maze);
        maze.generateMaze(stopThread);

    }
}

class MazeSolverRunnable implements Runnable {

    AtomicReference<Maze> mazeRef;
    AtomicReference<SearchAlgorithm> SolutionRef;
    String algo;
    Spinner<Integer> sleepTimer;
    AtomicBoolean stopThreads;

    public MazeSolverRunnable(String algo, AtomicReference<Maze> mazeRef, AtomicReference<SearchAlgorithm> SolutionRef, Spinner<Integer> sleepTimer, AtomicBoolean stopThreads) {
        this.algo = algo;
        this.mazeRef = mazeRef;
        this.SolutionRef = SolutionRef;
        this.sleepTimer = sleepTimer;
        this.stopThreads = stopThreads;
    }

    @Override
    public void run() {

        switch (algo.toLowerCase()) {
            case "bfs" -> SolutionRef.set(new BFS(this.mazeRef.get(), sleepTimer.getValue()));
            case "dfs" -> SolutionRef.set(new DFS(this.mazeRef.get(), sleepTimer.getValue()));
            case "dijkstra" -> SolutionRef.set(new Dijkstra(this.mazeRef.get(), sleepTimer.getValue()));
            default -> SolutionRef.set(new Astar(this.mazeRef.get(), sleepTimer.getValue()));
        }
        SolutionRef.get().start(stopThreads);
    }
}
