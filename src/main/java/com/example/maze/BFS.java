package com.example.maze;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

class BFS extends SearchAlgorithm {

    public BFS(Maze maze, long sleepTimer) {
        super(maze, sleepTimer);
    }

    protected void algorithm(AtomicBoolean stopCondition) {
        Point[] startAndFinish = maze.getStartAndFinish();
        Point start = startAndFinish[0];
        Point finish = startAndFinish[1];
        maze.maze[start.y][start.x] = Material.Visited;

        maze.maze[start.y + 1][start.x] = Material.Visited;
        ArrayList<Point> queue = new ArrayList<Point>();
        queue.add(new Point(start.y + 1, start.x));

        while (queue.size() != 0) {
            if (stopCondition.get()) {
                return;
            }


            if (queue.get(0).x == finish.x && queue.get(0).y == finish.y - 1) {
                maze.onlyShowVisitedPath(queue.get(0), sleepTimer);
                break;
            }
            ArrayList<Point> neighbors = maze.getPossibleNeighbors(queue.get(0), Material.Air);
            if (neighbors.size() == 0) {
                queue.remove(0);
            }
            for (Point p : neighbors) {
                p.pred = queue.get(0);
                mazeMove(queue.get(0), queue.get(0).whichDirection(p), Material.Visited);
                queue.add(p);
            }
        }
    }
}
