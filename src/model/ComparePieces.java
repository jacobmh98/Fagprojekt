package model;

import java.util.ArrayList;

public class ComparePieces {

    // Method that checks all possible pairs of pieces if they are equal using the comparePiece method
    // Input - an array of pieces
    // Output - true if any pieces are a duplicate of another and false if not
    // Written by Oscar
    public static boolean checkForDuplicates(Piece[] pieceList){
        for(int i = 0; i < pieceList.length; i++){
            for(int j = i; j < pieceList.length; j++){
                if(i != j){
                    if(comparePieces(pieceList[i].getCorners(), pieceList[j].getCorners())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Method that checks all possible pairs of pieces if they are equal using the comparePiece method
    // Input - a list of pieces
    // Output - true if any pieces are a duplicate of another and false if not
    // Written by Oscar
    public static boolean checkForDuplicates(ArrayList<Piece> pieceList){
        for(int i = 0; i < pieceList.size(); i++){
            for(int j = i; j < pieceList.size(); j++){
                if(i != j){
                    if(comparePieces(pieceList.get(i).getCorners(), pieceList.get(j).getCorners())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Method that compares to arrays of corners to check if the pieces they make are the same
    // Input - two arrays of corners that makes up pieces (arrays have the following syntax {x1,y1,x2,y2....,xn,yn}
    // Output - true if the pieces are equal and false if they are different
    // Written by Oscar
    public static boolean comparePieces(Double[] piece1, Double[] piece2){
        ArrayList<Double> newPiece1 = calculatePieceCenterOrigo(deleteObsoletePoints(piece1));
        ArrayList<Double> newPiece2 = calculatePieceCenterOrigo(deleteObsoletePoints(piece2));
        if(newPiece1.size() != newPiece2.size()){ // Checks if the pieces if the same number of corners
            return false;
        }
        ArrayList<Double> possibleRotations = calculateRotationTheta(newPiece1, newPiece2);
        if(possibleRotations.size() == 0){ // Checks if the reference point didn't matched any of the other pieces corners
            return false;
        }
        return checkRotations(newPiece1, newPiece2, possibleRotations);
    }

    // Method that deletes all points that are not defined as corners
    // Definition of a corner is A->B->C then if the angle between AB and BC is 180 B is not a corner (-> means connected to)
    // Input - Array of corners that makes up a piece
    // Output - Array where all non-corners are removed
    // Written by Oscar
    private static ArrayList<Double> deleteObsoletePoints(Double[] piece){
        ArrayList<Double> newPoints = new ArrayList<>();
        for(int i = 0; i < piece.length; i += 2){
            int index1;
            int index3;
            if(i == 0){
                index1 = piece.length-2;
                index3 = i+2;
            } else if (i == piece.length-2){
                index1 = i-2;
                index3 = 0;
            } else {
                index1 = i-2;
                index3 = i+2;
            }
            double angle = findAngle(piece[index1], piece[index1+1], piece[i], piece[i+1], piece[index3], piece[index3+1]);
            //angle == angle is because if we try to take acos() of something greater than 1 we get NaN and NaN != NaN
            if(angle != Math.PI && angle != 0 && angle == angle){
                newPoints.add(piece[i]);
                newPoints.add(piece[i+1]);
            }
        }
        return newPoints;
    }

    // Method that finds the angle between the two vectors AB BC given the 3 points A->B->C (-> means connected to)
    // Input - the x coordinates of 3 points and y coordinates of 3 points
    // Output - the angle between the first two points and the last two points
    // Written by Oscar
    public static double findAngle(double x1, double y1, double x2, double y2, double x3, double y3){
        double[] vector1 = {x1-x2,y1-y2};
        double[] vector2 = {x2-x3, y2-y3};
        double dotP = vector1[0]*vector2[0]+vector1[1]*vector2[1];
        double length1 = Math.sqrt(Math.pow(vector1[0],2)+Math.pow(vector1[1],2));
        double length2 = Math.sqrt(Math.pow(vector2[0],2)+Math.pow(vector2[1],2));
        return Math.acos(dotP/(length1*length2));
    }

    // Method that finds the length from Origo to all corners in a piece
    // Input - a list of corners (the center of the piece these corners make up needs to be in Origo)
    // input have the following syntax {x1,y1,x2,y2....,xn,yn}
    // Output - a list the length from Origo to each corner
    // Written by Oscar
    private static ArrayList<Double> findLengthO(ArrayList<Double> piece){
        ArrayList<Double> lengthArray = new ArrayList<>();
        for(int i = 0; i < piece.size(); i+=2){
            double length = Math.sqrt(Math.pow(piece.get(i),2)+Math.pow(piece.get(i+1),2));
            lengthArray.add(length);
        }
        return lengthArray;
    }

    // Method that finds the center of piece from a list of its corners and
    // then creates a new list of corners where the center is in Origo
    // Input - List of corners that make up a piece
    // Output - List of corners that make up the same piece, but with center in Origo
    // Written by Oscar
    private static ArrayList<Double> calculatePieceCenterOrigo(ArrayList<Double> piece){
        ArrayList<Double> newCoords = new ArrayList<>();
        double sumX = 0;
        double sumY = 0;
        for(int i = 0; i < piece.size(); i+=2){
            sumX += piece.get(i);
            sumY += piece.get(i+1);
        }
        double moveX = sumX/(piece.size()/2.0);
        double moveY = sumY/(piece.size()/2.0);
        for(int i = 0; i < piece.size(); i+=2){
            newCoords.add(piece.get(i)-moveX);
            newCoords.add(piece.get(i+1)-moveY);
        }
        return newCoords;
    }

    // Method that calculates the distance from the first point in the first piece and all corners in the second piece
    // If any distance matches within 0.0000000001 error, the angle between the two points are calculated
    // Input - Two lists of corners that makes up pieces
    // Output - a list of the angle between the first point in piece 1 and all points in piece 2 with the same distance from Origo
    // Written by Oscar
    private static ArrayList<Double> calculateRotationTheta(ArrayList<Double> piece1, ArrayList<Double> piece2){
        double epsilon = 0.0000000001;
        ArrayList<Double> rotationAngles = new ArrayList<>();
        ArrayList<Double> length1 = findLengthO(piece1);
        ArrayList<Double> length2 = findLengthO(piece2);
        //We assume that the first point in piece1 has been rotated to be at x=0 (we do this in check rotations)
        for(int i = 0; i < piece2.size(); i+=2){
            double upper = length2.get(i/2) + epsilon;
            double lower = length2.get(i/2) - epsilon;
            if(length1.get(0) < upper && length1.get(0) > lower){ //If point i is not the same length away as starting point a rotation wont be possible
                double angle12 = findAngle(0.0,length1.get(0),0.0,0.0,piece2.get(i), piece2.get(i+1));
                rotationAngles.add(angle12);
            }
        }
        return rotationAngles;
    }

    // Method that rotates a list corners around their center by a certain angle
    // Input - List of corners that makes up a piece, and an angle to rotate the corners
    // Output - A list of the new corners after rotation
    // Written by Oscar
    private static ArrayList<Double> rotateArray(ArrayList<Double> piece, double angle){
        ArrayList<Double> rotatedArray = new ArrayList<>();
        for(int i = 0; i < piece.size(); i+=2){
            double x = Math.cos(angle)*piece.get(i)-Math.sin(angle)*piece.get(i+1);
            double y = Math.cos(angle)*piece.get(i+1)+Math.sin(angle)*piece.get(i);
            rotatedArray.add(x);
            rotatedArray.add(y);
        }
        return rotatedArray;
    }

    // Method that checks all the rotations from calculateRotationTheta using rotateArray and checks if all corners on
    // piece1 matches a corner on piece2
    // Input - Two list of corners that makes up pieces, a list of all possible valid angles
    // Output - Return true if one of the rotations makes the two pieces equal, false if not
    // Written by Oscar
    private static boolean checkRotations(ArrayList<Double> piece1, ArrayList<Double> piece2, ArrayList<Double> angles){
        double epsilon = 0.0000000001;
        ArrayList<Double> length1 = findLengthO(piece1);
        ArrayList<Double> startingPiece1;
        ArrayList<Double> rotatedPiece1;
        double angle = findAngle(0.0, length1.get(0), 0.0, 0.0, piece1.get(0), piece1.get(1));
        if(piece1.get(0) > 0){
            startingPiece1 = rotateArray(piece1, -angle);
        } else {
            startingPiece1 = rotateArray(piece1, angle);
        }
        for(int i = 0; i < angles.size(); i++){
            if(piece2.get(i*2) > 0) {
                rotatedPiece1 = rotateArray(startingPiece1, angles.get(i));
            } else {
                rotatedPiece1 = rotateArray(startingPiece1, -angles.get(i));
            }
            boolean isEqual = true;
            for(int j = 0; j < rotatedPiece1.size(); j++){
                double upper = piece2.get(j) + epsilon;
                double lower = piece2.get(j) - epsilon;
                if (rotatedPiece1.get(j) > upper || rotatedPiece1.get(j) < lower) {
                    isEqual = false;
                    break;
                }
            }
            if(isEqual){
                return true;
            }
        }
        return false;
    }
}
