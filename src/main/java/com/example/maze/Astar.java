package com.example.maze;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

class Astar extends SearchAlgorithm {

    public Astar(Maze maze, long sleepTimer) {
        super(maze, sleepTimer);
    }

    protected void algorithm(AtomicBoolean stopCondition) {
        Point[] startAndFinish = maze.getStartAndFinish();
        Point start = startAndFinish[0];
        Point finish = startAndFinish[1];
        maze.maze[start.y][start.x] = Material.Visited;

        maze.maze[start.y + 1][start.x] = Material.Visited;
        PriorityQueue<PointAndPriority> queue = new PriorityQueue<PointAndPriority>(new PointAndPriorityComparator());
        queue.add(new PointAndPriority(new Point(start.y + 1, start.x), 0));

        while (queue.size() != 0) {
            if (stopCondition.get()) {
                return;
            }
            PointAndPriority current = queue.peek();
            queue.remove(current);
            if (current.p.x == finish.x && current.p.y == finish.y - 1) {
                maze.onlyShowVisitedPath(current.p, this.sleepTimer);
                break;
            }
            System.out.println("currently at " + current.p);
            ArrayList<Point> neighbors = maze.getPossibleNeighbors(current.p, Material.Air);
            if (neighbors.size() == 0) {
                queue.remove(current);
            }

            for (Point p : neighbors) {
                System.out.println("adding " + p + " as possible neighbor");
                p.pred = current.p;
                queue.add(new PointAndPriority(p, p.distanceTo(finish)));
            }
            mazeMove(queue.peek().p.pred, queue.peek().p.pred.whichDirection(queue.peek().p), Material.Visited);
        }
    }
}
