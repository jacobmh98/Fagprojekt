package model;

import controller.Controller;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class SolvePuzzle {
    ArrayList<Piece> boardPieces;
    ArrayList<SideLength> sideLengthsSorted = new ArrayList<>();

    public SolvePuzzle(ArrayList<Piece> boardPieces) {
        this.boardPieces = boardPieces;

        for (Piece p : boardPieces) {
            for (SideLength l : p.getSideLengths()) {
                sideLengthsSorted.add(l);
            }
        }

        updateSolvePuzzle();

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
            if(Math.round(sideLengthsSorted.get(i).getValue()) == Math.round(sideLengthsSorted.get(i+1).getValue()) &&
               sideLengthsSorted.get(i).getPieceId() != sideLengthsSorted.get(i+1).getPieceId()) {
                double angle = findRotationAngle(sideLengthsSorted.get(i), sideLengthsSorted.get(i+1));
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

        System.out.println("dx: " + dx);
        System.out.println("dy: " + dy);

        double[] tempVector1 = {s1Corners[1][0], s1Corners[1][1]};
        double[] tempVector2 = {s2Corners[1][0] + dx, s2Corners[1][1] + dy};

        double dotProduct = (tempVector1[0] * tempVector2[0]) + (tempVector1[1] * tempVector2[1]);
        double magnitude1 = Math.sqrt(Math.pow(tempVector1[0], 2) + Math.pow(tempVector1[1], 2));
        double magnitude2 = Math.sqrt(Math.pow(tempVector2[0], 2) + Math.pow(tempVector2[1], 2));

        System.out.println("vector 1 <" + tempVector1[0] + ", " + tempVector1[1] +">");
        System.out.println("vector 2 <" + tempVector2[0] + ", " + tempVector2[1] +">");

        angle = Math.acos(dotProduct / (magnitude1*magnitude2));
        Controller.getInstance().getBoardPieces().get(s2.getPieceId()).rotatePiece(angle);

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
        return angle;
    }
}
