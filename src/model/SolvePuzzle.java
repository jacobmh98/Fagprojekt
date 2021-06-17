package model;

import controller.Controller;
import java.util.*;

public class SolvePuzzle extends Thread{
    private ArrayList<Piece> boardPieces;
    private ArrayList<SideLength> sideLengthsSorted = new ArrayList<>();
    private Controller controller = Controller.getInstance();
    private boolean solveBySideLength;

    public SolvePuzzle(boolean b) {
        this.solveBySideLength = b;
        this.boardPieces = controller.getBoardPieces();
    }

    // Method that is in charge of initializing a sorting algorithm of the side lengths.
    // It copies all of the values of the unsorted side lengths into a temporary array
    // and overwrites the unsorted side lengths into the sorted after having called merge sort.
    // Written by Oscar
    public void sortSideLength(){
        SideLength[] tempArray = new SideLength[sideLengthsSorted.size()];
        for(int i = 0; i < sideLengthsSorted.size(); i++){
            tempArray[i] = sideLengthsSorted.get(i);
        }
        mergeSort(tempArray, tempArray.length);
        for(int i = 0; i < sideLengthsSorted.size(); i++){
            sideLengthsSorted.set(i, tempArray[i]);
        }
    }

    // Method that uses mergesort algorithm to sort an array of sidelength
    // Input - A temporary array and the number of entries
    // Output - Non, but it alters the tempArray which alters the actual address of the argument
    // Written by Oscar
    private void mergeSort(SideLength[] tempArray, int entries){
        if(entries <= 1){
            return;
        }
        int middle = entries/2;
        SideLength[] left = new SideLength[middle];
        SideLength[] right = new SideLength[entries-middle];
        for(int i = 0; i  < middle; i++){
            left[i] = tempArray[i];
        }
        for(int i = middle; i < entries; i++){
            right[i-middle] = tempArray[i];
        }
        mergeSort(left, middle);
        mergeSort(right, entries-middle);
        merge(tempArray, left, right, middle, entries-middle);
    }

    // Method that sort two arrays using the mergesort algorithm
    // Input - temparray to merge into, both arrays to merge into that tempArray and the entries in both arrays
    // Output - Non, but but it alters the tempArray which alters the actual address of the argument
    // Written by Oscar
    private void merge(SideLength[] tempArray, SideLength[] left, SideLength[] right, int entriesL, int entriesR){
        int i = 0, j = 0, k = 0;
        while(i < entriesL && j < entriesR){
            if(left[i].getValue() <= right[j].getValue()){
                tempArray[k++] = left[i++];
            } else {
                tempArray[k++] = right[j++];
            }
        }
        while(i < entriesL){
            tempArray[k++] = left[i++];
        }
        while(j < entriesR){
            tempArray[k++] = right[j++];
        }
    }

