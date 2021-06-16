package model;

// This class is a data structure we have created by ourself to represent the side length of a piece,
// such that each piece can contain a list of SideLength objects containing all of its side lengths.
// We utilize it in SolvePuzzle when we compare the side lengths of each piece.
// Written by Jacob
class SideLength implements Comparable {
    private Integer pieceId;
    private Double length;
    private Double[] corner1;
    private Double[] corner2;

    public SideLength(Integer pieceId, Double length, Double[] corner1, Double[] corner2) {
        this.pieceId = pieceId;
        this.length = length;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    // Getter methods for the fields of the class
    // Written by Jacob
    public Integer getPieceId() {
        return this.pieceId;
    }
    public Double getValue() {
        return this.length;
    }
    public Double[][] getCorners() {
        return new Double[][] {new Double[]{this.corner1[0], this.corner1[1]}, new Double[]{this.corner2[0], this.corner2[1]}};
    }

    // Method for comparing the current side length to another side length.
    // Written by Jacob
    @Override
    public int compareTo(Object l) {
        return (int)(this.length - ((SideLength) l).getValue());
    }

    // Method for updating a side length when the piece has been moved. It takes the updated corners and length
    // as argument.
    // Written by Jacob
    public void update(Double sl, Double[] corner1, Double[] corner2) {
        this.length = sl;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }
}
