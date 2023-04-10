package com.example.maze;

import java.util.Comparator;

class PointAndPriority {
    Point p;
    int priority;

    public PointAndPriority(Point p, int priority) {
        this.p = p;
        this.priority = priority;
    }

}

class PointAndPriorityComparator implements Comparator<PointAndPriority> {
    public int compare(PointAndPriority p1, PointAndPriority p2) {
        if (p1.priority < p2.priority) {
            return -1;
        }
        if (p1.priority == p2.priority) {
            return 0;
        }
        return 1;
    }
}
