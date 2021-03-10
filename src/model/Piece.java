package model;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Piece extends Polygon {
	private Integer pieceID;
	private Double[] corners;
	private Double[] center = new Double[2];
	private Double rotation = 0.0;
	Controller controller = Controller.getInstance();
	
	// Constructor for piece
	public Piece(Integer pieceID, Double[] corners) {
		this.pieceID = pieceID;
		this.corners = corners;
		
		// Methods inherited from JavafX Polygon class
		this.getPoints().addAll(this.corners);
		this.setStroke(Color.BLACK);
		this.setFill(Color.WHITE);
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
				mouseEvent.setDragDetect(true);
				computeCenter();

				double deltaX = mouseEvent.getX();
				double deltaY = mouseEvent.getY();
				Double[] updateCorners = new Double[corners.length];
				boolean update = true;

				for(int i = 0; i < corners.length; i++) {
					double updateCornerX = corners[i] + (deltaX - center[0]);
					double updateCornerY = corners[i] + (deltaY - center[1]);

					if(updateCornerX < 0 || updateCornerY < 0 || deltaX < 0 || deltaY < 0) {
						update = false;
						break;
					}

					if(i % 2 == 0) {
						updateCorners[i] = updateCornerX;
					} else {
						updateCorners[i] = updateCornerY;
					}
				}

				if(update) {
					for(int i = 0; i < corners.length; i++) {
						corners[i] = updateCorners[i];
					}

					Piece.this.getPoints().removeAll();
					Piece.this.getPoints().setAll(corners);
					Piece.this.setMouseTransparent(false);
				}
			}
		});
	}

	// Getter method for corners
	public Double[] getCorners() {
		return this.corners;
	}

	// Calculate center
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
	
	private void updatePolygon(Double[][] cornerMatrix) {
		Double[] corners = new Double[cornerMatrix[0].length * 2];
		int pos = 0;
		for(int i = 0; i < cornerMatrix[0].length; i++) {
			corners[pos] = cornerMatrix[0][i];
			pos++;
			corners[pos] = cornerMatrix[1][i];
			pos++;
		}
		this.getPoints().setAll(corners);
	}
	
	// Method for rotating piece
	public void setRotation(Double angle) {
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
		
		// Variable contains rotatet coordinates
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
		System.out.println("Corners");
		print2dArray(pOld);
		
		// Compute the centroid of the polygon
		centroid[0] = (1/(double) n) * sumX;
		centroid[1] = (1/(double) n) * sumY;
		for(int col = 0; col < n ; col++) {
			C[0][col] = centroid[0];
			C[1][col] = centroid[1];
		}
		System.out.println("\nCentroid");
		print2dArray(C);
		
		System.out.println("\nRotation with angle: " + angle);
		
		pNew = addition(dot(R, subtract(pOld,C,n)), C,n);
		print2dArray(pNew);
		
		updatePolygon(pNew);
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
}
