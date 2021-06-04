package model;

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
        Collections.sort(sideLengthsSorted);
    }

    public void runner() {
        for(int i = 0; i < sideLengthsSorted.size() - 1; i++) {
            System.out.println(sideLengthsSorted.get(i).getPieceId()+", "+
                                sideLengthsSorted.get(i).getLineId()+", "+
                                sideLengthsSorted.get(i).getValue()+", "+
                                sideLengthsSorted.get(i).getDx()+", "+
                                sideLengthsSorted.get(i).getDy());

            if(sideLengthsSorted.get(i).getValue().equals(sideLengthsSorted.get(i+1).getValue())) {
                double angle = findRotationAngle(sideLengthsSorted.get(i), sideLengthsSorted.get(i+1));
                System.out.println(angle);
            }
        }
    }

    public double findRotationAngle(SideLength s1, SideLength s2){
        double angle, m1, m2;
        m1 = s1.getDy()/s1.getDx();
        m2 = s2.getDy()/s1.getDx();
        if(m1 == m1 && m2 == m2){
            angle = Math.atan(Math.abs((m2-m1)/(1+m1*m2)));
        } else if (m1 == m1){
            angle = (Math.PI/2)-Math.atan(m2);
        } else if (m2 == m2){
            angle = (Math.PI/2)-Math.atan(m1);
        } else {
            angle = 0;
        }
        System.out.println("dx: (" + s1.getDx()+", "+s2.getDx() + ")");
        System.out.println("dy: (" + s1.getDy()+", "+s2.getDy() + ")");
        return angle;
    }
}
