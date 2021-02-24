package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PuzzleRunner extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 500, 500);
			stage.setScene(scene);
			stage.setTitle("Puzzle");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
