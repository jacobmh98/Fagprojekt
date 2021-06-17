package model;

public class PieceLock {
    private boolean directionIn;


    private double sideLength;
    private Corner startCorner;
    private Corner endCorner;

    public PieceLock(boolean directionIn, double sideLength, Corner startCorner, Corner endCorner) {
        this.directionIn = directionIn;
        this.sideLength = sideLength;
        this.startCorner = startCorner;
        this.endCorner = endCorner;
    }

    public boolean isDirectionIn() {
        return directionIn;
    }

    public double getSideLength() {
        return sideLength;
    }

    public Corner getStartCorner() {
        return startCorner;
    }

    public Corner getEndCorner() {
        return endCorner;
    }
}
