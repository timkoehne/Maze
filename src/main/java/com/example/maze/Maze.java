package com.example.maze;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Maze {

    volatile Material[][] maze;
    AtomicReference<Float> squareSize;
    public static ArrayList<String> availableAlgorithms = new ArrayList<String>(Arrays.asList("DFS", "BFS", "Dijkstra", "Astar"));
    long sleepTimer;

    boolean finishedGenerating = false;
    AtomicBoolean showSolution;

    public static boolean isAlgorithm(String algo) {
        for (String s : availableAlgorithms) {
            if (algo.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public Maze(AtomicReference<Float> squareSize, AtomicBoolean showSolution, int width, int height, long sleepTimer) {
        this.squareSize = squareSize;
        this.sleepTimer = sleepTimer;
        this.showSolution = showSolution;

        this.maze = new Material[width][height];
        for (int h = 0; h < this.maze.length; h++) {
            for (int w = 0; w < this.maze[0].length; w++) {
                this.maze[h][w] = Material.Wall;
            }
        }

    }

    public Point[] getStartAndFinish() {
        int start = 0, end = 0;

        for (int w = 0; w < maze[0].length; w++) {
            if (maze[0][w] == Material.Air) {
                start = w;
            }
            if (maze[maze.length - 1][w] == Material.Air) {
                end = w;
            }
        }
        Point[] points = {new Point(0, start), new Point(maze.length - 1, end)};
        return points;
    }

    public void onlyShowVisitedPath(Point p, long sleepTimer) {
        for (int h = 0; h < maze.length; h++) {
            for (int w = 0; w < maze[0].length; w++) {
                if (maze[h][w] == Material.Visited) {
                    maze[h][w] = Material.Air;
                }
            }
        }

        for (int w = 0; w < maze[0].length; w++) {
            if (maze[0][w] == Material.Air) {
                maze[0][w] = Material.Visited;
            }
        }

        while (p != null && p.pred != null) {
            mazeMove(p, p.whichDirection(p.pred), Material.Visited);
            p = p.pred;
        }

        for (int w = 0; w < maze[maze.length - 1].length; w++) {
            if (maze[maze.length - 1][w] == Material.Air) {
                maze[maze.length - 1][w] = Material.Visited;
                maze[maze.length - 2][w] = Material.Visited;
            }
        }
        //draw();
    }

    void generateMaze(AtomicBoolean stopCondition) {
        Random rand = new Random();
        Stack<Point> points = new Stack<Point>();

        int start = rand.nextInt(maze[0].length / 6) * 2 + 1;
        maze[0][start] = Material.Air;
        maze[1][start] = Material.Air;
        points.push(new Point(1, start, new Point(0, start)));


        while (points.size() != 0) {
            if(stopCondition.get()){
               return;
            }

            ArrayList<Point> neighbors = getPossibleNeighbors(points.peek(), Material.Wall);
            if (neighbors.size() == 0) {
                points.pop();
            } else {
                Point nextPoint = neighbors.get(rand.nextInt(neighbors.size()));
                if (this.sleepTimer > 0) {
                    //draw();
                }
                mazeMove(points.peek(), points.peek().whichDirection(nextPoint), Material.Air);
                points.push(nextPoint);
                try {
                    Thread.sleep(this.sleepTimer);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }

        int end;
        do {
            end = rand.nextInt(maze[0].length / 6) * 2 + 1 + maze[0].length / 3 * 2;
        } while (maze[maze.length - 2][end] != Material.Air);
        maze[maze.length - 1][end] = Material.Air;

        finishedGenerating = true;
    }

    public void mazeMove(Point start, Direction dir, Material mat) {
        Point newPos1;
        Point newPos2;
        switch (dir) {
            case Up -> {
                newPos1 = new Point(start.y - 1, start.x);
                newPos2 = new Point(start.y - 2, start.x);
            }
            case Down -> {
                newPos1 = new Point(start.y + 1, start.x);
                newPos2 = new Point(start.y + 2, start.x);
            }
            case Left -> {
                newPos1 = new Point(start.y, start.x - 1);
                newPos2 = new Point(start.y, start.x - 2);
            }
            case Right -> {
                newPos1 = new Point(start.y, start.x + 1);
                newPos2 = new Point(start.y, start.x + 2);
            }
            default -> throw new IllegalStateException("Unexpected value: " + dir);
        }
        maze[newPos1.y][newPos1.x] = mat;
        maze[newPos2.y][newPos2.x] = mat;
    }

    public String toString() {
        String s = "";
        for (int h = 0; h < maze.length; h++) {
            for (int w = 0; w < maze[0].length; w++) {
                s = s + getSymbol(maze[h][w]);
            }
            s = s + "\n";
        }
        return s;
    }

    private boolean inBounds(int y, int x) {
        if (x > maze[0].length - 1 || x < 1)
            return false;
        if (y > maze.length - 1 || y < 1)
            return false;
        return true;
    }

    public ArrayList<Point> getPossibleNeighbors(Point p, Material mat) {
        ArrayList<Point> possible = new ArrayList<Point>();
        if (inBounds(p.y, p.x + 2) && maze[p.y][p.x + 1] == mat && maze[p.y][p.x + 2] == mat) {
            possible.add(new Point(p.y, p.x + 2));
        }
        if (inBounds(p.y + 2, p.x) && maze[p.y + 1][p.x] == mat && maze[p.y + 2][p.x] == mat) {
            possible.add(new Point(p.y + 2, p.x));
        }
        if (inBounds(p.y - 2, p.x) && maze[p.y - 1][p.x] == mat && maze[p.y - 2][p.x] == mat) {
            possible.add(new Point(p.y - 2, p.x));
        }
        if (inBounds(p.y, p.x - 2) && maze[p.y][p.x - 1] == mat && maze[p.y][p.x - 2] == mat) {
            possible.add(new Point(p.y, p.x - 2));
        }
        return possible;
    }

    private String getSymbol(Material mat) {
        return switch (mat) {
            case Air -> "▓";
            case Wall -> "\u001B[30m▓\u001B[0m";
            case Visited -> "\u001B[31m▓\u001B[0m";
            default -> " ";
        };
    }

    void drawMaze(GraphicsContext g) {
        g.clearRect(0, 0, maze.length * squareSize.get(), maze[0].length * squareSize.get());
        for (int h = 0; h < maze.length; h++) {
            for (int w = 0; w < maze[0].length; w++) {
                g.setFill(getColor(maze[h][w]));
                g.fillRect(w * squareSize.get(), h * squareSize.get(), squareSize.get(), squareSize.get());
            }
        }
    }

    void removeSolution() {
        for (int h = 0; h < maze.length; h++) {
            for (int w = 0; w < maze[0].length; w++) {
                if (maze[h][w] == Material.Visited)
                    maze[h][w] = Material.Air;
            }
        }
    }

    Color getColor(Material mat) {
        switch (mat) {
            case Visited:
                if (showSolution.get())
                    return Color.RED;
            case Air:
                return Color.WHITE;
            case Wall:
                return Color.BLACK;
            default:
                return Color.WHITE;
        }
    }
}
