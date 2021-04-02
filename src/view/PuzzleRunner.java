package view;

import controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.CreatePuzzleBoard;
import model.Piece;

import java.util.ArrayList;

public class PuzzleRunner extends Application {
	Controller controller = Controller.getInstance();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			//testRowColGeneration(stage);
			testPuzzleWithPolygons(stage);

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
		int width = controller.BOARD_SIZE[0];
		int height = controller.BOARD_SIZE[1];
		int rows = controller.ROWS;
		int columns = controller.COLUMNS;

		CreatePuzzleBoard createPuzzleBoard = new CreatePuzzleBoard(rows,columns,height,width);
		createPuzzleBoard.createOneRowPuzzle();

		Pane bg = new Pane();
		bg.setMaxWidth(width);
		bg.setMaxHeight(height);
		bg.setStyle("-fx-border-color: #000000");

		Group board = new Group();
		bg.getChildren().add(board);
		controller.setBoard(board);
		StackPane root = new StackPane();
		root.setPadding(new Insets(10,10,10,10));
		root.getChildren().add(bg);
		ArrayList<Piece> boardPieces = createPuzzleBoard.getBoardPieces();
		createPuzzleBoard.setAdjacentPieces();
		for(int i = 0; i < boardPieces.size(); i++) {
			board.getChildren().add(boardPieces.get(i));
		}

//		Temporary code for adding cm for each piece
//		for(Piece p : boardPieces) {
//
//			if(p.getPieceID() == 85) {
//				p.computeNearbyPieces();
//			}
//		}


		Scene boardScene = new Scene(root, width+600, height+200);
		stage.setScene((boardScene));
	}

}
