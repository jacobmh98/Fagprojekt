package model;

import java.util.ArrayList;

public class ComparePieces {

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

    public static boolean comparePieces(Double[] piece1, Double[] piece2){ //return false if pieces are different
        ArrayList<Double> newPiece1 = calculatePieceCenterOrigo(deleteObsoletePoints(piece1));
        ArrayList<Double> newPiece2 = calculatePieceCenterOrigo(deleteObsoletePoints(piece2));
        if(newPiece1.size() != newPiece2.size()){
            //System.out.println("Failed total corners check");
            return false;
        }
        ArrayList<Double> possibleRotations = calculateRotationTheta(newPiece1, newPiece2);
        if(possibleRotations.size() == 0){
            //System.out.println("Failed with no rotation possible");
            return false;
        }
        return checkRotations(newPiece1, newPiece2, possibleRotations);
    }

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

    public static double findAngle(double x1, double y1, double x2, double y2, double x3, double y3){
        double[] vector1 = {x1-x2,y1-y2};
        double[] vector2 = {x2-x3, y2-y3};
        double dotP = vector1[0]*vector2[0]+vector1[1]*vector2[1];
        double length1 = Math.sqrt(Math.pow(vector1[0],2)+Math.pow(vector1[1],2));
        double length2 = Math.sqrt(Math.pow(vector2[0],2)+Math.pow(vector2[1],2));
        return Math.acos(dotP/(length1*length2));
    }

    private static ArrayList<Double> findLengthO(ArrayList<Double> piece){
        ArrayList<Double> lengthArray = new ArrayList<>();
        for(int i = 0; i < piece.size(); i+=2){
            double length = Math.sqrt(Math.pow(piece.get(i),2)+Math.pow(piece.get(i+1),2));
            lengthArray.add(length);
        }
        return lengthArray;
    }

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
