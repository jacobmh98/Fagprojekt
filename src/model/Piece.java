package model;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.HashMap;

public class Piece extends Polygon {
	private Integer pieceID;
	private Double[] corners;
	private Double[] center = new Double[2];
	private Double rotation = 0.0;
	private double prevY = 0.0;
	Controller controller = Controller.getInstance();
	private ArrayList<Piece> nearbyPieces = new ArrayList<Piece>();
	private Graph graph = controller.getGraph();
	private HashMap<Piece, Double[]> adjacentPieces = new HashMap<Piece, Double[]>();

	public Double getRotation() { return this.rotation; }
	public Double[] getCenter() { return this.center; }
	public Integer getPieceID() { return this.pieceID; }
	public HashMap<Piece, Double[]> getAdjacentPieces() { return this.adjacentPieces; }
	public Double[] getCorners() {
		return this.corners;
	}
	
	// Constructor for piece
	public Piece(Integer pieceID, Double[] corners) {
		this.pieceID = pieceID;
		this.corners = corners;
		graph.addVertex(this);
		
		// Methods inherited from JavafX Polygon class
		this.getPoints().addAll(this.corners);
		this.setStroke(Color.BLACK);
		this.setFill(Color.LIGHTBLUE);
		this.setCursor(Cursor.HAND);

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

				double deltaX = mouseEvent.getX();
				double deltaY = mouseEvent.getY();
				int direction = 0;

				if(deltaY > Piece.this.prevY) {
					direction = -1;
				} else {
					direction = 1;
				}
				Piece.this.prevY = deltaY;

//				computeNearbyPieces();
//				intersect();

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

		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				computeNearbyPieces();
				checkForConnect();
			}
		});

		updatePiece();
	}
	public void updatePiece() {
		this.getPoints().removeAll();
		this.getPoints().setAll(this.corners);
		this.computeCenter();
	}

	public void checkForConnect() {
		for(Piece p : nearbyPieces) {
			Double[] temp = adjacentPieces.get(p);
			if(temp != null) {
				System.out.println("\nPiece " + this.pieceID + " should connect to " + p.getPieceID());
				System.out.println("Distance between cm's should be: " + temp[0] + ", " + temp[1]);
				Double dx = p.getCenter()[0] - this.getCenter()[0];
				Double dy = p.getCenter()[1] - this.getCenter()[1];

				Double dxMin = temp[0] - 5.0;
				Double dxMax = temp[0] + 5.0;
				Double dyMin = temp[1] - 5.0;
				Double dyMax = temp[1] + 5.0;

				System.out.println("Distance is currently: " + dx + ", " + dy);
//				System.out.println(dxMin + " < " + dx + " < " + dxMax);
//				System.out.println(dyMin + " < " + dy + " < " + dyMax);

				if(dx > dxMin && dx < dxMax && dy > dyMin && dy < dyMax) {
					System.out.println("Snap accepted");
					if(!graph.depthFirstTraversal(graph, p).contains(this)) {
						Double moveDx = dx - temp[0];
						Double moveDy = dy - temp[1];
						snapPiece(p, moveDx, moveDy);
					}
				}
			}
		}
	}

