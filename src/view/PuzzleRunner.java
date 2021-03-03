package view;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.CreatePuzzleBoard;

import java.util.ArrayList;

public class PuzzleRunner extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group group = new Group();
		// get instance of controller
		Controller controller = new Controller(group);
		
		// set example piece 1,2
		Double[] corners1 = {
				50.0, 50.0,
				150.0, 150.0,
				50.0, 250.0,
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
		controller.generatePiece(pieceID1, corners1);
		
		// draw the pieces
		controller.drawPieces();
		
		try {
			Scene scene = new Scene(group, 500, 500);
			stage.setScene(scene);
			testOneRowPuzzle(stage);
			stage.setTitle("Puzzle");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void testOneRowPuzzle(Stage stage){
		Group board = new Group();
		CreatePuzzleBoard createPuzzleBoard = new CreatePuzzleBoard(5,5,500,500);
		createPuzzleBoard.createOneRowPuzzle();
		ArrayList<ArrayList<Double>> columnX = createPuzzleBoard.getColumnX();
		ArrayList<ArrayList<Double>> columnY = createPuzzleBoard.getColumnY();
		ArrayList<ArrayList<Double>> rowX = createPuzzleBoard.getRowX();
		ArrayList<ArrayList<Double>> rowY = createPuzzleBoard.getRowY();
		for(int i = 0; i < columnX.size(); i++) {
			for(int j = 0; j < columnX.get(i).size()-1; j++){
				Line columnLine = new Line(columnX.get(i).get(j)+10,columnY.get(i).get(j)+10,columnX.get(i).get(j+1)+10,columnY.get(i).get(j+1)+10);
				board.getChildren().add(columnLine);
			}
		}
		for(int i = 0; i < rowX.size(); i++){
			for(int j = 0; j < rowX.get(i).size()-1; j++){
				Line rowLine = new Line(rowX.get(i).get(j)+10,rowY.get(i).get(j)+10,rowX.get(i).get(j+1)+10,rowY.get(i).get(j+1)+10);
				board.getChildren().add(rowLine);
			}
		}
		Scene boardScene = new Scene(board,600,600);
		stage.setScene(boardScene);
	}


}
