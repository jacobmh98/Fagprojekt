package model;

public class PieceLock {
    private final Corner prevCorner;
    private final Corner nextCorner;
    private boolean directionIn;
    private Piece p;
    private double sideLength;
    private Corner startCorner;
    private Corner endCorner;

    public PieceLock(boolean directionIn, double sideLength, Corner startCorner, Corner endCorner, Corner prevCorner, Corner nextCorner, Piece p) {
        this.directionIn = directionIn;
        this.sideLength = sideLength;
        this.startCorner = startCorner;
        this.endCorner = endCorner;
        this.p = p;
        this.prevCorner = prevCorner;
        this.nextCorner = nextCorner;
    }

    public Piece getPiece() { return p; }

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

    public Corner getPrevCorner() { return this.prevCorner; }

    public Corner getNextCorner() { return this.nextCorner; }

}