/*
	// Check intersect with other pieces nearby
	public void intersect() {
		for(Piece p : this.nearbyPieces) {
			if(this.intersects(p.getBoundsInLocal())) {
				if(adjacentPieces.containsKey(p)) {
					double rotationMin = p.getRotation() - (Math.PI/8.0);
					double rotationMax = p.getRotation() + (Math.PI/8.0);

					if(this.getRotation() > rotationMin && this.getRotation() < rotationMax) {
						boolean snap = false;
						double deltaX = p.getCenter()[0] - this.center[0];
						double deltaY = p.getCenter()[1] - this.center[1];
						Double[] distances = this.adjacentPieces.get(p);

						double deltaXMin = Math.abs(deltaX * (9/10.0));
						double deltaXMax = Math.abs(deltaX * (11/10.0));
						double deltaYMin = Math.abs(deltaY * (9/10.0));
						double deltaYMax = Math.abs(deltaY * (11/10.0));

						if(deltaXMin < Math.abs(distances[0]) && deltaXMax >  Math.abs(distances[0]) &&
							deltaYMin <  Math.abs(distances[1]) && deltaYMax >  Math.abs(distances[1])) {
//							System.out.println("SNAP BITCH");
//							System.out.println(deltaXMin + " < " + Math.abs(distances[0]) +  " < " + deltaXMax);
//							System.out.println(deltaYMin + " < " + Math.abs(distances[1]) + " < " + deltaYMax);
//							snap = true;

							if(!graph.depthFirstTraversal(graph, p).contains(this)) {
								snapPiece(p);
							}
						}

						if(snap) {

						}
					}
				}
			}
		}
	}
*/
	private void snapPiece(Piece p, Double dx, Double dy) {
		graph.addEdge(this, p);
		Double[] updateCorners = new Double[this.getCorners().length];

		for(int i = 0; i < this.getCorners().length; i++) {

			if(i % 2 == 0) {
				updateCorners[i] = this.getCorners()[i] + dx;
			} else {
				updateCorners[i] = this.getCorners()[i] + dy;
			}
		}

		setCorners(updateCorners);
		updatePiece();
	}
	// Method returning nearby pieces within some radius
	public void computeNearbyPieces() {
		ArrayList<Piece> nearbyPieces = new ArrayList<Piece>();
		double radius = (2*(controller.BOARD_SIZE[0] + controller.BOARD_SIZE[1])) / (controller.ROWS + controller.COLUMNS);

		for(Piece p : controller.getBoardPieces()) {
			Double[] p2Center = p.getCenter();
			double d = Math.sqrt(Math.pow(center[0] - p2Center[0], 2) +
								 Math.pow(center[1] - p2Center[1], 2));

			if(d < radius) {
//				Circle c = new Circle();
//				c.setCenterX(center[0]);
//				c.setCenterY(center[1]);
//				c.setRadius(radius);
//				c.setStroke(Color.RED);
//				c.setFill(Color.TRANSPARENT);
//				controller.getBoard().getChildren().addAll(c);
//				p.setFill(Color.LIGHTBLUE);

				if(this.getPieceID() != p.getPieceID()) {
					nearbyPieces.add(p);
				}
			}
		}
		System.out.print("Nearby pieces for " + this.getPieceID() + ": ");
		for(Piece p : nearbyPieces) {
			System.out.print(p.getPieceID()+", ");
		}
		System.out.print("\nAdjacent pieces for " + this.getPieceID() + ": ");
		for(Piece p : adjacentPieces.keySet()) {
			System.out.print(p.getPieceID()+", ");
		}

		System.out.println();

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

		center[0] = (1/ (corners.length/2.0)) * sumX;
		center[1] = (1/ (corners.length/2.0)) * sumY;

//		Circle c = new Circle();
//		c.setFill(Color.BLACK);
//		c.setCenterX(center[0]);
//		c.setCenterY(center[1]);
//		if(controller.getBoard() != null) {
//			controller.getBoard().getChildren().add(c);
//		}
	}

	// set corner coordinates
	public void setCorners(Double[] updateCorners) {
		for(int i = 0; i < this.corners.length; i++) {
			this.corners[i] = updateCorners[i];
		}
	}

	// set corner coordinates
//	public void shufflePiece() {
//		double distToCenterX =  controller.BOARD_SIZE[0]/2.0 - center[0];
//		double distToCenterY = controller.BOARD_SIZE[1]/2.0 - center[1];
//		double[] updateCorners = new double[corners.length];
//		double seed1 = Math.random() * controller.BOARD_SIZE[0] - controller.BOARD_SIZE[0];
//		double seed2 = Math.random() * controller.BOARD_SIZE[1] - controller.BOARD_SIZE[1];
//		double seed3 = Math.random() * 2*Math.PI;
//
////		wait with random rotation too difficult ;)
////		this.rotatePiece(seed3);
//
//		for(int i = 0; i < corners.length; i++) {
//			double updateCornerX = corners[i] + distToCenterX + seed1;
//			double updateCornerY = corners[i] + distToCenterY + seed2;
//
//			// Taking borders into consideration
//			//if(updateCornerX < 0 || updateCornerY < 0 || updateCornerX > controller.BOARD_SIZE[0] || updateCornerY > controller.BOARD_SIZE[1]) {
//			//	update = false;
//			//} else {
//			if(i % 2 == 0)
//				updateCorners[i] = updateCornerX;
//			else
//				updateCorners[i] = updateCornerY;
//			//}
//		}
//		for(int i = 0; i < corners.length; i++) {
//			corners[i] = updateCorners[i];
//		}
//		updatePiece();
//	}

	// Method for moving piece
	public void movePiece(double deltaX, double deltaY) {
		boolean update = true;
		double c1 = deltaX - this.getCenter()[0];
		double c2 = deltaY - this.getCenter()[1];

		for(Piece p : graph.depthFirstTraversal(graph, this)) {
			Double[] updateCorners = new Double[p.getCorners().length];
			for(int i = 0; i < p.getCorners().length; i++) {
				double updateCornerX = p.getCorners()[i] + c1;
				double updateCornerY =  p.getCorners()[i] + c2;

				if(i % 2 == 0)
					updateCorners[i] = updateCornerX;
				else
					updateCorners[i] = updateCornerY;
			}
			if(update) {
				p.setCorners(updateCorners);
				p.updatePiece();
			}
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
		double deltaX = p.getCenter()[0] - this.center[0];
		double deltaY = p.getCenter()[1] - this.center[1];
		Double[] distances = {deltaX, deltaY};
		adjacentPieces.put(p, distances);
	}
}
