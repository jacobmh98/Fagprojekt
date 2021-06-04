package model;

import java.util.*;

public class SolvePuzzle {
    ArrayList<Piece> boardPieces;
    ArrayList<SideLength> sideLengths = new ArrayList<>();

    public SolvePuzzle(ArrayList<Piece> boardPieces) {
        this.boardPieces = boardPieces;

        for (Piece p : boardPieces) {
            for (SideLength l : p.getSideLengths()) {
                sideLengths.add(l);
            }
        }

        Collections.sort(sideLengths);

        for(int i = 0; i < sideLengths.size() -1; i++) {

            if(sideLengths.get(i).getValue().equals(sideLengths.get(i+1).getValue())) {
//                solve(sideLengths.get(i), sideLengths.get(i+1));
            }
        }
    }

    public void solve(SideLength s1, SideLength s2) {
        System.out.println(s1.getPieceID() + ", " + s1.getValue() + ", " + s1.getDx() + ", " + s1.getDy());
        System.out.println(s2.getPieceID() + ", " + s2.getValue() + ", " + s2.getDx() + ", " + s2.getDy());
    }
}
