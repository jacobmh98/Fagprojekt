package model;

import controller.Controller;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

public class JsonImport {
    private static Controller controller = Controller.getInstance();

    // Main method that calls the other methods to create the array of pieces
    // Input - A string containing the path to a json file
    // Output - An array containing all the board pieces
    // Written by Oscar
    public static ArrayList<Piece> readJson(String filename, boolean addSnap) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(filename));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray formArray = (JSONArray) ((JSONObject) jsonObject.get("puzzle")).get("form");
        JSONArray jsonPieces = (JSONArray) jsonObject.get("pieces");
        double factor = extractCoordFactor(formArray);
        ArrayList<Piece> pieceArray = extractReformattetPieces(jsonPieces, factor);
        controller.setBoardPieces(pieceArray);
        if(addSnap) {
            addNeighboursWithSolving();
        }
        return pieceArray;
    }

    // Method that extracts the factor to increase the json board size to the desired boardsize
    // Input - A JSONArray that contain the board edge
    // Output - The Factor to multiply to all coordinates
    // Written by Oscar
    public static double extractCoordFactor(JSONArray formArray){
        double highestX = 0.0;
        double highestY = 0.0;
        for(int i = 0; i < formArray.size(); i++){
            Map edges = (Map) ((JSONObject) formArray.get(i)).get("coord");
            double x = (double) edges.get("x");
            double y = (double) edges.get("y");
            if(x > highestX){
                highestX = x;
            }
            if(y > highestY){
                highestY = y;
            }
        }
        double xFactor = controller.getBoardSize()[0]/highestX;
        double yFactor = controller.getBoardSize()[1]/highestY;
        if(xFactor > yFactor){
            return yFactor;
        }
        return xFactor;
    }

    // Method to extract all the corners of the pieces, increases their size based on the factor and
    // moves them to be completely inside the board
    // Input - JSONArray containing all the coordinates to all piece corners and the factor to multiply to all coordinates
    // Output - An array containing all the board pieces
    // Written by Oscar
    public static ArrayList<Piece> extractReformattetPieces(JSONArray jsonPieces, double factor){
        double lowestX = 0.0; //To deal with potential negative numbers since javaFX operate on x & y > 0
        double lowestY = 0.0;
        int pieceNumber = 0;
        ArrayList<Piece> pieceArray = new ArrayList<>();
        ArrayList<ArrayList<Double>> allPieceCorners = new ArrayList<>();
        for(int i = 0; i < jsonPieces.size(); i++){ //Loops around all pieces
            JSONArray pieceCornerArray = (JSONArray) ((JSONObject) jsonPieces.get(i)).get("corners");
            ArrayList<Double> cornerList = new ArrayList<>();
            for(int j = 0; j < pieceCornerArray.size(); j++){ //Loops around all corners
                Map coords = (Map) ((JSONObject) pieceCornerArray.get(j)).get("coord");
                double x = (double) coords.get("x");
                double y = (double) coords.get("y");
                if(x < lowestX){
                    lowestX = x;
                }
                if(y < lowestY){
                    lowestY = y;
                }
                cornerList.add(x);
                cornerList.add(y);
            }
            allPieceCorners.add(cornerList);
        }
        System.out.println(lowestX);
        System.out.println(lowestY);
        for(int i = 0; i < allPieceCorners.size(); i++){
            pieceArray.add(createPiece(allPieceCorners.get(i), pieceNumber, lowestX, lowestY, factor));
            pieceNumber++;
        }
        return pieceArray;
    }

    // Method that multiply the corners by the factor and moves them onto the board, and it creates the Piece objects
    // from the corner array
    // Input - The array containing the corners of the piece, the PieceID, the values to move the piece to  make sure
    // that it is on the board, The scaling factor
    // Output - A piece object
    // Written by Oscar
    public static Piece createPiece(ArrayList<Double> cornerList, int pieceNumber, double lowestX, double lowestY, double factor){
        Double[] corners = new Double[cornerList.size()];
        for(int i = 0; i < cornerList.size(); i++){
            double corner = cornerList.get(i);
            if(i%2 == 0){
                corner = (corner-lowestX)*factor;
            } else {
                corner = (corner-lowestY)*factor;
            }
            corners[i] = corner;
        }
        Piece piece = new Piece(pieceNumber, corners);
        return piece;
    }

    // Method that tries to solve puzzle and then adds snap to neighbours and then shuffles the pieces again
    // if no solution was found it doesn't do anything
    // Input - Non, it uses the board pieces from the controller
    // Output - Non, but it adds neighbours to all the pieces if solution was found and shuffles the pieces
    // Written by Oscar
    public static void addNeighboursWithSolving() throws InterruptedException {
        ArrayList<Piece> pieces = controller.getBoardPieces();
        ArrayList<Double[]> oldCoords = new ArrayList<>();
        for(Piece p : pieces){
            oldCoords.add(p.getCorners());
        }
        SolvePuzzle solver = new SolvePuzzle(false);
        solver.solveByCorners();
        if(solver.checkIfSolved(controller.getBoardPieces())) {
            for(Piece p : controller.getBoardPieces()){
                p.setRotation(-p.getRotation());
            }
            for (Piece p1 : controller.getBoardPieces()) {
                for (Piece p2 : controller.getBoardPieces()) {
                    if (p1 != p2) {
                        for (SideLength s1 : p1.getSideLengths()) {
                            for (SideLength s2 : p2.getSideLengths()) {
                                if (compareEpsilon(s1.getCorners()[0][0], s2.getCorners()[0][0]) && compareEpsilon(s1.getCorners()[1][0], s2.getCorners()[1][0]) &&
                                        compareEpsilon(s1.getCorners()[0][1], s2.getCorners()[0][1]) && compareEpsilon(s1.getCorners()[1][1], s2.getCorners()[1][1])) {
                                        p1.addAdjacentPiece(p2);
                                } else if (compareEpsilon(s1.getCorners()[0][0], s2.getCorners()[1][0]) && compareEpsilon(s1.getCorners()[1][0], s2.getCorners()[0][0]) &&
                                        compareEpsilon(s1.getCorners()[0][1], s2.getCorners()[1][1]) && compareEpsilon(s1.getCorners()[1][1], s2.getCorners()[0][1])) {
                                        p1.addAdjacentPiece(p2);
                                }
                            }
                        }
                    }
                }

            }
            for(Piece p : controller.getBoardPieces()){
                p.shufflePiece();
            }
        }
    }

    private static boolean compareEpsilon(Double value1, Double value2){
        double epsilon = 0.0000001;
        return value1 + epsilon > value2 && value1 - epsilon < value2;
    }
}
