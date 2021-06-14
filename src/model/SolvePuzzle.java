package model;

import controller.Controller;
import java.util.*;

public class SolvePuzzle extends Thread{
    private ArrayList<Piece> boardPieces;
    private ArrayList<SideLength> sideLengthsSorted = new ArrayList<>();

    private Controller controller = Controller.getInstance();
//    boolean firstConnected = false;
//    private int[] idConnected;

    public SolvePuzzle(ArrayList<Piece> boardPieces) {
        System.out.println("setting solve puzzle");
        this.boardPieces = boardPieces;
//        idConnected = new int[boardPieces.size()];
        for (Piece p : boardPieces) {
            for (SideLength l : p.getSideLengths()) {
                sideLengthsSorted.add(l);
            }
        }

        sortSideLength();
//        removeSingleSideLengths();
    }

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

    public void runner() throws InterruptedException {
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

                    Double angleVertical1 = SolvePuzzleJSON.findAngleBetweenVectors(vector1, tempVector);
                    Double angleVertical2 = SolvePuzzleJSON.findAngleBetweenVectors(vector2, tempVector);

                    if(angleVertical1 < angleVertical2) {
                        while (!((angleVertical1 + epsilon >= 0.0 && angleVertical1 - epsilon <= 0.0)) && !Double.isNaN(angleVertical1)) {
                            root.rotatePiece(angleVertical1);
                            angleVertical1 = SolvePuzzleJSON.findAngleBetweenVectors(c.getVectors()[0], tempVector);
                        }
                    } else {
                        root.rotatePiece(angleVertical2);
                        while (!((angleVertical2 + epsilon >= 0.0 && angleVertical2 - epsilon <= 0.0)) && !Double.isNaN(angleVertical2)) {
                            root.rotatePiece(angleVertical2);
                            angleVertical2 = SolvePuzzleJSON.findAngleBetweenVectors(c.getVectors()[1], tempVector);
                        }
                    }

                    vector1 = c.getVectors()[0];
                    vector2 = c.getVectors()[1];

//                    System.out.println("Vector 1 " + vector1[0] + ", " + vector1[1]);
//                    System.out.println("Vector 2 " + vector2[0] + ", " + vector2[1]);
//                    System.out.println("angle1 between vertical " + angleVertical1);
//                    System.out.println("angle2 between vertical " + angleVertical2);

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
                            Piece p2 = controller.getBoardPieces().get(s2.getPieceId());

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

        // OLD METHOD DUNNO IF WE STILL NEED IT
//        for(int k = 0; k < boardPieces.size()*2; k++){
//            sortSideLength();
//            for (SideLength l : sideLengthsSorted) {
//                System.out.println(l.getPieceId() + ", " +
//                        l.getLineId() + ", " +
//                        l.getValue() + ", ");
//            }
//            System.out.println();
//
//            for (int i = 0; i < sideLengthsSorted.size() - 1; i++) {
//                double epsilon = 0.001;
//                SideLength currentSideLength = sideLengthsSorted.get(i);
//                SideLength nextSideLength = sideLengthsSorted.get(i + 1);
//
//                Piece currentPiece = Controller.getInstance().getBoardPieces().get(currentSideLength.getPieceId());
//                Piece nextPiece = Controller.getInstance().getBoardPieces().get(nextSideLength.getPieceId());
//
//                if (currentSideLength.getValue() + epsilon >= nextSideLength.getValue() &&
//                        currentSideLength.getValue() - epsilon <= nextSideLength.getValue() &&
//                        currentSideLength.getPieceId() != nextSideLength.getPieceId() &&
//                        (idConnected[currentSideLength.getPieceId()] == 1 || idConnected[nextSideLength.getPieceId()] == 1 || !firstConnected)) {
//                    double angle;
//                    if (idConnected[currentSideLength.getPieceId()] == 1) {
//                        angle = findRotationAngle(currentSideLength, nextSideLength);
//                        idConnected[nextSideLength.getPieceId()] = 1;
//                    } else {
//                        angle = findRotationAngle(nextSideLength, currentSideLength);
//                        idConnected[nextSideLength.getPieceId()] = 1;
//                        idConnected[currentSideLength.getPieceId()] = 1;
//                    }
//
//                    firstConnected = true;
//                    if (currentPiece.checkForConnect()) {
//                        Graph graph = Controller.getInstance().getGraph();
//
//                        Set<Piece> connectedPiecesGraph = graph.depthFirstTraversal(currentPiece);
//                        ArrayList<Integer> connectedPieces = new ArrayList<Integer>();
//                        for (Piece p : connectedPiecesGraph) {
//                            connectedPieces.add(p.getPieceID());
//                            System.out.print(p.getPieceID() + ", ");
//                        }
//
//                        for (int j = 0; j < sideLengthsSorted.size() - 1; j++) {
//                            if (sideLengthsSorted.get(j).getValue() + epsilon >= sideLengthsSorted.get(j + 1).getValue() &&
//                                    sideLengthsSorted.get(j).getValue() - epsilon <= sideLengthsSorted.get(j + 1).getValue()) {
//
//                                if (connectedPieces.contains(sideLengthsSorted.get(j).getPieceId()) &&
//                                        connectedPieces.contains(sideLengthsSorted.get(j + 1).getPieceId())) {
//
//                                    System.out.println("remove sidelength " + sideLengthsSorted.get(j).getValue());
//                                    sideLengthsSorted.remove(j);
//                                    sideLengthsSorted.remove(j);
//                                }
//                            }
//                        }
//
//                        break;
//                    }
//
//                    System.out.println("angle: " + angle);
//                }
//            }
//        }
//
//        int rand = (int) Math.random()*boardPieces.size();
//        Piece p = boardPieces.get(rand);
//        double cmSumX = p.getCenter()[0];
//        double cmSumY = p.getCenter()[1];
//        double width = Controller.getInstance().getBoardSize()[0];
//        double height = Controller.getInstance().getBoardSize()[1];
//        Set<Piece> solution = Controller.getInstance().getGraph().depthFirstTraversal(p);
//        for(Piece pi : solution) {
//            cmSumX += pi.getCenter()[0];
//            cmSumY += pi.getCenter()[1];
//        }
//        double cmX = cmSumX/solution.size();
//        double cmY = cmSumY/solution.size();
//
//        double dx = width/2.0 - cmX;
//        double dy = height/2.0 - cmY;
//        p.movePiece(dx + p.getCenter()[0], dy + p.getCenter()[1]);
    }

    public double findRotationAngle(SideLength s1, SideLength s2) throws InterruptedException {
        double angle;
        System.out.println("\n equals sidelengths " + s1.getValue() + " = " + s2.getValue());
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

    private int orientation(Double[] p1, Double[] p2, Double[] p3){
        double val = (p2[1]-p1[1])*(p3[0]-p2[0])-(p2[0]-p1[0])*(p3[1]-p2[1]);
        if(val > 0){
            return 1;
        }
        return 2;
    }

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

    public void run(){
        try {
            runner();
        } catch (InterruptedException e) {
            System.out.println("Thread ended");
        }
    }
}
