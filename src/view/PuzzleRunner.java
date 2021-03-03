package view;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
			stage.setTitle("Puzzle");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
