package view;

import controller.Controller;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.CreatePuzzleBoard;
import model.Piece;
import model.SolvePuzzle;
import model.VoronoiBoard;
//import org.delaunay.model.Triangle;
//import org.kynosarges.tektosyne.geometry.PointD;
//import org.kynosarges.tektosyne.geometry.VoronoiResults;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class PuzzleRunner extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// get instance of controller
		Controller controller = Controller.getInstance();

		try {
			GridPane pane = new GridPane();
			pane.setPadding(new Insets(10, 10, 30, 10));
			pane.setVgap(5);
			pane.setHgap(5);

			Label lblLogin = new Label("Fill out how many pieces you want to solve and the preferred board size");
			lblLogin.setFont(new Font(15.0));
			TextField txtNumberOfPieces = new TextField();
			txtNumberOfPieces.setPromptText("Number of pieces");
			txtNumberOfPieces.setPrefColumnCount(5);
			txtNumberOfPieces.setText("50");
			TextField txtWidth = new TextField();
			txtWidth.setPromptText("Puzzle width");
			txtWidth.setPrefColumnCount(5);
			txtWidth.setText("600");
			TextField txtHeight = new TextField();
			txtHeight.setPromptText("Puzzle height");
			txtHeight.setPrefColumnCount(5);
			txtHeight.setText("600");
			Button btnLogin = new Button("Initialize puzzle");
			HBox rbContainer = new HBox();
			ToggleGroup toggleGroup = new ToggleGroup();
			RadioButton rb1 = new RadioButton();
			rb1.setText("Solved");
			rb1.setToggleGroup(toggleGroup);
			rb1.setSelected(true);
			RadioButton rb2 = new RadioButton();
			rb2.setText("Shuffled");
			rb2.setToggleGroup(toggleGroup);
			rbContainer.getChildren().addAll(rb1, rb2);

			GridPane.setConstraints(lblLogin, 0, 0);
			GridPane.setConstraints(txtNumberOfPieces, 0, 1);
			GridPane.setConstraints(txtWidth, 0, 2);
			GridPane.setConstraints(txtHeight, 0, 3);
			GridPane.setConstraints(btnLogin, 0, 5);
			GridPane.setConstraints(rbContainer, 0, 4);

			pane.getChildren().addAll(lblLogin, txtNumberOfPieces, txtWidth, txtHeight, rbContainer, btnLogin);

			Scene scene = new Scene(pane);
			stage.setScene(scene);

			scene.setRoot(pane);

			stage.setTitle("Initialize Puzzle");
			stage.show();

			btnLogin.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					try {

						int points = Integer.parseInt(txtNumberOfPieces.getText());
						int width = Integer.parseInt(txtWidth.getText());
						int height = Integer.parseInt(txtHeight.getText());

						controller.setBoardSize(width, height);
						testTriangulation(stage, points, width, height);

						if(((RadioButton) toggleGroup.getSelectedToggle()).getText().equals("Shuffled")) {
							shufflePieces(controller.getBoardPieces());

						}
					} catch(NumberFormatException e) {
						System.out.println("error");
						Label lblError = new Label("Insert valid arguments");
						GridPane.setConstraints(lblError, 0, 6);
						pane.getChildren().add(lblError);
					} catch(Exception e) {

					}
				}
			});

			//testRowColGeneration(stage);
			//testPuzzleWithPolygons(stage);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void testRowColGeneration(Stage stage){
		Group board = new Group();
		CreatePuzzleBoard createPuzzleBoard = new CreatePuzzleBoard();
		createPuzzleBoard.createPuzzle();
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
		CreatePuzzleBoard createPuzzleBoard = new CreatePuzzleBoard();
		createPuzzleBoard.createPuzzle();
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

	public void testTriangulation(Stage stage, int points, int width, int height) throws Exception {
		HBox root = new HBox(8);
		StackPane pane = new StackPane();
		pane.setPadding(new Insets(10,10,10,20));
		Pane outerBoard = new Pane();
		outerBoard.setPrefWidth(width);
		outerBoard.setPrefHeight(height);
		outerBoard.setMaxWidth(width);
		outerBoard.setMaxHeight(height);
		outerBoard.setMinWidth(width);
		outerBoard.setMinHeight(height);
		Group board = new Group();
		outerBoard.getChildren().add(board);
		VoronoiBoard voronoi = new VoronoiBoard(points);
		Piece[] pieces = voronoi.getPieces();
		for(Piece p : pieces){
			board.getChildren().add(p);
		}
		ArrayList<Piece> pieceArray = new ArrayList<>();
		for(int i = 0; i < pieces.length; i++){
			pieceArray.add(pieces[i]);
		}
		Controller.getInstance().setBoardPieces(pieceArray);
		Controller.getInstance().setBoard(board);
		outerBoard.setStyle("-fx-border-color: black");
		Button solveBtn = new Button("Solve");
		solveBtn.setPadding(new Insets(5,10,5,10));
		pane.getChildren().add(outerBoard);
		root.getChildren().addAll(pane, solveBtn);
		Scene boardScene = new Scene(root, width+150, height + 20);
		stage.setScene(boardScene);

		solveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				SolvePuzzle solvePuzzle = Controller.getInstance().getSolvePuzzle();
				solvePuzzle.runner();
			}
		});
	}

	// method shuffling the pieces on the board
	public void shufflePieces(ArrayList<Piece> pieces) {
		for(Piece p : pieces) {
			p.shufflePiece();
		}
	}
}