    // Method for solving a puzzle by sorting the side lengths and comparing those that are equal.
    // It takes all of the board pieces, sorts them, identifies a start piece with a 90 deg angle,
    // and then starts adding pieces to this piece by using a queue where the side lengths matches.
    // Written by Jacob
    public void solveBySideLengths() throws InterruptedException {
        for (Piece p : boardPieces) {
            for (SideLength l : p.getSideLengths()) {
                sideLengthsSorted.add(l);
            }
        }

        sortSideLength();
        removeSingleSideLengths();

        ArrayList<Piece> startCorners = new ArrayList<>();
        Piece root = null;

        for(Piece p : boardPieces) {
            for(Piece p2 : controller.getGraph().depthFirstTraversal(p)) {
                controller.getGraph().removeEdge(p, p2);
            }
            for(Corner c : p.getVectorCorners()) {
                double epsilon = 0.00000000001;
                if (c.getAngle() + epsilon >= Math.PI / 2.0 && c.getAngle() - epsilon <= Math.PI / 2.0) {
                    startCorners.add(p);
                }
            }
        }

        if(startCorners.size() == 4) {
            root = startCorners.get(0);

            for(Corner c : root.getVectorCorners()) {
                double epsilon = 0.00000000001;
                if (c.getAngle() + epsilon >= Math.PI / 2.0 && c.getAngle() - epsilon <= Math.PI / 2.0) {
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

                    if(angleVertical1 + epsilon >= 0.0 && angleVertical1 - epsilon <= 0.0) {
                        if(vector2[0] > 0.0) {
                            root.rotatePiece(Math.PI/2.0);
                        }
                    } else {
                        if(vector1[0] > 0.0) {
                            root.rotatePiece(Math.PI/2.0);
                        }
                    }

                    double dx = -c.getCoordinates()[0] + root.getCenter()[0];
                    double dy = -c.getCoordinates()[1] + root.getCenter()[1];

                    root.movePiece(dx, dy);
                }
            }

            sleep(Controller.getInstance().getSolveSpeed());

            ArrayList<Piece> queue = new ArrayList<>();
            queue.add(root);

            for(int i = 0; i < queue.size(); i++) {
                for(int j = 0; j < queue.get(i).getSideLengths().size(); j++) {
                    SideLength s = queue.get(i).getSideLengths().get(j);

                    for(int k = 0; k < sideLengthsSorted.size(); k++) {
                        double epsilon = 0.0001;
                        SideLength s2 = sideLengthsSorted.get(k);

                        if(s.getValue()+epsilon >= s2.getValue() && s.getValue()-epsilon <= s2.getValue() &&
                                s.getPieceId() != s2.getPieceId()) {
                            System.out.println(s.getValue() + " should connect to " + s2.getValue());

                            Piece p1 = queue.get(i);
                            Piece p2 = boardPieces.get(s2.getPieceId());

                            if(!queue.contains(p2)  &&  p1.checkIfConnect(p2)) {
                                findRotationAngle(s, s2);
                                sideLengthsSorted.remove(s);
                                sideLengthsSorted.remove(s2);
                                queue.add(p2);
                            }
                        }
                    }
                }
            }
        }
    }

