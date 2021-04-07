package view;

import controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.CreatePuzzleBoard;
import model.Piece;
import model.VoronoiBoard;
import org.delaunay.model.Triangle;
import org.kynosarges.tektosyne.geometry.PointD;
import org.kynosarges.tektosyne.geometry.VoronoiResults;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class PuzzleRunner extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group group = new Group();
		// get instance of controller
		Controller controller = Controller.getInstance();
		controller.setGroup(group);

		// Setting example board size

		// set example piece 1,2
		Double[] corners1 = {
				150.0, 150.0,
				250.0, 250.0,
				150.0, 350.0,
		};
		Integer pieceID1 = 0;
		
		Double[] corners2 = {
				50.0, 50.0,
				350.0, 50.0,
				350.0, 250.0,
				50.0, 250.0,
				150.0, 150.0,
		};
		Integer pieceID2 = 1;
		
		// generate the piece
		//controller.generatePiece(pieceID1, corners1);
		
		// draw the pieces
		//controller.drawPieces();
		
		try {
			Scene scene = new Scene(group, controller.BOARD_SIZE[0], controller.BOARD_SIZE[1]);
			stage.setScene(scene);
			//testRowColGeneration(stage);
			//testPuzzleWithPolygons(stage);
			testTriangulation(stage);

			stage.setTitle("Puzzle");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void testRowColGeneration(Stage stage){
		Group board = new Group();
		CreatePuzzleBoard createPuzzleBoard = new CreatePuzzleBoard(5,5,500,500);
		createPuzzleBoard.createOneRowPuzzle();
		ArrayList<ArrayList<Double>> columnX = createPuzzleBoard.getColumnX();
		ArrayList<ArrayList<Double>> columnY = createPuzzleBoard.getColumnY();
		ArrayList<ArrayList<Double>> rowX = createPuzzleBoard.getRowX();
		ArrayList<ArrayList<Double>> rowY = createPuzzleBoard.getRowY();
		//Create Column lines
		for(int i = 0; i < columnX.size(); i++) {
			for(int j = 0; j < columnX.get(i).size()-1; j++){
				Line columnLine = new Line(columnX.get(i).get(j)+10,columnY.get(i).get(j)+10,columnX.get(i).get(j+1)+10,columnY.get(i).get(j+1)+10);
				board.getChildren().add(columnLine);
			}
		}
		//Create row lines
		for(int i = 0; i < rowX.size(); i++){
			for(int j = 0; j < rowX.get(i).size()-1; j++){
				Line rowLine = new Line(rowX.get(i).get(j)+10,rowY.get(i).get(j)+10,rowX.get(i).get(j+1)+10,rowY.get(i).get(j+1)+10);
				board.getChildren().add(rowLine);
			}
		}
		//Create corner points
		ArrayList<Double> pieceX = createPuzzleBoard.getPieceX();
		ArrayList<Double> pieceY = createPuzzleBoard.getPieceY();
		for(int i = 0; i < pieceX.size(); i++){
			Circle circle = new Circle((pieceX.get(i))+10, (pieceY.get(i))+10, 3);
			board.getChildren().add(circle);
		}
		//create straight lines at 100 both y and x
		board.getChildren().add(new Line(110,10,110,510));
		board.getChildren().add(new Line(10,110,510,110));

		Scene boardScene = new Scene(board,600,600);
		stage.setScene(boardScene);
	}

	public void testPuzzleWithPolygons(Stage stage){
		int width = 800;
		int height = 800;
		CreatePuzzleBoard createPuzzleBoard = new CreatePuzzleBoard(5,5,height,width);
		createPuzzleBoard.createOneRowPuzzle();
		Group board = new Group();
		StackPane root = new StackPane();
		root.setPadding(new Insets(10,10,10,10));
		root.getChildren().add(board);
		ArrayList<Piece> boardPieces = createPuzzleBoard.getBoardPieces();
		for(int i = 0; i < boardPieces.size(); i++){
			board.getChildren().add(boardPieces.get(i));
		}
		Scene boardScene = new Scene(root, height+20, width+20);
		stage.setScene((boardScene));
	}

	public void testTriangulation(Stage stage) throws Exception {
		StackPane root = new StackPane();
		root.setPadding(new Insets(10,10,10,10));
		Group board = new Group();
		int points = 10;
		VoronoiBoard voronoi = new VoronoiBoard(points,800,800);
		Piece[] pieces = voronoi.getPieces();
		for(Piece p : pieces){
			board.getChildren().add(p);
		}

		root.getChildren().add(board);
		Scene boardScene = new Scene(root, 820, 820);
		stage.setScene(boardScene);
	}

}
