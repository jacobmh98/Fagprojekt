package view;

import controller.Controller;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.util.ArrayList;

public class PuzzleRunner extends Application {
	private Controller controller = Controller.getInstance();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			GridPane pane = new GridPane();
			pane.setPadding(new Insets(10, 10, 30, 10));
			pane.setVgap(5);
			pane.setHgap(5);

			HBox boardTypeSelectPane = new HBox();
			boardTypeSelectPane.setSpacing(50);
			ToggleGroup BoardTypeToggleGroup = new ToggleGroup();

			RadioButton VoronoiRB = new RadioButton();
			VoronoiRB.setText("Generate Voronoi board");
			VoronoiRB.setSelected(true);
			VoronoiRB.setToggleGroup(BoardTypeToggleGroup);

			RadioButton jsonRB = new RadioButton();
			jsonRB.setText("JSon import");
			jsonRB.setToggleGroup(BoardTypeToggleGroup);

			RadioButton rowColRB = new RadioButton();
			rowColRB.setText("Generate NxM board");
			rowColRB.setToggleGroup(BoardTypeToggleGroup);
			boardTypeSelectPane.getChildren().addAll(VoronoiRB, jsonRB, rowColRB);

			Label startTextLabel = new Label("Choose a puzzle and fill the necessary fields");
			startTextLabel.setFont(new Font(15.0));

			Button selectFileButton = new Button("Select JSon File");
			selectFileButton.setVisible(false);
			final File[] selectedFile = {null};
			Label lblSelectedFile = new Label("No file selected");
			lblSelectedFile.setVisible(false);

			TextField txtNumberOfPieces = new TextField();
			txtNumberOfPieces.setPromptText("Number of pieces");
			txtNumberOfPieces.setPrefColumnCount(5);
			TextField txtWidth = new TextField();
			txtWidth.setPromptText("Puzzle width");
			txtWidth.setPrefColumnCount(5);
			TextField txtHeight = new TextField();
			txtHeight.setPromptText("Puzzle height");
			txtHeight.setPrefColumnCount(5);

			TextField rowField = new TextField();
			TextField colField = new TextField();
			rowField.setPromptText("Rows");
			colField.setPromptText("Columns");
			TextField widthField = new TextField();
			TextField heightField = new TextField();
			widthField.setPromptText("Width");
			heightField.setPromptText("Height");
			rowField.setVisible(false);
			colField.setVisible(false);
			widthField.setVisible(false);
			heightField.setVisible(false);

			Button initializeButton = new Button("Initialize puzzle");
			HBox startStatePane = new HBox();
			ToggleGroup startStateToggleGroup = new ToggleGroup();
			RadioButton solvedRB = new RadioButton();
			solvedRB.setText("Solved");
			solvedRB.setToggleGroup(startStateToggleGroup);
			solvedRB.setSelected(true);
			RadioButton shuffledRB = new RadioButton();
			shuffledRB.setText("Shuffled");
			shuffledRB.setToggleGroup(startStateToggleGroup);
			startStatePane.getChildren().addAll(solvedRB, shuffledRB);

			GridPane.setConstraints(startTextLabel, 0, 0);
			GridPane.setConstraints(boardTypeSelectPane, 0, 1);
			GridPane.setConstraints(selectFileButton, 0, 2);
			GridPane.setConstraints(lblSelectedFile, 0, 3);
			GridPane.setConstraints(txtNumberOfPieces, 0, 2);
			GridPane.setConstraints(txtWidth, 0, 3);
			GridPane.setConstraints(txtHeight, 0, 4);
			GridPane.setConstraints(startStatePane, 0, 6);
			GridPane.setConstraints(initializeButton, 0, 7);
			GridPane.setConstraints(rowField,0,2);
			GridPane.setConstraints(colField,0,3);
			GridPane.setConstraints(widthField,0,4);
			GridPane.setConstraints(heightField,0,5);


			pane.getChildren().addAll(startTextLabel, boardTypeSelectPane, selectFileButton, lblSelectedFile, txtNumberOfPieces, txtWidth, txtHeight, startStatePane, initializeButton, rowField, colField, widthField, heightField);

			Scene scene = new Scene(pane);
			stage.setScene(scene);

			scene.setRoot(pane);

			stage.setTitle("Initialize Puzzle");
			stage.show();

			VoronoiRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasSelected, Boolean isSelected) {
					if(isSelected) {
						txtHeight.setVisible(true);
						txtWidth.setVisible(true);
						txtNumberOfPieces.setVisible(true);
					}
					if(wasSelected) {
						txtHeight.setVisible(false);
						txtWidth.setVisible(false);
						txtNumberOfPieces.setVisible(false);
					}
				}
			});

			jsonRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasSelected, Boolean isSelected) {
					if(isSelected){
						widthField.setVisible(true);
						heightField.setVisible(true);
						selectFileButton.setVisible(true);
						lblSelectedFile.setVisible(true);
					}
					if(wasSelected){
						widthField.setVisible(false);
						heightField.setVisible(false);
						selectFileButton.setVisible(false);
						lblSelectedFile.setVisible(false);
					}
				}
			});

			rowColRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasSelected, Boolean isSelected) {
					if(isSelected){
						rowField.setVisible(true);
						colField.setVisible(true);
						heightField.setVisible(true);
						widthField.setVisible(true);
					}
					if(wasSelected){
						rowField.setVisible(false);
						colField.setVisible(false);
						heightField.setVisible(false);
						widthField.setVisible(false);
					}
				}
			});

			selectFileButton.setOnAction(new EventHandler<ActionEvent>() {
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

			initializeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					try {
						int width;
						int height;

						if(VoronoiRB.isSelected()) {
							width = Integer.parseInt(txtWidth.getText());
							height = Integer.parseInt(txtHeight.getText());
							int points = Integer.parseInt(txtNumberOfPieces.getText());
							controller.setBoardSize(width, height);
							generateVoronoiBoard(stage, points, width, height);
						} else if(jsonRB.isSelected()){
							if(selectedFile != null) {
								width = Integer.parseInt(widthField.getText());
								height = Integer.parseInt(heightField.getText());

								generateBoardFromJson(stage, width, height, selectedFile[0].getAbsolutePath());
							}
						} else {
							int rows = Integer.parseInt(rowField.getText());
							int cols = Integer.parseInt(colField.getText());
							width = Integer.parseInt(widthField.getText());
							height = Integer.parseInt(heightField.getText());
							generateRowColBoard(stage, rows, cols, width, height);
						}

						if(((RadioButton) startStateToggleGroup.getSelectedToggle()).getText().equals("Shuffled")) {
							shufflePieces(controller.getBoardPieces());

						}
					} catch(NumberFormatException e) {
						System.out.println("Argument error");
						Label lblError = new Label("Insert valid arguments");
						GridPane.setConstraints(lblError, 0, 8);
						pane.getChildren().add(lblError);
					} catch(Exception e) {

					}
				}
			});


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void generateVoronoiBoard(Stage stage, int points, int width, int height) throws Exception {
		controller.setBoardSize(width, height);
		VoronoiBoard voronoi = new VoronoiBoard(points);
		Piece[] pieces = voronoi.getPieces();

		ArrayList<Piece> pieceArray = new ArrayList<>();
		for(int i = 0; i < pieces.length; i++){
			pieceArray.add(pieces[i]);
		}
		generateBoardScene(stage, pieceArray, width, height, true);

	}

	public void generateBoardFromJson(Stage stage, int width, int height, String filename) throws Exception {
		controller.setBoardSize(width, height);
		JsonImport jsonImport = new JsonImport();
		ArrayList<Piece> boardPieces = jsonImport.readJson(filename);
		generateBoardScene(stage, boardPieces, width, height, false);
	}

	public void generateRowColBoard(Stage stage, int rows, int cols, int width, int height){
		controller.setBoardSize(width, height);
		controller.setRows(rows);
		controller.setColumns(cols);
		CreatePuzzleBoard puzzleBoard = new CreatePuzzleBoard();
		puzzleBoard.createPuzzle();
		ArrayList<Piece> boardPieces = puzzleBoard.getBoardPieces();
		generateBoardScene(stage, boardPieces, width, height, false);
	}

	public void generateBoardScene(Stage stage, ArrayList<Piece> boardPieces, int width, int height, boolean voronoi) {
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

		for(Piece p : boardPieces){
			board.getChildren().add(p);
		}
		controller.setBoardPieces(boardPieces);
		outerBoard.setStyle("-fx-border-color: black");
		Label solveLbl = new Label("Solve the puzzle");
		solveLbl.getStyleClass().add("headerlbl");
		Button solveBtn = new Button("Solve Puzzle");
		root.setPadding(new Insets(10,10,10,10));
		pane.getChildren().add(outerBoard);
		VBox rightSide = new VBox(8);

		Slider speedSlider = new Slider(0,100,1);
		speedSlider.setValue(30);
		controller.setSolveSpeed((int)speedSlider.getValue());
		Label speedLabel = new Label("Solve speed");
		Label currentSpeedLabel = new Label("Current: " + controller.getSolveSpeed());

		rightSide.getChildren().addAll(solveLbl, solveBtn,speedLabel, speedSlider, currentSpeedLabel);
		root.getChildren().addAll(pane, rightSide);

		Scene boardScene = new Scene(root, width+300, height + 20);
		boardScene.getStylesheets().add(PuzzleRunner.class.getResource("styles.css").toExternalForm());
		stage.setScene(boardScene);

		speedSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observableValue, Number OldValue, Number newValue) {
						controller.setSolveSpeed(newValue.intValue());
						currentSpeedLabel.setText("Current: " + controller.getSolveSpeed());
					}
				}
		);
		if(voronoi) {
			solveBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					Thread t = new SolvePuzzle(true);
					t.setDaemon(true);
					t.start();
				}
			});
		} else {
			solveBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					Thread t = new SolvePuzzle(false);
					t.setDaemon(true);
					t.start();
				}
			});
		}
	}

	// method shuffling the pieces on the board
	public void shufflePieces(ArrayList<Piece> pieces) {
		for(Piece p : pieces) {
			p.shufflePiece();
		}
	}
}