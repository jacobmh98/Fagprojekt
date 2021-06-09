package model;

import controller.Controller;

import java.util.ArrayList;

public class SolvePuzzleJSON {

    public static void runner(ArrayList<Piece> boardPieces) {
        ArrayList<Piece> solution = new ArrayList<>();
        int index = 0;
        outerloop:
        for(Piece p : boardPieces) {
            for(Corner c : p.getVectorCorners()) {
                double epsilon = 0.001;

                if (c.getAngle() + epsilon >= Math.PI / 2.0 && c.getAngle() - epsilon <= Math.PI / 2.0) {
                    System.out.println("angle " + c.getAngle() + " for piece" + p);
                    solution.add(p);
                    break outerloop;
                }
            }
        }

        for(int i = 0; i < solution.size(); i++) {
            ArrayList<Corner> vectorCornersP1 = solution.get(i).getVectorCorners();

            for(Piece p : boardPieces) {
                if(!solution.contains(p)) {
                    ArrayList<Corner> vectorCornersP2 = p.getVectorCorners();

                    for(Corner c : vectorCornersP1) {
                        for(Corner c2 : vectorCornersP2) {
                            double epsilon = 0.001;
                            if(c.getAngle()+epsilon >= c2.getAngle() &&
                                    c.getAngle()-epsilon <= c2.getAngle() &&
                                    c.getAngle()*(180/Math.PI) != 90) {
                                System.out.println("Match with angle " + c.getAngle()*(180/Math.PI));

                                Double angle1 = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[0]);

                                while(!((angle1+epsilon >= 0.0 && angle1-epsilon <= 0.0)) && !Double.isNaN(angle1)) {
                                    p.rotatePiece(angle1);
                                    angle1 = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[0]);
                                }
                                Double angle2 = findAngleBetweenVectors(c.getVectors()[1], c2.getVectors()[1]);
                                if(!(angle2 + epsilon >= 0.0 && angle2 - epsilon <= 0.0) && !Double.isNaN(angle2)){
                                    angle1 = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[1]);
                                    while(!((angle1+epsilon >= 0.0 && angle1-epsilon <= 0.0)) && !Double.isNaN(angle1)) {
                                        System.out.println("Angle" +  angle1);
                                        p.rotatePiece(angle1);
                                        angle1 = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[1]);
                                    }
                                }

                                Double dx = c.getCoordinates()[0] - c2.getCoordinates()[0];
                                Double dy = c.getCoordinates()[1] - c2.getCoordinates()[1];

                                p.movePiece(dx , dy);
                                solution.add(p);
                            }
                        }
                    }
                }
            }
        }


//        Piece p1 = boardPieces.get(0);
//        Piece p2 = boardPieces.get(1);
//        ArrayList<Corner> vectorCornersP1 = p1.getVectorCorners();
//        ArrayList<Corner> vectorCornersP2 = p2.getVectorCorners();
//        for(Corner c : vectorCornersP1) {
//            for(Corner c2 : vectorCornersP2) {
//                double epsilon = 0.001;
//                if(c.getAngle()+epsilon >= c2.getAngle() &&
//                        c.getAngle()-epsilon <= c2.getAngle() &&
//                        c.getAngle()*(180/Math.PI) != 90) {
//                    System.out.println("Match with angle " + c.getAngle()*(180/Math.PI));
//
//                    Double angle = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[0]);
//                    while(!(angle == 0.0 || Double.isNaN(angle))) {
//                        System.out.println("angle " + angle*(180/Math.PI));
//                        p2.rotatePiece(angle);
//                        angle = findAngleBetweenVectors(c.getVectors()[0], c2.getVectors()[0]);
//                    }
//
//                    Double dx = c.getCoordinates()[0] - c2.getCoordinates()[0];
//                    Double dy = c.getCoordinates()[1] - c2.getCoordinates()[1];
//
//                    p2.movePiece(dx , dy);
//                }
//            }
//        }


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
