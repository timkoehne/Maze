package com.example.maze;

import java.lang.Math;

class Point {
    Point pred;
    int x, y;

    public Point(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public Point(int y, int x, Point pred) {
        this(y, x);
        this.pred = pred;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public int distanceTo(Point to) {
        return Math.abs(x - to.x) + Math.abs(y - to.y);

    }

    public Direction whichDirection(Point end) {
        if (x < end.x)
            return Direction.Right;
        if (x > end.x)
            return Direction.Left;
        if (y < end.y)
            return Direction.Down;
        if (y > end.y)
            return Direction.Up;
        return Direction.None;
    }

}
