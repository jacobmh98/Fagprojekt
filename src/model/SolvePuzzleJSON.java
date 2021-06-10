package model;

import controller.Controller;

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
                            p.rotatePiece(angle1);
                            angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[0]);
                        }
                        Double angle2 = findAngleBetweenVectors(cs[0].getVectors()[1], cs[1].getVectors()[1]);
                        if (!(angle2 + epsilon >= 0.0 && angle2 - epsilon <= 0.0) && !Double.isNaN(angle2)) {
                            angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[1]);
                            while (!((angle1 + epsilon >= 0.0 && angle1 - epsilon <= 0.0)) && !Double.isNaN(angle1)) {
                                //                                        System.out.println("Angle" +  angle1);
                                p.rotatePiece(angle1);
                                angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[1]);
                            }
                        }

                        Double dx = cs[0].getCoordinates()[0] - cs[1].getCoordinates()[0];
                        Double dy = cs[0].getCoordinates()[1] - cs[1].getCoordinates()[1];
                        p.movePiece(dx, dy);
                        sleep(Controller.getInstance().getSolveSpeed());
                        queue.add(p);
                    }
                }
            }
        }
        System.out.println(checkIfSolved(boardPieces));
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

    public static boolean checkIfSolved(ArrayList<Piece> boardPieces){
        if(Controller.getInstance().getGraph().getAdjVertices().size() < boardPieces.size()){
            System.out.println("Not all pieces are snapped together");
            return false;
        }
        double[] boundaryBox = findBoundaryBox(boardPieces);
        for(Piece p1 : boardPieces){
            for(SideLength s1 : p1.getSideLengths()){
                int position = checkBoundaryBox(boundaryBox, s1);
                if(position == 0){
                    System.out.println("One side was out of bounds"); //Doesn't do anything right now
                    return false;
                } else if(position == 2) {
                    boolean foundMatch = false;
                    p2Loop:
                    for (Piece p2 : boardPieces) {
                        if (!p1.equals(p2)) {
                            for (SideLength s2 : p2.getSideLengths()) {
                                if(checkMatchingSides(s1, s2)){
                                    foundMatch = true;
                                    break p2Loop;
                                }
                            }
                        }
                    }
                    if(!foundMatch){
                        System.out.println("An inner side didn't have a matching side");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static double[] findBoundaryBox(ArrayList<Piece> boardPieces){
        Double lowX = null, lowY = null, highX = null, highY = null;
        for(Piece p : boardPieces){
            Double[] corners = p.getCorners();
            for(int i = 2; i < corners.length; i++){
                double value = corners[i];
                if(i%2 == 0){
                    if(lowX == null && highX == null){
                        lowX = value;
                        highX = value;
                    } else if (lowX > value){
                        lowX = value;
                    } else if (highX < value){
                        highX = value;
                    }
                } else {
                    if(lowY == null && highY == null){
                        lowY = value;
                        highY = value;
                    } else if (lowY > value){
                        lowY = value;
                    } else if (highY < value){
                        highY = value;
                    }
                }
            }
        }
        double[] boundaryBox = {lowX, highX, lowY, highY};
        return boundaryBox;
    }

    private static int checkBoundaryBox(double[] boundary, SideLength s1) {
        Double[][] corners = s1.getCorners();
        double epsilon = 0.00001;
        //return values 0 -> outside box -- 1 -> on the side of the box -- 2 -> inside the box
        if (corners[0][0] > boundary[1]+epsilon || corners[1][0] > boundary[1]+epsilon ||
                corners[0][0] < boundary[0]-epsilon || corners[1][0] < boundary[0]-epsilon) { //Check inside x values

            return 0;
        }
        if (corners[0][1] > boundary[3]+epsilon || corners[1][1] > boundary[3]+epsilon ||
                corners[0][1] < boundary[2]-epsilon || corners[1][1] < boundary[2]-epsilon) { //Check inside y values
            return 0;
        }
        if (compareEpsilon(corners[0][0], boundary[0]) || compareEpsilon(corners[0][0], boundary[1]) ||
                compareEpsilon(corners[1][0], boundary[0]) || compareEpsilon(corners[1][0], boundary[1])) { //Check x on the boundary
            return 1;
        }
        if (compareEpsilon(corners[0][1], boundary[2]) || compareEpsilon(corners[0][1], boundary[3]) ||
                compareEpsilon(corners[1][1], boundary[2]) || compareEpsilon(corners[1][1], boundary[3])) { //Check y on the boundary
            return 1;
        }
        return 2;
    }

    private static boolean checkMatchingSides(SideLength s1, SideLength s2){
        Double[][] corners1 = s1.getCorners();
        Double[][] corners2 = s2.getCorners();
        if(compareEpsilon(corners1[0][0], corners2[0][0]) && compareEpsilon(corners1[0][1], corners2[0][1]) && compareEpsilon(corners1[1][0], corners2[1][0]) && compareEpsilon(corners1[1][1], corners2[1][1])){
            return true;
        }
        if(compareEpsilon(corners1[0][0], corners2[1][0]) && compareEpsilon(corners1[0][1], corners2[1][1]) && compareEpsilon(corners1[1][0], corners2[0][0]) && compareEpsilon(corners1[1][1], corners2[0][1])){
            return true;
        }
        return false;
    }

    private static boolean compareEpsilon(double value1, double value2){
        double epsilon = 0.00001;
        if(value1 + epsilon >= value2 && value1 - epsilon <= value2){
            return true;
        }
        return false;
    }



}
