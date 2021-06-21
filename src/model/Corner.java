package model;

// This class is a data structure we have created by ourself to represent the corners of a piece,
// such that each piece can contain a list of Corner objects containing all of its corners.
// We utilize it in SolvePuzzle when we compare the corners of each piece.
// Written by Jacob
class Corner {
    private Double[] vector1;
    private Double[] vector2;
    private Double[] coordinates;
    private double angle;

    // Constructor for the Corner class
    // Input - two vectors that goes from the current corner its neighbours, the coordinates of the current corner and the angle between the vectors
    // Output - creates a Corner object
    public Corner(Double[] vector1, Double[] vector2, Double[] coordinates, double angle) {
        this.vector1 = vector1;
        this.vector2 = vector2;
        this.coordinates = coordinates;
        this.angle = angle;
    }

    // Getter methods for the fields in the Corner class
    // Written by Jacob
    public double getAngle() {
        return this.angle;
    }
    public Double[] getCoordinates() {
        return this.coordinates;
    }
    public Double[][] getVectors() {
        return new Double[][]{this.vector1, this.vector2};
    }

    // Method for setting the corners after the piece has been updated moved or rotated
    // Written by Jacob
    public void updateCorner(Double[] vector1, Double[] vector2, Double[] coordinates) {
        this.vector1 = vector1;
        this.vector2 = vector2;
        this.coordinates = coordinates;
    }
}