    // Method that solves a puzzle by comparing the corners of each piece. It starts by identifying a piece
    // with a 90 deg angle and places this as a basis. Then it starts placing the pieces from this piece
    // adding the next pieces dynamically to a queue.
    // Written by Jacob
    public void solveByCorners() throws InterruptedException {
        Piece root = null;
        for (Piece p : boardPieces) {
            for(Piece p2 : controller.getGraph().depthFirstTraversal(p)) {
                controller.getGraph().removeEdge(p, p2);
            }
        }

        outerloop:
        for (Piece p : boardPieces) {
            for (Corner c : p.getVectorCorners()) {
                double epsilon = 0.00000000001;
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
        if(root == null) {
            root = boardPieces.get(0);
        }
        System.out.println("Start piece " + root.getPieceID());

        ArrayList<Piece> queue = new ArrayList<>();
        ArrayList<Integer> PossibleWrongIndexes = new ArrayList<>();
        ArrayList<ArrayList<Piece>> possibleMatchingPieces = new ArrayList<>();
        queue.add(root);
        for(int i = 0; i < queue.size(); i++) {
            ArrayList<Corner> vectorCornersP1 = queue.get(i).getVectorCorners();
            for (Piece p : boardPieces) {
                if(!queue.contains(p)) {

                    ArrayList<Corner> vectorCornersP2 = p.getVectorCorners();
                    ArrayList<Corner[]> matchingCorners = new ArrayList<>();

                    for (Corner c : vectorCornersP1) {
                        for (Corner c2 : vectorCornersP2) {
                            double epsilon = 0.00000000001;
                            if (c.getAngle() + epsilon >= c2.getAngle() &&
                                    c.getAngle() - epsilon <= c2.getAngle() &&
                                    !(c.getAngle() + epsilon >= Math.PI / 2.0 && c.getAngle() - epsilon <= Math.PI / 2.0)) {
                                matchingCorners.add(new Corner[]{c, c2});
                            }
                        }
                    }

                    for (Corner[] cs : matchingCorners) {
                        Double angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[0]);
                        double epsilon = 0.001;
                        while (!((angle1 + epsilon >= 0.0 && angle1 - epsilon <= 0.0)) && !Double.isNaN(angle1)) {
                            p.rotatePiece(angle1);
                            angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[0]);
                        }
                        Double angle2 = findAngleBetweenVectors(cs[0].getVectors()[1], cs[1].getVectors()[1]);

                        if (!(angle2 + epsilon >= 0.0 && angle2 - epsilon <= 0.0) && !Double.isNaN(angle2)) {
                            angle1 = findAngleBetweenVectors(cs[0].getVectors()[0], cs[1].getVectors()[1]);
                            while (!((angle1 + epsilon >= 0.0 && angle1 - epsilon <= 0.0)) && !Double.isNaN(angle1)) {
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

    // Method for finding the angle between two angles. It takes two SideLength objects as argument
    // and returns the angle between these side lengths.
    // Written by Jacob and Oscar
    public double findRotationAngle(SideLength s1, SideLength s2) throws InterruptedException {
        double angle;
        Double[][] s1Corners = s1.getCorners();
        Double[][] s2Corners = s2.getCorners();

        double dx;
        double dy;

        double[] tempVector1 = {s1Corners[0][0]-s1Corners[1][0], s1Corners[0][1]-s1Corners[1][1]};
        double[] tempVector2 = {s2Corners[0][0]-s2Corners[1][0], s2Corners[0][1]-s2Corners[1][1]};

        double dotProduct = (tempVector1[0] * tempVector2[0]) + (tempVector1[1] * tempVector2[1]);
        double det = tempVector1[0]*tempVector2[1]-tempVector1[1]*tempVector2[0];
        angle = Math.atan2(det, dotProduct);
        Controller.getInstance().getBoardPieces().get(s2.getPieceId()).rotatePiece(angle);
        Controller.getInstance().getBoardPieces().get(s2.getPieceId()).rotateNeighbours(angle);

        s1Corners = s1.getCorners();
        s2Corners = s2.getCorners();

        double vector1 = s1Corners[0][0]-s1Corners[1][0];
        double vector2 = s2Corners[0][0]-s2Corners[1][0];
        if(vector1+0.001 >= vector2 && vector1-0.001 <= vector2){
            dx = s1Corners[0][0] - s2Corners[0][0];
            dy = s1Corners[0][1] - s2Corners[0][1];
        } else {
            dx = s1Corners[0][0] - s2Corners[1][0];
            dy = s1Corners[0][1] - s2Corners[1][1];
        }
        Double[] s2Center = Controller.getInstance().getBoardPieces().get(s2.getPieceId()).getCenter();
        Controller.getInstance().getBoardPieces().get(s2.getPieceId()).movePiece(dx+s2Center[0],dy+s2Center[1]);

        if(!checkIntersection(s1, s2)){
            Controller.getInstance().getBoardPieces().get(s2.getPieceId()).rotatePiece(Math.PI);
            Controller.getInstance().getBoardPieces().get(s2.getPieceId()).rotateNeighbours(Math.PI);
            s1Corners = s1.getCorners();
            s2Corners = s2.getCorners();
            dx = s1Corners[0][0] - s2Corners[1][0];
            dy = s1Corners[0][1] - s2Corners[1][1];
            s2Center = Controller.getInstance().getBoardPieces().get(s2.getPieceId()).getCenter();
            Controller.getInstance().getBoardPieces().get(s2.getPieceId()).movePiece(dx+s2Center[0],dy+s2Center[1]);
        }
        sleep(Controller.getInstance().getSolveSpeed());
        return angle;
    }

    // Method that removes single instances of a side length in the sorted side lengths array.
    // It determines which side lengths exists as singulars (which are the edges of the board),
    // and removes them from the sorted side lengths array.
    // Written by Oscar
    private void removeSingleSideLengths(){
        double epsilon = 0.0000001;
        for(int i = 0; i < sideLengthsSorted.size(); i++){
            int previous = i-1;
            int next = i+1;
            if(i == 0){
                previous = sideLengthsSorted.size()-1;
            }
            if(i == sideLengthsSorted.size()-1){
                next = 0;
            }
            double pSide = sideLengthsSorted.get(previous).getValue();
            double cSideLow = sideLengthsSorted.get(i).getValue()-epsilon;
            double cSideHigh = sideLengthsSorted.get(i).getValue()+epsilon;
            double nSide = sideLengthsSorted.get(next).getValue();
            if(!(pSide <= cSideHigh && pSide >= cSideLow || cSideLow <= nSide && cSideHigh>=nSide)){
                System.out.println(sideLengthsSorted.get(previous).getValue());
                System.out.println(sideLengthsSorted.get(i).getValue());
                System.out.println(sideLengthsSorted.get(next).getValue());
                System.out.println();
                sideLengthsSorted.remove(i);
            }
        }
    }

    // Method that finds the orientation of 3 points from https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    // Input - 3 2d points
    // Output - 1 if the orientation is clockwise 2 if counterclockwise
    // Written by Oscar
    private int orientation(Double[] p1, Double[] p2, Double[] p3){
        double val = (p2[1]-p1[1])*(p3[0]-p2[0])-(p2[0]-p1[0])*(p3[1]-p2[1]);
        if(val > 0){
            return 1;
        }
        return 2;
    }

    // Method that checks if two lines intersect https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    // checks if the line between two piece centers intersect with their connecting sidelength
    // Input - two sidelengths
    // Output - True if the lines intersect false if not
    // Written by Oscar
    private boolean checkIntersection(SideLength s1, SideLength s2){
        Double[] p1 = {s1.getCorners()[0][0] + (s1.getCorners()[1][0] - s1.getCorners()[0][0]) * (-1000), s1.getCorners()[0][1] + (s1.getCorners()[1][1] - s1.getCorners()[0][1]) * (-1000)};
        Double[] p2 = {s1.getCorners()[1][0] + (s1.getCorners()[1][0] - s1.getCorners()[0][0]) * (1000), s1.getCorners()[1][1] + (s1.getCorners()[1][1] - s1.getCorners()[0][1]) * 1000};

        Double[][] linePoints = s1.getCorners();
        Double[] c1 = Controller.getInstance().getBoardPieces().get(s1.getPieceId()).getCenter();
        Double[] c2 = Controller.getInstance().getBoardPieces().get(s2.getPieceId()).getCenter();

        int o1 = orientation(p1, p2, c1);
        int o2 = orientation(p1, p2, c2);
        int o3 = orientation(c1, c2, p1);
        int o4 = orientation(c1, c2, p2);
        if(o1 != o2 && o3 != o4){
            System.out.println("----------------------INTERSECT--------------");
            return true;
        }
        System.out.println("---------------------NO INTERSECT-----------------------");
        return false;
    }

    // Method for starting the SolvePuzzle object running in a new thread when the class is initialized.
    // Written by Jacob & Oscar
    public void run(){
//        try {
        solveLockPuzzle();
//            if(solveBySideLength) {
//                solveBySideLengths();
//            } else {
//                solveByCorners();
//            }
//        } catch (InterruptedException e) {
//            System.out.println("Thread ended");
//        }
    }

    // Method that finds the angle given the coordinates of two vectors
    // Input - Two vectors given as Double[] values
    // Output - Returns the acute angle between the two vectors
    // Written by Jacob
    public static double findAngleBetweenVectors(Double[] vector1, Double[] vector2){
        double angle;

        double dotProduct = (vector1[0] * vector2[0]) + (vector1[1] * vector2[1]);
        double magnitude1 = Math.sqrt(Math.pow(vector1[0], 2) + Math.pow(vector1[1], 2));
        double magnitude2 = Math.sqrt(Math.pow(vector2[0], 2) + Math.pow(vector2[1], 2));
        double preCos = dotProduct/(magnitude1 * magnitude2);
        if(preCos <= -1.0){
            return Math.PI;
        }
        angle = Math.acos(preCos);
        return angle;
    }
    
    // Method that checks if a board has been solved by using a boundary box and checking piece sides inside this box 
    // if they have a partner
    // Input - List of the board pieces
    // Output - True if the board is solved otherwise false
    // Written by Oscar
    public boolean checkIfSolved(ArrayList<Piece> boardPieces){
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
                        System.out.println("Corners: (" + s1.getCorners()[0][0] + ", " + s1.getCorners()[1][0] + "); (" + s1.getCorners()[0][1] + ", " + s1.getCorners()[1][1] + ")");
                        System.out.println("Boundary: (" + boundaryBox[0] + ", " + boundaryBox[1] + "); (" + boundaryBox[2] + ", " + boundaryBox[3] + ")");
                        System.out.println("An inner side didn't have a matching side");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Method that find the boundary box using the lowest and highest x and y value
    // Input - List of the board pieces
    // Output - the top, bottom and side values of the boundary box in the format {xlow, xhigh, ylow, yhigh}
    // Written by Oscar
    private double[] findBoundaryBox(ArrayList<Piece> boardPieces){
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

    // Method that checks if the side is within or on the boundary box
    // Input - The coordinates of the boundary box and the coordinates of the side to be checked
    // Output - 0 if outside box, 1 if on the edge and 2 if inside the boundary box
    // Written by Oscar
    private int checkBoundaryBox(double[] boundary, SideLength s1) {
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
    
    // Method for comparing two SideLengths to determine if they have the same point quantity,
    // meaning they lie on top of each other.
    // Written by Oscar
    private boolean checkMatchingSides(SideLength s1, SideLength s2){
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

    // Auxiliary method for comparing two double values to test for equality given a margin for error of 10^(-1).
    // It takes two double values as input and returns a boolean value of true if they are equal and false if not.
    // Written by Oscar
    private boolean compareEpsilon(double value1, double value2){
        double epsilon = 0.1;
        if(value1 + epsilon >= value2 && value1 - epsilon <= value2){
            return true;
        }
        return false;
    }

    public void solveLockPuzzle() {
        ArrayList<PieceLock> sortedLocks = new ArrayList<>();

        double octagonCorner = 2.356;
        double epsilon = 0.001;

        ArrayList<Piece> boardPieces = controller.getBoardPieces();
        for(Piece p : boardPieces) {
            int count = 0;
            Corner startCorner = null;
            Corner endCorner = null;
            Corner prevCorner = null;
            boolean directionIn = false;
            int startIndex = 0;
            ArrayList<Corner> corners = p.getVectorCorners();
            while(corners.get(startIndex).getAngle() + epsilon >= octagonCorner && corners.get(startIndex).getAngle() - epsilon <= octagonCorner){
                startIndex++;
            }
            System.out.println();
            int index = startIndex;
            while(index != startIndex || startCorner == null) {

                if (corners.get(index).getAngle() + epsilon >= 2.356 && corners.get(index).getAngle() - epsilon <= 2.356) {
                    if (count == 0) {
                        startCorner = corners.get(index);
                        if(index != 0) { prevCorner = corners.get(index-1);}
                        else { prevCorner = corners.get(corners.size()-1);}
                        double prevLength = Math.sqrt(Math.pow(prevCorner.getCoordinates()[0]-p.getCenter()[0],2) + Math.pow(prevCorner.getCoordinates()[1]-p.getCenter()[1],2));
                        double thisLength = Math.sqrt(Math.pow(startCorner.getCoordinates()[0]-p.getCenter()[0],2) + Math.pow(startCorner.getCoordinates()[1]-p.getCenter()[1],2));
                        directionIn = prevLength > thisLength;
                    }
                    if (count == 7) {
                        endCorner = corners.get(index);
                        double sideLength = Math.sqrt(Math.pow(startCorner.getVectors()[1][0], 2) + Math.pow(startCorner.getVectors()[1][1], 2));
                        PieceLock pieceLock = new PieceLock(directionIn, sideLength, startCorner, endCorner);
                        p.addPieceLock(pieceLock);
                        sortedLocks.add(pieceLock);
                    }
                    count++;
                } else {
                    count = 0;
                }
                index++;
                if(index == corners.size()){index = 0;}
            }
        }

        for(PieceLock l : boardPieces.get(0).getPieceLocks()) {
            System.out.println("(x,y) = (" + l.getStartCorner().getCoordinates()[0] + ", " + l.getStartCorner().getCoordinates()[1] + ")");
            System.out.println("type " + l.isDirectionIn());
        }
    }
}
