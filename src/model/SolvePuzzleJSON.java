package model;

import controller.Controller;

import java.util.ArrayList;

public class SolvePuzzleJSON {

    public static void runner(ArrayList<Piece> boardPieces) {

        Piece p1 = boardPieces.get(0);
        Piece p2 = boardPieces.get(1);
        ArrayList<Corner> vectorCornersP1 = p1.getVectorCorners();
        ArrayList<Corner> vectorCornersP2 = p2.getVectorCorners();
        for(Corner c : vectorCornersP1) {
            for(Corner c2 : vectorCornersP2) {
                double epsilon = 0.001;
                if(c.getAngle()+epsilon >= c2.getAngle() &&
                        c.getAngle()-epsilon <= c2.getAngle() &&
                        c.getAngle()*(180/Math.PI) != 90) {
                    System.out.println("Match with angle " + c.getAngle()*(180/Math.PI));

                    Double angle = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[0]);
                    while(!(angle == 0.0 || Double.isNaN(angle))) {
                        System.out.println("angle " + angle*(180/Math.PI));
                        p2.rotatePiece(angle);
                        angle = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[0]);
                    }

                    Double dx = c.getCoordinates()[0] - c2.getCoordinates()[0];
                    Double dy = c.getCoordinates()[1] - c2.getCoordinates()[1];

                    p2.movePiece(dx , dy);
                }
            }
        }


//        for(int i = 0; i < boardPieces.size(); i++) {
//            ArrayList<Corner> vectorCornersP1 = boardPieces.get(i).getVectorCorners();
//
//            for(int j = i+1; j < boardPieces.size(); j++) {
//                ArrayList<Corner> vectorCornersP2 = boardPieces.get(j).getVectorCorners();
//                for(Corner c : vectorCornersP1) {
//                    for(Corner c2 : vectorCornersP2) {
//                        double epsilon = 0.001;
//
//                        if(c.getAngle()+epsilon >= c2.getAngle() &&
//                                c.getAngle()-epsilon <= c2.getAngle() && c.getAngle()*(180/Math.PI) != 90) {
//                            System.out.println("Match with angle " + c.getAngle()*(180/Math.PI));
//                        }
//                    }
//                }
//            }
//        }
    }

    public static double findAngleBetweenVectors(Double[] vector1, Double[] vector2){
        double angle;

        double dotProduct = (vector1[0] * vector2[0]) + (vector1[1] * vector2[1]);
        double magnitude1 = Math.sqrt(Math.pow(vector1[0], 2) + Math.pow(vector1[1], 2));
        double magnitude2 = Math.sqrt(Math.pow(vector2[0], 2) + Math.pow(vector2[1], 2));

        angle = Math.acos(dotProduct/(magnitude1 * magnitude2));

        return angle;
    }
}
