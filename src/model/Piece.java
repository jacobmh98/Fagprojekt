package model;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import view.PuzzleRunner;

import java.awt.*;
import java.util.ArrayList;

public class Piece extends Polygon {
	private Integer pieceID;
	private Double[] corners;
	private Double[] center = new Double[2];
	private Double rotation = 0.0; // 2*PI
	private double prevY = 0.0;
	Controller controller = Controller.getInstance();
	private ArrayList<Piece> adjacentPieces = new ArrayList<Piece>();
	private ArrayList<Piece> nearbyPieces = new ArrayList<Piece>();
	private ArrayList<Piece> snappedPieces = new ArrayList<Piece>();

	public Double getRotation() { return this.rotation; }
	public Double[] getCenter() { return this.center; }
	public Integer getPieceID() { return this.pieceID; }
	public ArrayList<Piece> getAdjacentPieces() { return this.adjacentPieces; }
	
	// Constructor for piece
	public Piece(Integer pieceID, Double[] corners) {
		this.pieceID = pieceID;
		this.corners = corners;

		computeCenter();
		
		// Methods inherited from JavafX Polygon class
		this.getPoints().addAll(this.corners);
		this.setStroke(Color.BLACK);
		this.setFill(Color.WHITE);
		this.setCursor(Cursor.HAND);

		updatePiece();
	}
	public void updatePiece() {
		this.getPoints().removeAll();
		this.getPoints().setAll(this.corners);

		this.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {

				mouseEvent.setDragDetect(true);
				Piece.this.setMouseTransparent(true);
			}
		});

		this.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				Piece.this.setMouseTransparent(false);
				mouseEvent.setDragDetect(true);
				computeCenter();

				double deltaX = mouseEvent.getX();
				double deltaY = mouseEvent.getY();
				int direction = 0;

				if(deltaY > Piece.this.prevY) {
					direction = -1;
				} else {
					direction = 1;
				}
				Piece.this.prevY = deltaY;

				computeNearbyPieces();
				intersect();

				if(mouseEvent.getButton() == MouseButton.PRIMARY) {
					movePiece(deltaX, deltaY);
				} else {
					if(direction == 1) {
						rotatePiece(Math.PI/50);
					} else {
						rotatePiece(-Math.PI/50);
					}
				}
			}
		});
	}

	// Getter method for corners
	public Double[] getCorners() {
		return this.corners;
	}

	// Check intersect with other pieces nearby
	public void intersect() {
		for(Piece p : this.nearbyPieces) {
			if(this.intersects(p.getBoundsInLocal())) {

				if(adjacentPieces.contains(p)) {
					double rotationMin = p.getRotation() - (Math.PI/8.0);
					double rotationMax = p.getRotation() + (Math.PI/8.0);

					if(this.getRotation() > rotationMin && this.getRotation() < rotationMax) {
						boolean snap = false;
						for(int i = 0; i < corners.length; i++) {
							for(int k = 0; k < p.getCorners().length; k++) {
								double cornerP = p.getCorners()[k];

								double cornerPMin = cornerP - cornerP*1/10.0;
								double cornerPMax = cornerP + cornerP*1/10.0;

								if(corners[i] > cornerPMin && corners[i] < cornerPMax) {
									double cornerPNext;
									double cornerPPrev;
									double cornerNext;
									double cornerPrev;
									double cornerPNextMin;
									double cornerPNextMax;
									double cornerPPrevMin;
									double cornerPPrevMax;

									if(k < p.getCorners().length-1){
										cornerPNext = p.getCorners()[k+1];
									} else {
										cornerPNext = p.getCorners()[0];
									}
									if(k > 0) {
										cornerPPrev = p.getCorners()[k-1];
									} else {
										// k = 0
										cornerPPrev = p.getCorners()[p.getCorners().length-1];
									}

									if(i < corners.length-1) {
										cornerNext = corners[i+1];
									} else {
										cornerNext = corners[0];
									}
									if(i > 0) {
										cornerPrev = corners[i-1];
									} else {
										// i = 0
										cornerPrev = corners[corners.length-1];
									}

									cornerPNextMin = cornerPNext - cornerPNext*1/10.0;
									cornerPNextMax = cornerPNext + cornerPNext*1/10.0;
									cornerPPrevMin = cornerPPrev - cornerPPrev*1/10.0;
									cornerPPrevMax = cornerPPrev + cornerPPrev*1/10.0;

									if(cornerNext > cornerPNextMin && cornerNext < cornerPNextMax) {
										snap = true;
										System.out.println("NEW SNAP *********************");
										System.out.println(cornerPMin + " < " + corners[i] + " < " + cornerPMax);
										System.out.println(cornerPNextMin + " < " + cornerNext + " < " + cornerPNextMax);
									}

									if(cornerPrev > cornerPPrevMin && cornerPrev < cornerPPrevMax) {
										snap = true;
										System.out.println("NEW SNAP *********************");
										System.out.println(cornerPMin + " < " + corners[i] + " < " + cornerPMax);
										System.out.println(cornerPNextMin + " < " + cornerNext + " < " + cornerPNextMax);
									}
								}
							}
						}

						if(snap) {
							System.out.println(this.getPieceID() + ", " + p.getPieceID());
							//Snap piece from the top
							if(this.getPieceID() - 1 == p.getPieceID()) {
								System.out.println("snap top");
							}
							// Snap piece from the bottom
							else if(this.getPieceID()+1 == p.getPieceID()) {
								System.out.println("snap bottom");
							}
							// Snap piece from the right
							else if(p.getPieceID() + controller.ROWS == this.getPieceID()) {
								System.out.println("snap right");
							}
							// Snap piece from the left
							else if(p.getPieceID() - controller.ROWS == this.getPieceID()) {
								System.out.println("snap left");
							}
						}
					}
				}

				// System.out.println(this.getPieceID() + " intersects " + p.getPieceID());
			}
		}
	}

	// Method returning nearby pieces within some radius
	public void computeNearbyPieces() {
		ArrayList<Piece> nearbyPieces = new ArrayList<Piece>();
		double radius = (2*(controller.BOARD_SIZE[0] + controller.BOARD_SIZE[1])) / (controller.ROWS + controller.COLUMNS);

		for(Piece p : controller.getBoardPieces()) {
			Double[] p2Center = p.getCenter();
			double d = Math.sqrt(Math.pow(center[0] - p2Center[0], 2) +
								 Math.pow(center[1] - p2Center[1], 2));

//			System.out.print(p.getPieceID()+", ");

			if(d < radius) {
//				Circle c = new Circle();
//				c.setCenterX(center[0]);
//				c.setCenterY(center[1]);
//				c.setRadius(radius);
//				c.setStroke(Color.RED);
//				c.setFill(Color.TRANSPARENT);
//				controller.getBoard().getChildren().addAll(c);
//				p.setFill(Color.LIGHTBLUE);

				nearbyPieces.add(p);
			}
		}

//		System.out.print("\n");

		this.nearbyPieces = nearbyPieces;
	}

	// compute the center of the polygon
	public void computeCenter() {
		Double sumX = 0.0;
		Double sumY = 0.0;

		for(int i = 0; i < corners.length; i++) {
			if(i % 2 == 0) {
				sumX += corners[i];
			} else {
				sumY += corners[i];
			}
		}

		center[0] = (1/(double) (corners.length/2.0)) * sumX;
		center[1] = (1/(double) (corners.length/2.0)) * sumY;
	}

	// set corner coordinates
	public void shufflePiece() {
		double distToCenterX =  controller.BOARD_SIZE[0]/2.0 - center[0];
		double distToCenterY = controller.BOARD_SIZE[1]/2.0 - center[1];
		double[] updateCorners = new double[corners.length];
		double seed1 = Math.random() * controller.BOARD_SIZE[0] - controller.BOARD_SIZE[0];
		double seed2 = Math.random() * controller.BOARD_SIZE[1] - controller.BOARD_SIZE[1];
		double seed3 = Math.random() * 2*Math.PI;

//		wait with random rotation too difficult ;)
//		this.rotatePiece(seed3);

		for(int i = 0; i < corners.length; i++) {
			double updateCornerX = corners[i] + distToCenterX + seed1;
			double updateCornerY = corners[i] + distToCenterY + seed2;

			// Taking borders into consideration
			//if(updateCornerX < 0 || updateCornerY < 0 || updateCornerX > controller.BOARD_SIZE[0] || updateCornerY > controller.BOARD_SIZE[1]) {
			//	update = false;
			//} else {
			if(i % 2 == 0)
				updateCorners[i] = updateCornerX;
			else
				updateCorners[i] = updateCornerY;
			//}
		}
		for(int i = 0; i < corners.length; i++) {
			corners[i] = updateCorners[i];
		}
		updatePiece();
	}

	// Method for moving piece
	public void movePiece(double deltaX, double deltaY) {
		boolean update = true;
		Double[] updateCorners = new Double[corners.length];

		for(int i = 0; i < corners.length; i++) {
			double updateCornerX = corners[i] + (deltaX - center[0]);
			double updateCornerY = corners[i] + (deltaY - center[1]);

			// Taking borders into consideration
			//if(updateCornerX < 0 || updateCornerY < 0 || updateCornerX > controller.BOARD_SIZE[0] || updateCornerY > controller.BOARD_SIZE[1]) {
			//	update = false;
			//} else {
				if(i % 2 == 0)
					updateCorners[i] = updateCornerX;
				else
					updateCorners[i] = updateCornerY;
			//}
		}

		if(update) {
			for(int i = 0; i < corners.length; i++) {
				corners[i] = updateCorners[i];
			}
			updatePiece();
		}
	}


	// Method for rotating piece
	public void rotatePiece(Double angle) {
		setRotation(angle);
		Integer n = corners.length / 2;		
		
		// Variable C contains n pairs of the centroid
		Double[] centroid = new Double[2];
		Double[][] C = new Double[2][n];
		
		// Variable for rotation matrix
		Double[][] R = {
					{Math.cos(angle), Math.sin(angle) * (-1.0)},
					{Math.sin(angle), Math.cos(angle)}
				};
		
		// Variable contains old coordinates
		Double[][] pOld = new Double[2][n];
		
		// Variable contains rotated coordinates
		Double[][] pNew = new Double[2][n];
		
		// Represent the corners as a matrix
		int pos = 0;
		Double sumX = 0.0;
		Double sumY = 0.0;
		for(int col = 0; col < n; col++) {
			pOld[0][col] = corners[pos];
			sumX += corners[pos];
			pos++;
			pOld[1][col] = corners[pos];
			sumY += corners[pos];
			pos++;
		}
		
		// Compute the centroid of the polygon
		centroid[0] = (1/(double) n) * sumX;
		centroid[1] = (1/(double) n) * sumY;
		for(int col = 0; col < n ; col++) {
			C[0][col] = centroid[0];
			C[1][col] = centroid[1];
		}
		
		pNew = addition(dot(R, subtract(pOld,C,n)), C,n);
		Double[] newCorners = convertFrom2D(pNew);
		boolean update = true;

		// Taking borders into consideration
		/*for(Double coord : convertFrom2D(pNew)) {
			if(coord < 0 || coord > 800) {
				update = false;
			}
		}*/

		if(update) {
			corners = newCorners;
			Piece.this.getPoints().removeAll();
			Piece.this.getPoints().setAll(corners);
		}
	}

	public void setRotation(Double angle) {
		this.rotation += angle;
		if(this.rotation > 2*Math.PI) {
			this.rotation = 0.0;
		}
		if(this.rotation < 0) {
			this.rotation = 2*Math.PI;
		}
	}

	// Method for converting from 2d to 1d
	public Double[] convertFrom2D(Double[][] m2d) {
		Double[] temp = new Double[m2d[0].length * 2];

		int pos = 0;
		for(int i = 0; i < m2d[0].length; i++) {
			temp[pos] = m2d[0][i];
			pos++;
			temp[pos] = m2d[1][i];
			pos++;
		}

		return temp;
	}

	// Method for matrix subtraction
	private Double[][] subtract(Double[][] matrix1, Double[][] matrix2, int n) {
		Double[][] sum = new Double[2][n];
	
		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < n; c++) {
				sum[r][c] = matrix1[r][c] - matrix2[r][c];
			}
		}
		
		return sum;
	}
	
	// Method for matrix multiplication
	private Double[][] dot(Double[][] matrix1, Double[][] matrix2) {
		int row = matrix1.length;
		int column = matrix2[0].length;
		
		Double[][] result = new Double[row][column];
		
		for(int r = 0; r < row; r++) {
			for(int c = 0; c < column; c++) {
				result[r][c] = 0.0;
				for(int i = 0; i < matrix2.length; i++) {
					result[r][c] += matrix1[r][i] * matrix2[i][c];
				}
			}
		}
		
		return result;
	}
	
	// Method for matrix addition
	private Double[][] addition(Double[][] matrix1, Double[][] matrix2, int n) {
		Double[][] sum = new Double[2][n];
	
		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < n; c++) {
				sum[r][c] = matrix1[r][c] + matrix2[r][c];
			}
		}
		
		return sum;
	}
	
	// Method for printing matrices
	private void print2dArray(Double[][] matrix) {
		for(int r = 0; r < 2; r++) {
			for (int c = 0; c < matrix[0].length; c++) {
				System.out.print(Math.round(matrix[r][c]*100.0)/100.0+"\t");
			}
			System.out.println();
		}
	}

	public void addAdjacentPiece(Piece p) {
		adjacentPieces.add(p);
	}
}
