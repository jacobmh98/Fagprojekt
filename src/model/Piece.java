package model;

import java.util.ArrayList;

public class Piece {
	private ArrayList<Float> coordinates = new ArrayList<Float>();
	
	public Piece(ArrayList<Float> coordinates) {
		this.coordinates = coordinates;
	}
	
	// Getter method for coordinates
	public ArrayList<Float> getCoordinates() {
		return this.coordinates;
	}
}
