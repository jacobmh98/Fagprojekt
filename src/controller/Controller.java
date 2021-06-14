package controller;

import model.Graph;
import model.Piece;

import java.util.ArrayList;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> boardPieces = new ArrayList<Piece>();
	private int[] BOARD_SIZE = new int[2];

	private int rows;
	private int columns;
	private Graph graph;
	private int solveSpeed;

	public Controller() {
		graph = new Graph();
	}

	public ArrayList<Piece> getBoardPieces() { return this.boardPieces; }
	public Graph getGraph() { return this.graph; }
	public static Controller getInstance() { return controller; }
	public int[] getBoardSize(){ return this.BOARD_SIZE; }
	public int getRows(){return rows;}
	public int getColumns(){return columns;}

	public void setBoardPieces(ArrayList<Piece> boardPieces) {
		this.boardPieces = boardPieces;
	}

	public void setBoardSize(int width, int height) { BOARD_SIZE[0] = width; BOARD_SIZE[1] = height; }

	public void setRows(int rows){this.rows = rows;}

	public void setColumns(int columns){this.columns = columns;}

	public void setSolveSpeed(int speed){solveSpeed = speed;}

	public int getSolveSpeed(){return solveSpeed;}

}
