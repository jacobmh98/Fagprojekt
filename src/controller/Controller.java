package controller;

import model.Graph;
import model.Piece;
import view.PuzzleRunner;

import java.util.ArrayList;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> boardPieces = new ArrayList<Piece>();
	private final int[] BOARD_SIZE = new int[2];
	private int rows;
	private int columns;
	private Graph graph;
	private int solveSpeed;
	private PuzzleRunner puzzleRunner;

	public Controller() {
		graph = new Graph();
	}

	// Getter methods for the fields in the Controller
	// Written by Jacob & Oscar
	public ArrayList<Piece> getBoardPieces() { return this.boardPieces; }
	public Graph getGraph() { return this.graph; }
	public static Controller getInstance() { return controller; }
	public int[] getBoardSize(){ return this.BOARD_SIZE; }
	public int getRows(){return rows;}
	public int getColumns(){return columns;}
	public int getSolveSpeed(){return solveSpeed;}

	// Setter methods for the fields in the Controller
	// Written by Jacob & Oscar
	public void setPuzzleRunner(PuzzleRunner puzzleRunner){this.puzzleRunner = puzzleRunner;}
	public void setBoardPieces(ArrayList<Piece> boardPieces) {
		this.boardPieces = boardPieces;
	}
	public void setBoardSize(int width, int height) { BOARD_SIZE[0] = width; BOARD_SIZE[1] = height; }
	public void setRows(int rows){this.rows = rows;}
	public void setColumns(int columns){this.columns = columns;}
	public void setSolveSpeed(int speed){solveSpeed = speed;}
	public void setSolvedText(String text){ puzzleRunner.setIsSolvedLabelText(text);}
}
