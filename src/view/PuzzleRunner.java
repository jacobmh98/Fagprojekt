package view;

import controller.Controller;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
//import org.delaunay.model.Triangle;
//import org.kynosarges.tektosyne.geometry.PointD;
//import org.kynosarges.tektosyne.geometry.VoronoiResults;


import java.awt.geom.Point2D;
import java.io.File;
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

			HBox rbContainer1 = new HBox();
			rbContainer1.setSpacing(50);
			ToggleGroup toggleGroup1 = new ToggleGroup();
			RadioButton tg1Rb1 = new RadioButton();
			RadioButton tg1Rb2 = new RadioButton();
			RadioButton tg1Rb3 = new RadioButton();
			tg1Rb1.setText("Generate Voronoi board");
			tg1Rb1.setSelected(true);
			tg1Rb1.setToggleGroup(toggleGroup1);
			tg1Rb2.setText("JSon import");
			tg1Rb2.setToggleGroup(toggleGroup1);
			tg1Rb3.setText("Generate NxM board");
			tg1Rb3.setToggleGroup(toggleGroup1);
			rbContainer1.getChildren().addAll(tg1Rb1, tg1Rb2, tg1Rb3);

			Label lblLogin = new Label("Choose a puzzle and fill out how many pieces you want to solve and the preferred board size");
			lblLogin.setFont(new Font(15.0));

			Button selectFile = new Button("Select JSon File");
			selectFile.setVisible(false);
			final File[] selectedFile = {null};
			Label lblSelectedFile = new Label("No file selected");
			lblSelectedFile.setVisible(false);

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

			TextField rowField = new TextField();
			TextField colField = new TextField();
			rowField.setPromptText("Rows");
			colField.setPromptText("Columns");
			TextField widthField2 = new TextField();
			TextField heightField2 = new TextField();
			widthField2.setPromptText("Width");
			heightField2.setPromptText("Height");
			rowField.setVisible(false);
			colField.setVisible(false);
			widthField2.setVisible(false);
			heightField2.setVisible(false);

			Button btnLogin = new Button("Initialize puzzle");
			HBox rbContainer = new HBox();
			ToggleGroup toggleGroup2 = new ToggleGroup();
			RadioButton rb1 = new RadioButton();
			rb1.setText("Solved");
			rb1.setToggleGroup(toggleGroup2);
			rb1.setSelected(true);
			RadioButton rb2 = new RadioButton();
			rb2.setText("Shuffled");
			rb2.setToggleGroup(toggleGroup2);
			rbContainer.getChildren().addAll(rb1, rb2);

			GridPane.setConstraints(lblLogin, 0, 0);
			GridPane.setConstraints(rbContainer1, 0, 1);
			GridPane.setConstraints(selectFile, 0, 2);
			GridPane.setConstraints(lblSelectedFile, 0, 3);
			GridPane.setConstraints(txtNumberOfPieces, 0, 2);
			GridPane.setConstraints(txtWidth, 0, 3);
			GridPane.setConstraints(txtHeight, 0, 4);
			GridPane.setConstraints(rbContainer, 0, 6);
			GridPane.setConstraints(btnLogin, 0, 7);
			GridPane.setConstraints(rowField,0,2);
			GridPane.setConstraints(colField,0,3);
			GridPane.setConstraints(widthField2,0,4);
			GridPane.setConstraints(heightField2,0,5);


			pane.getChildren().addAll(lblLogin, rbContainer1, selectFile, lblSelectedFile, txtNumberOfPieces, txtWidth, txtHeight, rbContainer, btnLogin, rowField, colField, widthField2, heightField2);

			Scene scene = new Scene(pane);
			stage.setScene(scene);

			scene.setRoot(pane);

			stage.setTitle("Initialize Puzzle");
			stage.show();

			tg1Rb1.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasSelected, Boolean isSelected) {
					if(isSelected) {
						txtHeight.setVisible(true);
						txtWidth.setVisible(true);
						txtNumberOfPieces.setVisible(true);
						//rbContainer.setVisible(true);
					}
					if(wasSelected) {
						txtHeight.setVisible(false);
						txtWidth.setVisible(false);
						txtNumberOfPieces.setVisible(false);
						//rbContainer.setVisible(false);
					}
				}
			});

			tg1Rb2.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasSelected, Boolean isSelected) {
					if(isSelected){
						selectFile.setVisible(true);
						lblSelectedFile.setVisible(true);
					}
					if(wasSelected){
						selectFile.setVisible(false);
						lblSelectedFile.setVisible(false);
					}
				}
			});

			tg1Rb3.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasSelected, Boolean isSelected) {
					if(isSelected){
						rowField.setVisible(true);
						colField.setVisible(true);
						heightField2.setVisible(true);
						widthField2.setVisible(true);
						//rbContainer.setVisible(true);
					}
					if(wasSelected){
						rowField.setVisible(false);
						colField.setVisible(false);
						heightField2.setVisible(false);
						widthField2.setVisible(false);
						//rbContainer.setVisible(false);
					}
				}
			});

			selectFile.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select JSON file");

					selectedFile[0] = fileChooser.showOpenDialog(stage);
					if(selectedFile[0] != null) {
						lblSelectedFile.setText("File: " + selectedFile[0].getName());
					}
				}
			});

			btnLogin.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					try {

						int points = Integer.parseInt(txtNumberOfPieces.getText());
						int width = Integer.parseInt(txtWidth.getText());
						int height = Integer.parseInt(txtHeight.getText());

						controller.setBoardSize(width, height);
						if(tg1Rb1.isSelected()) {
							testTriangulation(stage, points, width, height);
						} else if(tg1Rb2.isSelected()){
							if(selectedFile != null) {
								generateBoardFromJson(stage, width, height, selectedFile[0].getAbsolutePath());
							}
						} else {
							int rows = Integer.parseInt(rowField.getText());
							int cols = Integer.parseInt(colField.getText());
							width = Integer.parseInt(widthField2.getText());
							height = Integer.parseInt(heightField2.getText());
							generateRowColBoard(stage, rows, cols, width, height);
						}

						if(((RadioButton) toggleGroup2.getSelectedToggle()).getText().equals("Shuffled")) {
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
		pane.setPadding(new Insets(0,10,10,20));
		Pane outerBoard = new Pane();
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
		Label solveLbl = new Label("Solve the puzzle");
		solveLbl.getStyleClass().add("headerlbl");
		Button solveBtn = new Button("Solve Puzzle");
		root.setPadding(new Insets(10,10,10,10));
		pane.getChildren().add(outerBoard);
		VBox rightSide = new VBox(8);
		rightSide.getChildren().addAll(solveLbl, solveBtn);
		root.getChildren().addAll(pane, rightSide);
		Scene boardScene = new Scene(root, width+300, height + 20);
		boardScene.getStylesheets().add(PuzzleRunner.class.getResource("styles.css").toExternalForm());
		stage.setScene(boardScene);

		Controller.getInstance().setSolvePuzzle();

		solveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				SolvePuzzle solvePuzzle = Controller.getInstance().getSolvePuzzle();
				//solvePuzzle.runner();
				Thread t = new SolvePuzzle(Controller.getInstance().getBoardPieces());
				t.start();
			}
		});
	}

	public void generateBoardFromJson(Stage stage, int width, int height, String filename) throws Exception {
		HBox root = new HBox(8);

		StackPane pane = new StackPane();
		pane.setPadding(new Insets(0,10,10,20));
		Pane outerBoard = new Pane();
		outerBoard.setMaxWidth(width);
		outerBoard.setMaxHeight(height);
		outerBoard.setMinWidth(width);
		outerBoard.setMinHeight(height);
		Group board = new Group();
		outerBoard.getChildren().add(board);

		JsonImport jsonImport = new JsonImport();
		ArrayList<Piece> boardPieces = jsonImport.readJson(filename);

		for(Piece p : boardPieces){
			System.out.println(p.getPieceID());
			board.getChildren().add(p);
		}


		Controller.getInstance().setBoardPieces(boardPieces);
		Controller.getInstance().setBoard(board);
		outerBoard.setStyle("-fx-border-color: black");
		Label solveLbl = new Label("Solve the puzzle");
		solveLbl.getStyleClass().add("headerlbl");
		Button solveBtn = new Button("Solve Puzzle");
		Slider speedSlider = new Slider(0,100,1);
		speedSlider.setValue(30);
		Controller.getInstance().setSolveSpeed((int)speedSlider.getValue());
		Label speedLabel = new Label("Solve speed");
		Label currentSpeedLabel = new Label("Current: " + Controller.getInstance().getSolveSpeed());
		root.setPadding(new Insets(10,10,10,10));
		pane.getChildren().add(outerBoard);
		VBox rightSide = new VBox(8);
		rightSide.getChildren().addAll(solveLbl, solveBtn,speedLabel, speedSlider, currentSpeedLabel);
		root.getChildren().addAll(pane, rightSide);
		Scene boardScene = new Scene(root, width+300, height + 20);
		boardScene.getStylesheets().add(PuzzleRunner.class.getResource("styles.css").toExternalForm());
		stage.setScene(boardScene);

		speedSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observableValue, Number OldValue, Number newValue) {
						Controller.getInstance().setSolveSpeed(newValue.intValue());
						currentSpeedLabel.setText("Current: " + Controller.getInstance().getSolveSpeed());
					}
				}
		);

		solveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
