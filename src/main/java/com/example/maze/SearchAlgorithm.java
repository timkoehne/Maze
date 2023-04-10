package com.example.maze;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class SearchAlgorithm {

    long sleepTimer;
    protected int steps = 0;
    Maze maze;
    boolean doneSolving = false;

    public SearchAlgorithm(Maze maze, long sleepTimer) {
        this.maze = maze;
        this.sleepTimer = sleepTimer;
    }

    protected abstract void algorithm(AtomicBoolean stopCondition);

    public void start(AtomicBoolean stopCondition) {
        algorithm(stopCondition);
        this.doneSolving = true;
        //maze.draw();
        printSteps();
    }

    public void printSteps() {
        System.out.println("It took " + this.getClass().getName() + " " + steps + " steps to find the exit");
    }

    public void mazeMove(Point current, Direction dir, Material mat) {
        steps++;
        maze.mazeMove(current, dir, mat);
        try {
            Thread.sleep(sleepTimer);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
