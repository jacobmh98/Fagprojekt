package controller;

import java.util.ArrayList;

import javafx.scene.Group;
import model.Piece;

public class Controller {
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	Group group;
	
	public Controller(Group g) {
		this.group = g;
	}
	
	// Method for drawing pieces on stage
	public void drawPieces() {
		for(Piece piece : pieces) {
			this.group.getChildren().add(piece);
		}
	}
	
	// Method for generating new piece
	public void generatePiece(Integer pieceID, Double[] corners) {
		Piece p = new Piece(pieceID, corners);
		pieces.add(p);
		
		p.setRotation(Math.PI/2.0);
	}
}
