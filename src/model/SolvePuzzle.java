package model;

import controller.Controller;
import javafx.geometry.Side;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class SolvePuzzle {
    ArrayList<Piece> boardPieces;
    ArrayList<SideLength> sideLengthsSorted = new ArrayList<>();

    public SolvePuzzle(ArrayList<Piece> boardPieces) {
        System.out.println("setting solve puzzle");
        this.boardPieces = boardPieces;

        for (Piece p : boardPieces) {
            for (SideLength l : p.getSideLengths()) {
                sideLengthsSorted.add(l);
            }
        }

        updateSolvePuzzle();

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

    public void updateSolvePuzzle() {
        Collections.sort(this.sideLengthsSorted);
    }

    public void runner() {
        Controller.getInstance().getSolvePuzzle().updateSolvePuzzle();
        for(SideLength l : sideLengthsSorted) {
            System.out.println(l.getPieceId()+", "+
                    l.getLineId()+", "+
                    l.getValue()+", ");
//                    l.getDx()+", "+
//                    l.getDy());
        }
        System.out.println();

        for(int i = 0; i < sideLengthsSorted.size() - 1; i++) {
            double epsilon = 0.001;
            if(sideLengthsSorted.get(i).getValue()+epsilon >= sideLengthsSorted.get(i+1).getValue() &&
                sideLengthsSorted.get(i).getValue()-epsilon <= sideLengthsSorted.get(i+1).getValue() &&
                sideLengthsSorted.get(i).getPieceId() != sideLengthsSorted.get(i+1).getPieceId()) {
                double angle = findRotationAngle(sideLengthsSorted.get(i), sideLengthsSorted.get(i+1));

                if(Controller.getInstance().getBoardPieces().get(sideLengthsSorted.get(i+1).getPieceId()).checkForConnect()) {
                    System.out.println("removing sidelength " + sideLengthsSorted.get(i).getValue());
                    Graph graph = Controller.getInstance().getGraph();
//                    graph.depthFirstTraversal(graph, Controller.getInstance().getBoardPieces().get(sideLengthsSorted.get(i).getPieceId()));

                    Set<Piece> connectedPieces = graph.depthFirstTraversal(graph, Controller.getInstance().getBoardPieces().get(sideLengthsSorted.get(i).getPieceId()));

                    for(Piece p : connectedPieces) {
                        System.out.println("Connected to: " + p.getPieceID());
                    }

                    sideLengthsSorted.remove(sideLengthsSorted.get(i));
                    sideLengthsSorted.remove(sideLengthsSorted.get(i));

                    break;
                } else {

                }

                System.out.println(angle);
            }
        }
    }

    public double findRotationAngle(SideLength s1, SideLength s2){
        double angle, m1, m2;
        System.out.println("\n equals sidelengths " + s1.getValue() + " = " + s2.getValue());
        Double[][] s1Corners = s1.getCorners();
        Double[][] s2Corners = s2.getCorners();

        double dx = s1Corners[0][0] - s2Corners[0][0];
        double dy = s1Corners[0][1] - s2Corners[0][1];

//        System.out.println("dx: " + dx);
//        System.out.println("dy: " + dy);

        double[] tempVector1 = {s1Corners[0][0]-s1Corners[1][0], s1Corners[0][1]-s1Corners[1][1]};
        double[] tempVector2 = {s2Corners[0][0]-s2Corners[1][0], s2Corners[0][1]-s2Corners[1][1]};

        double dotProduct = (tempVector1[0] * tempVector2[0]) + (tempVector1[1] * tempVector2[1]);
        //double magnitude1 = Math.sqrt(Math.pow(tempVector1[0], 2) + Math.pow(tempVector1[1], 2));
        //double magnitude2 = Math.sqrt(Math.pow(tempVector2[0], 2) + Math.pow(tempVector2[1], 2));

        //System.out.println("vector 1 <" + tempVector1[0] + ", " + tempVector1[1] +">");
        //System.out.println("vector 2 <" + tempVector2[0] + ", " + tempVector2[1] +">");
        //System.out.println("Angle: " + dotProduct/(magnitude1*magnitude2));

        //angle = Math.acos(dotProduct / (magnitude1*magnitude2));
        //if(!(angle == angle)){
        //    angle = 0;
        //}
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
//        System.out.println("dx: " + dx);
//        System.out.println("dy: " + dy);
        Double[] s2Center = Controller.getInstance().getBoardPieces().get(s2.getPieceId()).getCenter();
        Controller.getInstance().getBoardPieces().get(s2.getPieceId()).movePiece(dx+s2Center[0],dy+s2Center[1]);

//        m1 = s1.getDy()/s1.getDx();
//        m2 = s2.getDy()/s1.getDx();
//        if(m1 == m1 && m2 == m2){
//            angle = Math.atan(Math.abs((m2-m1)/(1+m1*m2)));
//        } else if (m1 == m1){
//            angle = (Math.PI/2)-Math.atan(m2);
//        } else if (m2 == m2){
//            angle = (Math.PI/2)-Math.atan(m1);
//        } else {
//            angle = 0;
//        }
//        System.out.println("dx: (" + s1.getDx()+", "+s2.getDx() + ")");
//        System.out.println("dy: (" + s1.getDy()+", "+s2.getDy() + ")");
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
        return angle;
    }

    private int orientation(Double[] p1, Double[] p2, Double[] p3){
        double val = (p2[1]-p1[1])*(p3[0]-p2[0])-(p2[0]-p1[0])*(p3[1]-p2[1]);
        if(val > 0){
            return 1;
        }
        return 2;
    }

    private boolean checkIntersection(SideLength s1, SideLength s2){
        Double[][] linePoints = s1.getCorners();
        Double[] p1 = new Double[2];
        Double[] p2 = new Double[2];
        Double[] c1 = Controller.getInstance().getBoardPieces().get(s1.getPieceId()).getCenter();
        Double[] c2 = Controller.getInstance().getBoardPieces().get(s2.getPieceId()).getCenter();
        for(int i = 0; i < 2; i++){
            p1[i] = linePoints[0][i];
            p2[i] = linePoints[1][i];
        } //p1p2 c1c2
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
}
