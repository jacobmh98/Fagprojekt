package controller;

import java.util.ArrayList;

import javafx.scene.Group;
import model.Piece;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> boardPieces = new ArrayList<Piece>();
	public final int[] BOARD_SIZE = {300, 300};
	public final int ROWS = 3;
	public final int COLUMNS = 3;
	Group board;
	public ArrayList<Piece> getBoardPieces() { return this.boardPieces; }
	public Group getBoard() { return this.board; }
	
	public Controller() {}

	public static Controller getInstance() {
		return controller;
	}

	public void setBoardPieces(ArrayList<Piece> boardPieces) {
		this.boardPieces = boardPieces;
	}

	public void setBoard(Group board) {
		this.board = board;
	}
}
