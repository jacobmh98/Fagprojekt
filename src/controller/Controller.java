package controller;

import java.util.ArrayList;

import javafx.scene.Group;
import model.Graph;
import model.Piece;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> boardPieces = new ArrayList<Piece>();
	public final int[] BOARD_SIZE = {500, 500};
	public final int ROWS = 3;
	public final int COLUMNS = 3;
	private Group board;
	private Graph graph;

	public ArrayList<Piece> getBoardPieces() { return this.boardPieces; }
	public Group getBoard() { return this.board; }
	public Graph getGraph() { return this.graph; }
	public static Controller getInstance() { return controller; }

	public Controller() {
		graph = new Graph();
	}

	public void setBoardPieces(ArrayList<Piece> boardPieces) {
		this.boardPieces = boardPieces;
	}

	public void setBoard(Group board) {
		this.board = board;
	}
}
