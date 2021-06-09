package model;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Piece extends Polygon {
	private Integer pieceID;
	private Double[] corners;
	private ArrayList<SideLength> sideLengths = new ArrayList<>();
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
	public ArrayList<Corner> vectorCorners = new ArrayList<>();
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
		this.setFill(Color.WHITE);
		this.setCursor(Cursor.HAND);

		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Piece.this.setMouseTransparent(true);
				mouseEvent.setDragDetect(true);
			}
		});

		this.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {

				mouseEvent.setDragDetect(false);

				double deltaX = mouseEvent.getX();
				double deltaY = mouseEvent.getY();
				int direction = 0;

				if(deltaY > Piece.this.prevY) {
					direction = -1;
				} else {
					direction = 1;
				}
				Piece.this.prevY = deltaY;

				if(mouseEvent.getButton() == MouseButton.PRIMARY) {
					movePiece(deltaX, deltaY);
				} else {
					if(direction == 1) {
						rotatePiece(Math.PI/50);
						rotateNeighbours(Math.PI/50);
					} else {
						rotatePiece(-Math.PI/50);
						rotateNeighbours(-Math.PI/50);
					}
				}
			}
		});

		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Piece.this.setMouseTransparent(false);
				checkForConnect();
			}
		});

		updatePiece();
	}

	public void updateVectorCorners() {
		if(vectorCorners.isEmpty()) {
			for (int i = 0; i <= corners.length - 2; i += 2) {
				Double[] vector1, vector2;

				if (i == 0) {
					vector1 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
					vector2 = new Double[]{corners[i] - corners[corners.length - 2], corners[i] - corners[corners.length - 1]};
				} else if (i == corners.length - 2) {
					vector1 = new Double[]{corners[i] - corners[0], corners[i + 1] - corners[1]};
					vector2 = new Double[]{corners[i] - corners[i - 2], corners[i + 1] - corners[i - 1]};
				} else {
					vector1 = new Double[]{corners[i] - corners[i - 2], corners[i + 1] - corners[i - 1]};
					vector2 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
				}
				Double[] coordinates = {corners[i], corners[i + 1]};
				addVectorCorner(vector1, vector2, coordinates, SolvePuzzleJSON.findAngleBetweenVectors(vector1, vector2));
			}
		} else {
			for (int i = 0; i <= corners.length - 2; i += 2) {
				Double[] vector1, vector2;

				if (i == 0) {
					vector1 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
					vector2 = new Double[]{corners[i] - corners[corners.length - 2], corners[i] - corners[corners.length - 1]};
				} else if (i == corners.length - 2) {
					vector1 = new Double[]{corners[i] - corners[0], corners[i + 1] - corners[1]};
					vector2 = new Double[]{corners[i] - corners[i - 2], corners[i + 1] - corners[i - 1]};
				} else {
					vector1 = new Double[]{corners[i] - corners[i - 2], corners[i + 1] - corners[i - 1]};
					vector2 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
				}
				Double[] coordinates = {corners[i], corners[i + 1]};
				vectorCorners.get(i/2).updateCorner(vector1, vector2, coordinates);
			}
		}
	}


	public void updatePiece() {
		this.getPoints().removeAll();
		this.getPoints().setAll(this.corners);
		this.computeCenter();
		updateSideLengths();
		updateVectorCorners();
	}

	public void updateSideLengths() {
		if(sideLengths.isEmpty()) {
			for (int i = 0; i < corners.length; i += 2) {
				if (i < corners.length - 3) {
					Double dx = (corners[i] - corners[i + 2]);
					Double dy = (corners[i + 1] - corners[i + 3]);
					Double[] corner1 = {corners[i], corners[i + 1]};
					Double[] corner2 = {corners[i + 2], corners[i + 3]};
					Double sideLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
					sideLengths.add(new SideLength(pieceID, i / 2, sideLength, corner1, corner2));
					continue;
				} else {
					Double dx = (corners[i] - corners[0]);
					Double dy = (corners[i + 1] - corners[1]);
					Double[] corner1 = {corners[i], corners[i + 1]};
					Double[] corner2 = {corners[0], corners[1]};
					Double sideLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
					sideLengths.add(new SideLength(pieceID, i / 2, sideLength, corner1, corner2));
					break;
				}
			}
		} else {
			for (int i = 0; i < corners.length; i += 2) {
				if (i < corners.length - 3) {
					Double[] corner1 = {corners[i], corners[i + 1]};
					Double[] corner2 = {corners[i + 2], corners[i + 3]};
					Double dx = (corners[i] - corners[i + 2]);
					Double dy = (corners[i + 1] - corners[i + 3]);
					Double sideLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
					sideLengths.get(i/2).update(sideLength, corner1, corner2);
					continue;
				} else {
					Double[] corner1 = {corners[i], corners[i + 1]};
					Double[] corner2 = {corners[0], corners[1]};
					Double dx = (corners[i] - corners[0]);
					Double dy = (corners[i + 1] - corners[1]);
					Double sideLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
					sideLengths.get(i/2).update(sideLength, corner1, corner2);
					break;
				}
			}
		}
//		System.out.println("After Update");
//		for (SideLength l : sideLengths) {
//			System.out.println(l.getPieceId() + ", " +
//					l.getLineId() + ", " +
//					l.getValue() + ", " +
//					l.getDx() + ", " +
//					l.getDy());
//		}
	}

	public boolean checkForConnect() {
		computeNearbyPieces();
		for(Piece p : nearbyPieces) {
			Double[] temp = adjacentPieces.get(p);

			if(temp != null) {
				Double dx = p.getCenter()[0] - this.getCenter()[0];
				Double dy = p.getCenter()[1] - this.getCenter()[1];

				Double dxMin = temp[0] - 25.0;
				Double dxMax = temp[0] + 25.0;
				Double dyMin = temp[1] - 25.0;
				Double dyMax = temp[1] + 25.0;

				if(dx > dxMin && dx < dxMax && dy > dyMin && dy < dyMax) {
					if(!graph.depthFirstTraversal(p).contains(this)) {
						double angle = p.getRotation() - this.rotation;
						rotatePiece(angle);
						rotateNeighbours(angle);
						temp = adjacentPieces.get(p);
						Double moveDx = dx - temp[0];
						Double moveDy = dy - temp[1];
						Set<Piece> connectedPieces = graph.depthFirstTraversal(this);
						snapPiece(p, moveDx, moveDy);
						for(Piece pi : connectedPieces) {
							if(pi != this && pi != p) {
								pi.movePiece(moveDx, moveDy);
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

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

	public void movePiece(Double dx, Double dy) {
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
		double radius = (2*(controller.getBoardSize()[0] + controller.getBoardSize()[1])) / (controller.ROWS + controller.COLUMNS);

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
	public void shufflePiece() {
		double seed1 = (Math.random() * controller.getBoardSize()[0]);
		double seed2 = (Math.random() * controller.getBoardSize()[1]);

		movePieceAbsolute(seed1, seed2);

		System.out.println("seeds: " + seed1 +", " + seed2);

//		double seed3 = Math.random() * 2*Math.PI;
//		wait with random rotation too difficult ;)
//		this.rotatePiece(seed3);
	}

	// Method for moving piece relatively
	public void movePiece(double deltaX, double deltaY) {
		boolean update = true;
		double c1 = deltaX - this.getCenter()[0];
		double c2 = deltaY - this.getCenter()[1];

		for(Piece p : graph.depthFirstTraversal(this)) {
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

	public void movePieceAbsolute(double xCoordinate, double yCoordinate) {
		double diffX = xCoordinate - this.center[0];
		double diffY = yCoordinate - this.center[1];
		int[] direction = {0,0};

		for (int i = 0; i < corners.length; i++) {
			if (i % 2 == 0) {
				corners[i] += diffX;
				if(corners[i] < 0) {
					direction[0] = 1;
				} else if(corners[i] > controller.getBoardSize()[0]) {
					direction[0] = -1;
				} else {
					direction[0] = 0;
				}
			} else {
				corners[i] += diffY;
				if(corners[i] < 0) {
					direction[1] = 1;
				} else if(corners[i] > controller.getBoardSize()[0]) {
					direction[1] = -1;
				} else {
					direction[1] = 0;
				}
			}
		}

		updatePiece();
	}

	public void rotateAdjacentPieces(Double angle) {
		for(Piece p : adjacentPieces.keySet()) {
			Double[][] R = {
					{Math.cos(angle), Math.sin(angle)},
					{Math.sin(angle) * (-1.0), Math.cos(angle)}
			};
			Double[] rotationCenter = this.center;
			Double[] oldCenter = {rotationCenter[0] + adjacentPieces.get(p)[0], rotationCenter[1] + adjacentPieces.get(p)[1]};
			Double[] tempVector = new Double[]{oldCenter[0] - rotationCenter[0], oldCenter[1] - rotationCenter[1]};

			Double[] newCenter = new Double[] {
					(R[0][0] * tempVector[0] + R[0][1] * tempVector[1]),
					(R[1][0] * tempVector[0] + R[1][1] * tempVector[1])
			};

			adjacentPieces.put(p, newCenter);
		}
	}

	public void rotateNeighbours(Double angle) {
		for(Piece p : graph.depthFirstTraversal(this)) {
			if(p != this) {
				p.rotatePiece(angle);

				Double[][] R = {
						{Math.cos(angle), Math.sin(angle)},
						{Math.sin(angle) * (-1.0), Math.cos(angle)}
				};
				Double[] rotationCenter = this.center;
				Double[] oldCenter = {p.getCenter()[0], p.getCenter()[1]};
				Double[] tempVector = new Double[]{oldCenter[0] - rotationCenter[0], oldCenter[1] - rotationCenter[1]};

				Double[] newCenter = new Double[] {
						(R[0][0] * tempVector[0] + R[0][1] * tempVector[1]) + rotationCenter[0],
						(R[1][0] * tempVector[0] + R[1][1] * tempVector[1]) + rotationCenter[1]
				};

				p.movePieceAbsolute(newCenter[0], newCenter[1]);
			}
		}
	}

	// Method for rotating piece
	public void rotatePiece(Double angle) {
		setRotation(angle);
		rotateAdjacentPieces(angle);
		Integer n = corners.length / 2;

		// Variable C contains n pairs of the centroid
		Double[] centroid = new Double[2];
		Double[][] C = new Double[2][n];

		// Variable for rotation matrix
		Double[][] R = {
				{Math.cos(angle), Math.sin(angle)},
				{Math.sin(angle) * (-1.0), Math.cos(angle)}
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
			updatePiece();
		}
	}

	public void setRotation(Double angle) {
		this.rotation += angle;
		this.rotation %= 2*Math.PI;
		if(this.rotation < 0) {
			this.rotation += 2*Math.PI;
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

	public void addPossibleAdjacentPiece(Piece p, SideLength s1, SideLength s2){
		if(adjacentPieces.get(p) == null) {
			double toMoveX, toMoveY;
			double x1 = s1.getCorners()[0][0]-s1.getCorners()[1][0];
			double x2 = s2.getCorners()[0][0]-s2.getCorners()[1][0];
			if(x2 > 0 && x1 < 0 || x2 < 0 && x1 > 0){
				toMoveX = s1.getCorners()[0][0] - s2.getCorners()[1][0];
				toMoveY = s1.getCorners()[0][1] - s2.getCorners()[1][1];
			} else {
				toMoveX = s1.getCorners()[0][0] - s2.getCorners()[0][0];
				toMoveY = s1.getCorners()[0][1] - s2.getCorners()[0][1];
			}
			p.movePiece(toMoveX + p.getCenter()[0], toMoveY + p.getCenter()[1]);
			addAdjacentPiece(p);
			p.movePiece(-toMoveX + p.getCenter()[0], -toMoveY + p.getCenter()[1]);
		}
	}

	public ArrayList<SideLength> getSideLengths() {
		return this.sideLengths;
	}

	public void addVectorCorner(Double[] vector1, Double[] vector2, Double[] coordinates, double angle) {
		this.vectorCorners.add(new Corner(vector1, vector2, coordinates, angle));
	}

	public ArrayList<Corner> getVectorCorners() {
		return vectorCorners;
	}
}

class SideLength implements Comparable {
	private Integer pieceId;
	private Integer lineId;
	private Double length;
	private Double[] corner1;
	private Double[] corner2;

	public SideLength(Integer pieceId, Integer lineId, Double length, Double[] corner1, Double[] corner2) {
		this.pieceId = pieceId;
		this.lineId = lineId;
		this.length = length;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public Integer getPieceId() {
		return this.pieceId;
	}
	public Integer getLineId() {
		return this.lineId;
	}
	public Double getValue() {
		return this.length;
	}

	public Double[][] getCorners() {
//		System.out.println("Corner 1: ( + " + corner1[0] + ", " + corner1[1] + " )");
//		System.out.println("Corner 2: ( + " + corner2[0] + ", " + corner2[1] + " )");
		return new Double[][] {new Double[]{this.corner1[0], this.corner1[1]}, new Double[]{this.corner2[0], this.corner2[1]}};
	}

	@Override
	public int compareTo(Object l) {
		return (int)(this.length - ((SideLength) l).getValue());
	}

	public void update(Double sideLength, Double[] corner1, Double[] corner2) {
		this.length = sideLength;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}
}

class Corner {
	private Double[] vector1;
	private Double[] vector2;
	private Double[] coordinates;
	private double angle;

	public Corner(Double[] vector1, Double[] vector2, Double[] coordinates, double angle) {
		this.vector1 = vector1;
		this.vector2 = vector2;
		this.coordinates = coordinates;
		this.angle = angle;
	}

	public double getAngle() {
		return this.angle;
	}

	public Double[] getCoordinates() {
		return this.coordinates;
	}

	public void updateCorner(Double[] vector1, Double[] vector2, Double[] coordinates) {
		this.vector1 = vector1;
		this.vector2 = vector2;
		this.coordinates = coordinates;
	}

	public Double[][] getVectors() {
		return new Double[][]{this.vector1, this.vector2};
	}
}