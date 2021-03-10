package controller;

import java.util.ArrayList;

import javafx.scene.Group;
import model.Piece;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	Group group;
	
	public Controller() {}

	public static Controller getInstance() {
		return controller;
	}

	public void setGroup(Group g) {
		this.group = g;
	}
	
	// Method for drawing pieces on stage
	public void drawPieces() {
		for(Piece piece : pieces) {
			this.group.getChildren().add(piece);
		}
	}

	// Method for drawing individual pieces
	public void drawPiece(Piece p) {
		int index = this.group.getChildren().indexOf(p);
		if(index != -1) {
			this.group.getChildren().remove(p);
		}
	}
	
	// Method for generating new piece
	public void generatePiece(Integer pieceID, Double[] corners) {
		Piece p = new Piece(pieceID, corners);
		pieces.add(p);
		
		p.setRotation(Math.PI/3.0);
	}
}
