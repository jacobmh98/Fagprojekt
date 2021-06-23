package model;

import controller.Controller;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Piece extends Polygon {
	private Integer pieceID;
	private Double[] corners;
	private ArrayList<SideLength> sideLengths = new ArrayList<>();
	private Double[] center = new Double[2];
	private Double rotation = 0.0;
	private double prevY = 0.0;
	private Controller controller = Controller.getInstance();
	private ArrayList<Piece> nearbyPieces = new ArrayList<Piece>();
	private HashMap<Piece, Double[]> adjacentPieces = new HashMap<Piece, Double[]>();
	private ArrayList<Corner> vectorCorners = new ArrayList<>();
	private ArrayList<PieceLock> pieceLocks = new ArrayList<>();

	public Double getRotation() { return this.rotation; }
	public Double[] getCenter() { return this.center; }
	public Integer getPieceID() { return this.pieceID; }
	public HashMap<Piece, Double[]> getAdjacentPieces() { return this.adjacentPieces; }
	public Double[] getCorners() { return this.corners; }
	public ArrayList<SideLength> getSideLengths() { return this.sideLengths; }
	public ArrayList<Corner> getVectorCorners() { return vectorCorners; }
	public ArrayList<PieceLock> getPieceLocks() {
		return this.pieceLocks;
	}

	// Constructor for piece objects
	// Input - the piece id as well as an array of the corner coordinates
	// Output - constructs the piece object with the mouse events for control
	// Written by Jacob
	public Piece(Integer pieceID, Double[] corners) {
		this.pieceID = pieceID;
		this.corners = corners;
		controller.getGraph().addVertex(this);

		// Methods inherited from JavafX Polygon class
		this.getPoints().addAll(this.corners);
		this.setStroke(Color.BLACK);
		this.setFill(Color.WHITE);
		this.setCursor(Cursor.HAND);
		this.toFront();

		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Piece.this.setMouseTransparent(true);
				mouseEvent.setDragDetect(true);

				System.out.println("Piece id = " + Piece.this.pieceID);
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
				for(Piece p : controller.getGraph().depthFirstTraversal(Piece.this)) {
					p.checkForConnect();
				}
			}
		});

		updatePiece();
	}

	// Method for setting the JavaFX coordinates for polygons
	// written by Jacob
	public void setPoints(){
		this.getPoints().removeAll();
		this.getPoints().setAll(this.corners);
	}

	// Method for updating the corners for a piece. It takes the updates corners and loops through
	// each of its current corners to update these.
	// written by Jacob
	public void setCorners(Double[] updateCorners) {
		for(int i = 0; i < this.corners.length; i++) {
			this.corners[i] = updateCorners[i];
		}
	}

	// Method for setting the rotation of a piece. It takes an angle and updates the piece
	// rotation with this angle using modulus so that it stays in the interval of ]0..2Pi]
	// written by Jacob
	public void setRotation(Double angle) {
		this.rotation += angle;
		this.rotation %= 2*Math.PI;
		if(this.rotation < 0) {
			this.rotation += 2*Math.PI;
		}
	}

	// Method for updating/setting the vector corners meaning the two vectors that can be generated from each corner.
	// It utilizes the Corner class.
	// Written by Jacob
	public void updateVectorCorners() {
		if(vectorCorners.isEmpty()) {
			for (int i = 0; i <= corners.length - 2; i += 2) {
				Double[] vector1, vector2;

				if (i == 0) {
					vector1 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
					vector2 = new Double[]{corners[i] - corners[corners.length - 2], corners[i + 1] - corners[corners.length - 1]};
				} else if (i == corners.length - 2) {
					vector1 = new Double[]{corners[i] - corners[0], corners[i + 1] - corners[1]};
					vector2 = new Double[]{corners[i] - corners[i - 2], corners[i + 1] - corners[i - 1]};
				} else {
					vector1 = new Double[]{corners[i] - corners[i - 2], corners[i + 1] - corners[i - 1]};
					vector2 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
				}
				Double[] coordinates = {corners[i], corners[i + 1]};
				addVectorCorner(vector1, vector2, coordinates, SolvePuzzle.findAngleBetweenVectors(vector1, vector2));
			}
		} else {
			for (int i = 0; i <= corners.length - 2; i += 2) {
				Double[] vector1, vector2;

				if (i == 0) {
					vector1 = new Double[]{corners[i] - corners[i + 2], corners[i + 1] - corners[i + 3]};
					vector2 = new Double[]{corners[i] - corners[corners.length - 2], corners[i + 1] - corners[corners.length - 1]};
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

	// Method for updating the piece in terms of the information behind such as center, sidelengths, vectors and
	// the graphical representation in JavaFX
	// Written by Jacob & Oscar
	public void updatePiece() {
		try {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					setPoints();
				}
			});
		} catch (Exception e){}

		this.computeCenter();
		updateSideLengths();
		updateVectorCorners();
	}

	// Method for updating/setting the side lengths of a piece utilizing the SideLength class.
	// Written by Jacob
	public void updateSideLengths() {
		if(sideLengths.isEmpty()) {
			for (int i = 0; i < corners.length; i += 2) {
				if (i < corners.length - 3) {
					Double dx = (corners[i] - corners[i + 2]);
					Double dy = (corners[i + 1] - corners[i + 3]);
					Double[] corner1 = {corners[i], corners[i + 1]};
					Double[] corner2 = {corners[i + 2], corners[i + 3]};
					Double sideLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
					sideLengths.add(new SideLength(pieceID, sideLength, corner1, corner2));
					continue;
				} else {
					Double dx = (corners[i] - corners[0]);
					Double dy = (corners[i + 1] - corners[1]);
					Double[] corner1 = {corners[i], corners[i + 1]};
					Double[] corner2 = {corners[0], corners[1]};
					Double sideLength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
					sideLengths.add(new SideLength(pieceID, sideLength, corner1, corner2));
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
	}

	// Method in charge of checking whether a piece can connect to another piece in its proximity.
	// It utilizes the nearby pieces and adjacent pieces to determine whether it can snap and
	// if so performs the necessary operations to snap.
	// written by Jacob
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
					if(!controller.getGraph().depthFirstTraversal(p).contains(this)) {
						double angle = p.getRotation() - this.rotation;
						rotatePiece(angle);
						rotateNeighbours(angle);
						temp = adjacentPieces.get(p);
						Double moveDx = dx - temp[0];
						Double moveDy = dy - temp[1];
						Set<Piece> connectedPieces = controller.getGraph().depthFirstTraversal(this);
						snapPiece(p, moveDx, moveDy);
						for(Piece pi : connectedPieces) {
							if(pi != this && pi != p) {
								pi.movePiece(moveDx, moveDy);
							}
						}
					}
				}
			}
		}
		return true;
	}

	// Method for performing the actual snap of a piece when the computations and checks has been done.
	// Input - a piece and how much the piece should move
	// Written by Jacob
	private void snapPiece(Piece p, Double dx, Double dy) {
		controller.getGraph().addEdge(this, p);
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

	// Method for moving the piece relatively to its current position by user interaction (left mouse click)
	// written by Jacob
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

	// Method for moving piece relatively to its current position in all other context of a user interaction
	// for example in snaps.
	// written by Jacob
	public void movePiece(double dx, double dy) {
		boolean update = true;
		double c1 = dx - this.getCenter()[0];
		double c2 = dy - this.getCenter()[1];

		for(Piece p : controller.getGraph().depthFirstTraversal(this)) {
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

	// Method for moving a piece to an aboslute (x, y) coordinate. Used in shuffling of the pieces and the
	// rotation from a neighbour.
	// written by Jacob
	public void movePieceAbsolute(double x, double y) {
		double diffX = x - this.center[0];
		double diffY = y - this.center[1];

		for (int i = 0; i < corners.length; i++) {
			if (i % 2 == 0) {
				corners[i] += diffX;

			} else {
				corners[i] += diffY;
			}
		}

		updatePiece();
	}

	// Method returning nearby pieces within some a specified radius
	// written by Jacob
	public void computeNearbyPieces() {
		ArrayList<Piece> nearbyPieces = new ArrayList<Piece>();
		double radius = (2*(controller.getBoardSize()[0] + controller.getBoardSize()[1])) / 6;

		for(Piece p : controller.getBoardPieces()) {
			Double[] p2Center = p.getCenter();
			double d = Math.sqrt(Math.pow(center[0] - p2Center[0], 2) +
					Math.pow(center[1] - p2Center[1], 2));

			if(d < radius) {

				if(this.getPieceID() != p.getPieceID()) {
					nearbyPieces.add(p);
				}
			}
		}
		this.nearbyPieces = nearbyPieces;
	}

	// Method to compute the center of piece as a polygon. Using the corners it computes the new center
	// and updates the field in the class
	// written by Jacob
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
	}


	// Method for shuffling this particular piece. It generates a random coordinate and rotation within the board
	// and moves the piece to these coordinates and rotates it.
	// written by Jacob
	public void shufflePiece() {
		double seed1 = (Math.random() * controller.getBoardSize()[0]);
		double seed2 = (Math.random() * controller.getBoardSize()[1]);

		movePieceAbsolute(seed1, seed2);

		double seed3 = Math.random() * 2*Math.PI;
		this.rotatePiece(seed3);
	}

	// Method for rotation the adjacent pieces to this piece which contain the pieces that
	// this particular piece can connect to as well the correct difference in both x and y direction
	// where the adjacent pieces are supposed to be to connect. It doesn't update anything
	// graphically as it only computes and updates the fields of the piece.
	// written by Jacob
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

	// Method for rotating the neighbours of this piece using the DFS graph search to get the connected pieces.
	// It computes all the connected pieces as well as moves the neighbours to a center simulating
	// a rotation around the axis of this piece.
	// Written by Jacob
	public void rotateNeighbours(Double angle) {
		for(Piece p : controller.getGraph().depthFirstTraversal(this)) {
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

	// Method for rotating this particular piece. It takes an angle and computes the new corners for the piece
	// after rotating it with this angle.
	// Written by Jacob
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

		if(update) {
			corners = newCorners;
			updatePiece();
		}
	}

	// Method for converting the corners from a 2 dimensional array needed to perform the matrix operations to a
	// 1 dimensional array needed for the JavaFX polygon class to draw the piece.
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

	// Method for matrix subtraction taking two 2 dimensional arrays as arguments as well as the size
	// and returning the result of the subtraction.
	// Written by Jacob
	private Double[][] subtract(Double[][] matrix1, Double[][] matrix2, int n) {
		Double[][] sum = new Double[2][n];

		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < n; c++) {
				sum[r][c] = matrix1[r][c] - matrix2[r][c];
			}
		}

		return sum;
	}

	// Method for matrix multiplication taking two 2 dimensional arrays as arguments
	// and returning the scalar.
	// Written by Jacob
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

	// Method for matrix addition taking two 2 dimensional arrays as arguments as well as the size
	// and returning the sum of the addition.
	// Written by Jacob
	private Double[][] addition(Double[][] matrix1, Double[][] matrix2, int n) {
		Double[][] sum = new Double[2][n];

		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < n; c++) {
				sum[r][c] = matrix1[r][c] + matrix2[r][c];
			}
		}

		return sum;
	}

	// Method to add an adjacent piece meaning one that this piece is supposed to connect to.
	// Written by Jacob
	public void addAdjacentPiece(Piece p) {
		double deltaX = p.getCenter()[0] - this.center[0];
		double deltaY = p.getCenter()[1] - this.center[1];
		Double[] distances = {deltaX, deltaY};
		adjacentPieces.put(p, distances);
	}

	// Method to add a vector corner to the current piece utilizing the Corner class.
	// Written by Jacob
	public void addVectorCorner(Double[] vector1, Double[] vector2, Double[] coordinates, double angle) {
		this.vectorCorners.add(new Corner(vector1, vector2, coordinates, angle));
	}

	// Method that helps adding adjacent pieces to the pieces loaded from a JSON file
	// Input - the piece to add neighbour to and the two sidelengths that are matching
	// Output - Non, but piece has had a neighbour added to it
	// Written by Oscar
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

	// Method for adding a new instance of PieceLock to the list for this piece
	// Input - a PieceLock object
	// Output - adds the PieceLock object to the list containing all of the PieceLock objects of this class
	// Written by Jacob
	public void addPieceLock(PieceLock pieceLock) {
		this.pieceLocks.add(pieceLock);
	}

	// Method for clearing the list containing the SideLock objects.
	// Written by Jacob
	public void clearPieceLocks() {
		pieceLocks.clear();
	}
}