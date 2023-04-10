package com.example.maze;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DFS extends SearchAlgorithm {

    public DFS(Maze maze, long sleepTimer) {
        super(maze, sleepTimer);
    }

    protected void algorithm(AtomicBoolean stopCondition) {
        Point[] startAndFinish = maze.getStartAndFinish();
        Point start = startAndFinish[0];
        Point finish = startAndFinish[1];
        maze.maze[start.y][start.x] = Material.Visited;
        Point firstElement = new Point(start.y + 1, start.x);
        Point lastElement = new Point(finish.y - 1, finish.x);
        maze.maze[firstElement.y][firstElement.x] = Material.Visited;

        ArrayList<Point> neighbors = maze.getPossibleNeighbors(firstElement, Material.Air);
        if (neighbors.size() == 0) {
            return;
        }
        DFS(firstElement, lastElement, stopCondition);
    }

    private boolean DFS(Point current, Point stopAt, AtomicBoolean stopCondition) {
        boolean endFound = false;
        System.out.println(current);

        if (current.x == stopAt.x && current.y == stopAt.y) {
            maze.onlyShowVisitedPath(current, sleepTimer);
            return true;
        }

        ArrayList<Point> neighbors = maze.getPossibleNeighbors(current, Material.Air);
        if (neighbors.size() == 0) {
            return false;
        }
        for (Point p : neighbors) {
            p.pred = current;
            if (endFound || stopCondition.get()) {
                return true;
            }
            mazeMove(current, current.whichDirection(p), Material.Visited);
            endFound = DFS(p, stopAt, stopCondition);
        }
        return endFound;
    }
}
