package model;

import java.sql.Array;
import java.util.ArrayList;

public class ComparePieces {

    public static boolean comparePieces(Double[] piece1, Double[] piece2){
        ArrayList<Double> newPiece1 = calculatePieceCenterOrigo(deleteObsoletePoints(piece1));
        ArrayList<Double> newPiece2 = calculatePieceCenterOrigo(deleteObsoletePoints(piece2));
        if(newPiece1.size() != newPiece2.size()){
            System.out.println("Failed total corners check");
            return false;
        }
        //return checkAngles(newPiece1, newPiece2);
        ArrayList<Double> possibleRotations = calculateRotationTheta(newPiece1, newPiece2);
        if(possibleRotations.size() == 0){
            System.out.println("Failed with no rotation possible");
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
            if(angle != Math.PI && angle != 0){
                newPoints.add(piece[i]);
                newPoints.add(piece[i+1]);
            }
        }
        return newPoints;
    }

    private static boolean checkAngles(ArrayList<Double> piece1, ArrayList<Double> piece2){
        ArrayList<Double> piece1Angles = new ArrayList<>();
        ArrayList<Double> piece2Angles = new ArrayList<>();
        for(int i = 0; i < piece1.size(); i+=2){
            int index1;
            int index3;
            if(i == 0){
                index1 = piece1.size()-2;
                index3 = i+2;
            } else if (i ==piece1.size()-2){
                index1 = i-2;
                index3 = 0;
            } else {
                index1 = i-2;
                index3 = i+2;
            }
            piece1Angles.add(findAngle(piece1.get(index1), piece1.get(index1+1), piece1.get(i), piece1.get(i+1), piece1.get(index3), piece1.get(index3+1)));
            piece2Angles.add(findAngle(piece2.get(index1), piece2.get(index1+1), piece2.get(i), piece2.get(i+1), piece2.get(index3), piece2.get(index3+1)));
        }
        System.out.println(piece1Angles);
        System.out.println(piece2Angles);
        for(int i = 0; i < piece1Angles.size(); i++){
            if(piece2Angles.indexOf(piece1Angles.get(i)) == -1){ //First test if all angles from piece1 are in piece2
                System.out.println("Failed All angles exist in each other check");
                return false;
            }
        }
        //Take the first angle of piece1 and find all entries of the same value in piece2
        ArrayList<Integer> angleIndexes = new ArrayList<>();
        for(int i = 0; i < piece2Angles.size(); i++){
            if(piece2Angles.get(i).equals(piece1Angles.get(0))){
                angleIndexes.add(i);
            }
        }
        //For all the angleIndexes we got we check if the angles matches forward or backward
        ArrayList<Integer> successIndexes = new ArrayList<>();
        ArrayList<Boolean> direction = new ArrayList<>();
        for(int i = 0; i < angleIndexes.size(); i++){
            if(checkOrder(piece1Angles, piece2Angles, angleIndexes.get(i),true)){
                successIndexes.add(angleIndexes.get(i));
                direction.add(true);
            }
            if(checkOrder(piece1Angles, piece2Angles, angleIndexes.get(i),false)){
                successIndexes.add(angleIndexes.get(i));
                direction.add(false);
            }
        }
        if(successIndexes.size() < 1){
            System.out.println("Failed angle order check");
            return false;
        }
        //Now we need to check if all the sides matches (side index is the same index as the starting point ie side 0 is point 0 and 1
        ArrayList<Double> sideLength1 = findLength(piece1);
        ArrayList<Double> sideLength2 = findLength(piece2);
        System.out.println(sideLength1);
        System.out.println(sideLength2);
        for(int i = 0; i < successIndexes.size(); i++){
            if(checkOrder(sideLength1, sideLength2, successIndexes.get(i), direction.get(i))){
                return true;
            }
        }
        System.out.println("Failed side order check");
        return false;
    }

    private static boolean checkOrder(ArrayList<Double> set1, ArrayList<Double> set2, int indexStart, boolean forward){
        int index = indexStart;
        for(int i = 0; i < set1.size(); i++){
            if(!set1.get(i).equals(set2.get(index))){
                return false;
            }
            if(forward) {
                if (index ==set2.size() - 1) {
                    index = 0;
                } else {
                    index++;
                }
            } else {
                if(index == 0){
                    index = set2.size()-1;
                } else {
                    index--;
                }
            }
        }
        return true;
    }

    private static double findAngle(double x1, double y1, double x2, double y2, double x3, double y3){
        double[] vector1 = {x1-x2,y1-y2};
        double[] vector2 = {x2-x3, y2-y3};
        double dotP = vector1[0]*vector2[0]+vector1[1]*vector2[1];
        double length1 = Math.sqrt(Math.pow(vector1[0],2)+Math.pow(vector1[1],2));
        double length2 = Math.sqrt(Math.pow(vector2[0],2)+Math.pow(vector2[1],2));
        double angle  = Math.acos(dotP/(length1*length2));
        return angle;
    }

    private static ArrayList<Double> findLength(ArrayList<Double> piece){
        ArrayList<Double> sideLength = new ArrayList<>();
        for(int i = 0; i < piece.size()-2; i+=2){
            double x = piece.get(i)-piece.get(i+2);
            double y = piece.get(i+1)-piece.get(i+3);
            double length = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
            sideLength.add(length);
        }
        double x = piece.get(piece.size()-2)-piece.get(0);
        double y = piece.get(piece.size()-1)-piece.get(1);
        double length = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        sideLength.add(length);
        return sideLength;
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
        double moveX = sumX/(piece.size()/2);
        double moveY = sumY/(piece.size()/2);
        for(int i = 0; i < piece.size(); i+=2){
            newCoords.add(piece.get(i)-moveX);
            newCoords.add(piece.get(i+1)-moveY);
        }
        return newCoords;
    }

    private static ArrayList<Double> calculateRotationTheta(ArrayList<Double> piece1, ArrayList<Double> piece2){
        double epsilon = 0.00000000000001;
        ArrayList<Double> rotationAngles = new ArrayList<>();
        ArrayList<Double> length1 = findLengthO(piece1);
        ArrayList<Double> length2 = findLengthO(piece2);
        //We assume that the first point in piece1 has been rotated to be at x=0 (we do this in checkroations)
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
        double epsilon = 0.00000000000001;
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
                //System.out.println(rotatedPiece1.get(j) + ", " + piece2.get(j));
                double upper = piece2.get(j) + epsilon;
                double lower = piece2.get(j) - epsilon;
                if(rotatedPiece1.get(j) > upper || rotatedPiece1.get(j) < lower){
                    isEqual = false;
                }
            }
            System.out.println();
            if(isEqual){
                return true;
            }
        }
        return false;
    }
}