//				try {
//					SolvePuzzleJSON.runner(boardPieces);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				Thread t = new SolvePuzzleJSON();
				t.setDaemon(true);
				t.start();
			}
		});
	}

	public void generateRowColBoard(Stage stage, int rows, int cols, int width, int height){
		Controller controller = Controller.getInstance();
		HBox root = new HBox(8);

		StackPane pane = new StackPane();
		pane.setPadding(new Insets(0,10,10,20));
		Pane outerBoard = new Pane();
		outerBoard.setMaxWidth(width);
		outerBoard.setMaxHeight(height);
		outerBoard.setMinWidth(width);
		outerBoard.setMinHeight(height);
		Group board = new Group();
		outerBoard.getChildren().add(board);

		controller.setBoardSize(width, height);
		controller.setRows(rows);
		controller.setColumns(cols);
		CreatePuzzleBoard puzzleBoard = new CreatePuzzleBoard();
		puzzleBoard.createPuzzle();
		ArrayList<Piece> boardPieces = puzzleBoard.getBoardPieces();
		for(Piece p : boardPieces){
			board.getChildren().add(p);
		}
		controller.setBoard(board);
		controller.setBoardPieces(boardPieces);

		outerBoard.setStyle("-fx-border-color: black");
		Label solveLbl = new Label("Solve the puzzle");
		solveLbl.getStyleClass().add("headerlbl");
		Button solveBtn = new Button("Solve Puzzle");
		Slider speedSlider = new Slider(0,100,1);
		speedSlider.setValue(30);
		Controller.getInstance().setSolveSpeed((int)speedSlider.getValue());
		Label speedLabel = new Label("Solve speed");
		Label currentSpeedLabel = new Label("Current: " + Controller.getInstance().getSolveSpeed());
		root.setPadding(new Insets(10,10,10,10));
		pane.getChildren().add(outerBoard);
		VBox rightSide = new VBox(8);
		rightSide.getChildren().addAll(solveLbl, solveBtn,speedLabel, speedSlider, currentSpeedLabel);
		root.getChildren().addAll(pane, rightSide);
		Scene boardScene = new Scene(root, width+300, height + 20);
		boardScene.getStylesheets().add(PuzzleRunner.class.getResource("styles.css").toExternalForm());
		stage.setScene(boardScene);

		speedSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observableValue, Number OldValue, Number newValue) {
						Controller.getInstance().setSolveSpeed(newValue.intValue());
						currentSpeedLabel.setText("Current: " + Controller.getInstance().getSolveSpeed());
					}
				}
		);

		solveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
//				try {
//					SolvePuzzleJSON.runner(boardPieces);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				Thread t = new SolvePuzzleJSON();
				t.setDaemon(true);
				t.start();
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