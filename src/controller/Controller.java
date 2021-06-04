package controller;

import java.util.ArrayList;

import javafx.scene.Group;
import model.Graph;
import model.Piece;
import model.SolvePuzzle;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> boardPieces = new ArrayList<Piece>();
	private int[] BOARD_SIZE = new int[2];
	public final int ROWS = 3;
	public final int COLUMNS = 3;
	private Group board;
	private Graph graph;
	private SolvePuzzle solvePuzzle;

	public ArrayList<Piece> getBoardPieces() { return this.boardPieces; }
	public Group getBoard() { return this.board; }
	public Graph getGraph() { return this.graph; }
	public static Controller getInstance() { return controller; }
	public int[] getBoardSize(){ return this.BOARD_SIZE; }

	public Controller() {
		graph = new Graph();
	}

	public void setBoardPieces(ArrayList<Piece> boardPieces) {
		this.boardPieces = boardPieces;
		setSolvePuzzle();
	}

	public void setSolvePuzzle() {
		System.out.println("setting solve puzzle");
		solvePuzzle = new SolvePuzzle(Controller.getInstance().getBoardPieces());
	}

	public SolvePuzzle getSolvePuzzle() {return this.solvePuzzle; };

	public void setBoard(Group board) {
		this.board = board;
	}

	public void setBoardSize(int width, int height) { BOARD_SIZE[0] = width; BOARD_SIZE[1] = height; }
}
