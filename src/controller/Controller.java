package controller;

import java.util.ArrayList;

import javafx.scene.Group;
import model.Piece;

public class Controller {
	private static Controller controller = new Controller();;
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	Group group;
	public final int[] BOARD_SIZE = {800, 800};
	public final int ROWS = 15;
	public final int COLUMNS = 20;
	
	public Controller() {}

	public static Controller getInstance() {
		return controller;
	}

	public void setGroup(Group g) {
		this.group = g;
	}

}
