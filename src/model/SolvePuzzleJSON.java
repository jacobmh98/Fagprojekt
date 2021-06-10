package model;

import controller.Controller;
import javafx.application.Platform;

import java.util.ArrayList;

public class SolvePuzzleJSON extends Thread{

    public static void runner(ArrayList<Piece> boardPieces) throws InterruptedException {
        Piece root = null;
        double epsilon = 0.00000000001;
        outerloop:
        for (Piece p : boardPieces) {


            for (Corner c : p.getVectorCorners()) {

                if (c.getAngle() + epsilon >= Math.PI / 2.0 && c.getAngle() - epsilon <= Math.PI / 2.0) {
                    root = p;

                    Double[] tempVector = new Double[]{0.0, -1.0};
                    Double[] vector1 = c.getVectors()[0];
                    Double[] vector2 = c.getVectors()[1];

                    Double angleVertical1 = findAngleBetweenVectors(vector1, tempVector);
                    Double angleVertical2 = findAngleBetweenVectors(vector2, tempVector);

                    if(angleVertical1 < angleVertical2) {
                        while (!((angleVertical1 + epsilon >= 0.0 && angleVertical1 - epsilon <= 0.0)) && !Double.isNaN(angleVertical1)) {
                            root.rotatePiece(angleVertical1);
                            angleVertical1 = findAngleBetweenVectors(c.getVectors()[0], tempVector);
                        }
                    } else {
                        root.rotatePiece(angleVertical2);
                        while (!((angleVertical2 + epsilon >= 0.0 && angleVertical2 - epsilon <= 0.0)) && !Double.isNaN(angleVertical2)) {
                            root.rotatePiece(angleVertical2);
                            angleVertical2 = findAngleBetweenVectors(c.getVectors()[1], tempVector);
                        }
                    }

                    vector1 = c.getVectors()[0];
                    vector2 = c.getVectors()[1];

                    System.out.println("Vector 1 " + vector1[0] + ", " + vector1[1]);
                    System.out.println("Vector 2 " + vector2[0] + ", " + vector2[1]);
                    System.out.println("angle1 between vertical " + angleVertical1);
                    System.out.println("angle2 between vertical " + angleVertical2);


                    if(angleVertical1 + epsilon >= 0.0 && angleVertical1 - epsilon <= 0.0) {
                        if(vector2[0] > 0.0) {
                            root.rotatePiece(Math.PI/2.0);
                        }
                    } else {
                        if(vector1[0] > 0.0) {
                            root.rotatePiece(Math.PI/2.0);
                        }
                    }

                    Double dx = -c.getCoordinates()[0];
                    Double dy = -c.getCoordinates()[1];


                    root.movePiece(dx, dy);
                    break outerloop;
                }
            }
        }

        System.out.println("Start piece " + root.getPieceID());

        ArrayList<Piece> queue = new ArrayList<>();
        queue.add(root);
        for(int i = 0; i < queue.size(); i++) {
            ArrayList<Corner> vectorCornersP1 = queue.get(i).getVectorCorners();
            for (Piece p : boardPieces) {
                if(!queue.contains(p)) {

                    ArrayList<Corner> vectorCornersP2 = p.getVectorCorners();
                    ArrayList<Corner[]> matchingCorners = new ArrayList<>();

                    for (Corner c : vectorCornersP1) {
                        for (Corner c2 : vectorCornersP2) {

                            if (c.getAngle() + epsilon >= c2.getAngle() &&
                                    c.getAngle() - epsilon <= c2.getAngle() &&
                                    !(c.getAngle() + epsilon >= Math.PI / 2.0 && c.getAngle() - epsilon <= Math.PI / 2.0)) {
                                matchingCorners.add(new Corner[]{c, c2});
                            }
                        }
                    }

                    for (Corner[] cs : matchingCorners) {
                        System.out.println("matching corner " + cs[0].getAngle() + ", " + cs[1].getAngle());

                        Double angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[0]);

                        while (!((angle1 + epsilon >= 0.0 && angle1 - epsilon <= 0.0)) && !Double.isNaN(angle1)) {
                            Double finalAngle1 = angle1;
                            p.rotatePiece(finalAngle1);
                            angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[0]);
                        }
                        Double angle2 = findAngleBetweenVectors(cs[0].getVectors()[1], cs[1].getVectors()[1]);
                        if (!(angle2 + epsilon >= 0.0 && angle2 - epsilon <= 0.0) && !Double.isNaN(angle2)) {
                            angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[1]);
                            while (!((angle1 + epsilon >= 0.0 && angle1 - epsilon <= 0.0)) && !Double.isNaN(angle1)) {
                                //                                        System.out.println("Angle" +  angle1);
                                Double finalAngle = angle1;
                                p.rotatePiece(finalAngle);
                                angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[1]);
                            }
                        }

                        Double dx = cs[0].getCoordinates()[0] - cs[1].getCoordinates()[0];
                        Double dy = cs[0].getCoordinates()[1] - cs[1].getCoordinates()[1];
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                p.movePiece(dx, dy);

                            }
                        });

                        queue.add(p);
                        sleep(30);
                    }
                }
            }
        }
    }

    public static double findAngleBetweenVectors(Double[] vector1, Double[] vector2){
        double angle;

        double dotProduct = (vector1[0] * vector2[0]) + (vector1[1] * vector2[1]);
        double magnitude1 = Math.sqrt(Math.pow(vector1[0], 2) + Math.pow(vector1[1], 2));
        double magnitude2 = Math.sqrt(Math.pow(vector2[0], 2) + Math.pow(vector2[1], 2));

        angle = Math.acos(dotProduct/(magnitude1 * magnitude2));

        return angle;
    }

    public void run(){
        ArrayList<Piece> pieces = Controller.getInstance().getBoardPieces();
        try {
            runner(pieces);
        } catch (InterruptedException e) {
            System.out.println("Thread ended");
        }
    }
}
