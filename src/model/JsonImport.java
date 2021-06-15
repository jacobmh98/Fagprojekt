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

    public static ArrayList<Piece> readJson(String filename) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(filename));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray formArray = (JSONArray) ((JSONObject) jsonObject.get("puzzle")).get("form");
        JSONArray jsonPieces = (JSONArray) jsonObject.get("pieces");
        long totalPieces = (long) jsonObject.get("no. of pieces");
        double factor = extractCoordFactor(formArray);
        ArrayList<Piece> pieceArray = extractReformattetPieces(totalPieces, jsonPieces, factor);
        addSnapToPossibleNeighbours(pieceArray);
        return pieceArray;
    }

    public static double extractCoordFactor(JSONArray formArray){
        Double[] factors = new Double[2];
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

    public static ArrayList<Piece> extractReformattetPieces(long totalPieces, JSONArray jsonPieces, double factor){
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

    public static ArrayList<Piece> extractPieces(long totalPieces, JSONArray jsonPieces){
        int pieceNumber = 0;
        ArrayList<Piece> pieceArray = new ArrayList<>();
        for(int i = 0; i < jsonPieces.size(); i++){ //Loops around all pieces
            JSONArray pieceCornerArray = (JSONArray) ((JSONObject) jsonPieces.get(i)).get("corners");
            ArrayList<Double> cornerList = new ArrayList<>();
            for(int j = 0; j < pieceCornerArray.size(); j++){ //Loops around all corners
                Map coords = (Map) ((JSONObject) pieceCornerArray.get(j)).get("coord");
                cornerList.add((double) coords.get("x"));
                cornerList.add((double) coords.get("y"));
            }
            pieceArray.add(createPiece(cornerList, pieceNumber, 0, 0, 1));
            pieceNumber++;
        }
        return pieceArray;
    }

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

    public static void addSnapToPossibleNeighbours(ArrayList<Piece> pieces){
        double epsilon = 0.00001;
        for(Piece p1: pieces){
            for(Piece p2: pieces){
                if(p2.getPieceID() != p1.getPieceID()){
                    for(SideLength s1 : p1.getSideLengths()){
                        for(SideLength s2 : p2.getSideLengths()){
                            if(s1.getValue()+epsilon >= s2.getValue() && s1.getValue()-epsilon <= s2.getValue()){
                                p1.addPossibleAdjacentPiece(p2, s1, s2);
                            }
                        }
                    }
                }
            }
        }
    }
}
