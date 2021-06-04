package model;


import controller.Controller;

import java.util.ArrayList;

public class CreatePuzzleBoard {
    private int rows;
    private int columns;
    private int height;
    private int width;
    private double pieceWidth;
    private double pieceHeight;
    private double maxWidth;
    private double maxHeight;
    private ArrayList<ArrayList<Double>> columnX = new ArrayList<>();
    private ArrayList<ArrayList<Double>> columnY = new ArrayList<>();
    private ArrayList<ArrayList<Double>> rowX = new ArrayList<>();
    private ArrayList<ArrayList<Double>> rowY = new ArrayList<>();
    private Controller controller = Controller.getInstance();

    private ArrayList<Double> pieceX = new ArrayList<>();
    private ArrayList<Double> pieceY = new ArrayList<>();
    private ArrayList<Integer> rowIntersectPoint = new ArrayList<>();
    private ArrayList<Integer> columnIntersectPoint = new ArrayList<>();

    private ArrayList<Piece> boardPieces = new ArrayList<>();

    //Getters
    public ArrayList<ArrayList<Double>> getColumnX() { return columnX; }

    public ArrayList<ArrayList<Double>> getColumnY() { return columnY; }

    public ArrayList<ArrayList<Double>> getRowX() { return rowX; }

    public ArrayList<ArrayList<Double>> getRowY() { return rowY; }

    public ArrayList<Double> getPieceX() {return pieceX;}

    public ArrayList<Double> getPieceY() {return pieceY;}

    public ArrayList<Piece> getBoardPieces() {return boardPieces;}

    public CreatePuzzleBoard(){
        this.rows = controller.ROWS;
        this.columns = controller.COLUMNS;
        this.height = controller.getBoardSize()[1];
        this.width = controller.getBoardSize()[0];
        this.pieceWidth =(double)width/columns;
        this.pieceHeight=(double)height/rows;
        this.maxWidth = pieceWidth *0.3;
        this.maxHeight = pieceHeight*0.3;
    }

    public void createPuzzle(){
        createColumn(columns);
        createRows(rows);
        findIntersectPoints();
        defineBoardPieceCorners();
        setAdjacentPieces();
    }

    private void createColumn(int totalColumns) {
        for(int i = 0; i < totalColumns+1; i++){
            ArrayList<Double> columnListX = new ArrayList<>();
            ArrayList<Double> columnListY = new ArrayList<>();
            if(i != 0 && i != totalColumns) {
                double y = 0;
                columnListX.add(i* pieceWidth);
                columnListY.add(y);
                while (y < height) {
                    double x = (i * pieceWidth - maxWidth) + Math.random() * (i * pieceWidth + maxWidth - (i * pieceWidth - maxWidth));
                    //y needs to be atleast the same length as x can vary (angle can vary atmost 45 degrees from vertical line)
                    y += (maxWidth*2) + Math.random() * (pieceHeight-maxWidth*2)*0;
                    if(y > height-10) {
                        y = height;
                    }
                    columnListY.add(y);
                    columnListX.add(x);
                }
            } else {
                columnListX.add(i* pieceWidth);
                columnListX.add(i* pieceWidth);
                columnListY.add(0.0);
                columnListY.add((double)height);
            }
            columnX.add(columnListX);
            columnY.add(columnListY);
        }

    }

    private void createRows(int rows){
        for(int i = 0; i < rows+1; i++){
            ArrayList<Double> rowListX = new ArrayList<>();
            ArrayList<Double> rowListY = new ArrayList<>();
            if(i != 0 && i != rows){
                double x = 0;
                rowListX.add(x);
                rowListY.add(i*pieceHeight);
                while(x < width){
                    x += (maxHeight*2) + Math.random()*(pieceWidth -maxHeight*2)*0;
                    double y = (i * pieceHeight - maxHeight) + Math.random() * (i * pieceHeight + maxHeight - (i * pieceHeight - maxWidth));
                    if(x > width-10){
                        x = width;
                    }
                    rowListX.add(x);
                    rowListY.add(y);
                }
            } else {
                rowListX.add(0.0);
                rowListX.add((double)width);
                rowListY.add(i*pieceHeight);
                rowListY.add(i*pieceHeight);
            }
            rowX.add(rowListX);
            rowY.add(rowListY);
        }
    }
    
    public void findIntersectPoints(){
        //Goes through each column and finds where the row intersects it
        for(int col = 0; col <= columns; col++){
            for(int row = 0; row <= rows; row++){
                int currentColumnPoint = 0;
                boolean foundIntersect = false;
                while(!foundIntersect && currentColumnPoint < columnX.get(col).size()-1){
                    int currentRowPoint = 0;
                    while(!foundIntersect && rowX.get(row).get(currentRowPoint) < col* pieceWidth +maxWidth && currentRowPoint < rowX.get(row).size()-1){
                        double[] intersectingPoints = intersect(columnX.get(col).get(currentColumnPoint), columnY.get(col).get(currentColumnPoint),
                                                                columnX.get(col).get(currentColumnPoint+1), columnY.get(col).get(currentColumnPoint+1),
                                                                rowX.get(row).get(currentRowPoint), rowY.get(row).get(currentRowPoint),
                                                                rowX.get(row).get(currentRowPoint+1), rowY.get(row).get(currentRowPoint+1));
                        if(intersectingPoints[0] < 0) { //Didn't intersect
                            currentRowPoint++;
                        } else {
                            foundIntersect = true;
                            columnIntersectPoint.add(currentColumnPoint);
                            rowIntersectPoint.add(currentRowPoint);
                            pieceX.add(intersectingPoints[0]);
                            pieceY.add(intersectingPoints[1]);
                        }
                    }
                    currentColumnPoint++;
                }
            }
        }
    }

    //Column points, row points
    public double[] intersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
        double[] intersectingPoints = new double[2];
        double m1, m2, b1, b2;
        //If column line is vertical we can't use the two point formula
        if(x1-x2 == 0) {
            m2 = (y4-y3)/(x4-x3);
            b2 = y3-x3*m2;
            double yAtX = m2*x1+b2;
            if((yAtX <= y2 && yAtX >= y1) && (x3 <= x1 && x4 >= x1)){
                intersectingPoints[0] = x1;
                intersectingPoints[1] = yAtX;
            } else {
                intersectingPoints[0] = -1;
            }
        }   else {
            //Finding the formula for the two lines
            m1 = (y2-y1)/(x2-x1);
            m2 = (y4-y3)/(x4-x3);
            b1 = y1-x1*m1;
            b2 = y3-x3*m2;
            //Check the x value the two lines intersect
            double intersectX = (b2-b1)/(m1-m2);
            intersectX = Math.round(intersectX*1000.0)/1000.0;
            x1 = Math.round(x1*1000.0)/1000.0;
            x2 = Math.round(x2*1000.0)/1000.0;
            //We know that x4 > x3, but x2 is not always bigger than x1
            if((intersectX >= x3 && intersectX <= x4) && ((intersectX >= x1 && intersectX <= x2) || (intersectX <= x1 && intersectX >= x2))){
                intersectingPoints[0] = intersectX;
                intersectingPoints[1] = m1*intersectX+b1;
            } else {
                intersectingPoints[0] = -1; //Used as a boolean to check if there were an intersection
            }
        }
        return intersectingPoints;
    }

    private void defineBoardPieceCorners(){
        int pieceID = 0;
        for(int i = 0; i < columns; i++){
            for(int j = 0; j < rows; j++){
                //Defining where in the point matrix the corners are
                int tleft = i*(rows+1)+j;
                int tright = (i+1)*(rows+1)+j;
                int bright = (i+1)*(rows+1)+j+1;
                int bleft = i*(rows+1)+j+1;
                //Using arraylist since we don't know total corners yet
                ArrayList<Double> pieceCorners = new ArrayList<>();
                //top line corners
                pieceCorners.add(pieceX.get(tleft));
                pieceCorners.add(pieceY.get(tleft));
                for(int k = rowIntersectPoint.get(tleft)+1; k <= rowIntersectPoint.get(tright); k++){
                    pieceCorners.add(rowX.get(j).get(k));
                    pieceCorners.add(rowY.get(j).get(k));
                }
                //Right line corners
                pieceCorners.add(pieceX.get(tright));
                pieceCorners.add(pieceY.get(tright));
                for(int k = columnIntersectPoint.get(tright)+1; k <= columnIntersectPoint.get(bright); k++){
                    pieceCorners.add(columnX.get(i+1).get(k));
                    pieceCorners.add(columnY.get(i+1).get(k));
                }
                //Bottom line corners
                pieceCorners.add(pieceX.get(bright));
                pieceCorners.add(pieceY.get(bright));
                for(int k = rowIntersectPoint.get(bright); k > rowIntersectPoint.get(bleft); k--){
                    pieceCorners.add(rowX.get(j+1).get(k));
                    pieceCorners.add(rowY.get(j+1).get(k));
                }
                //Left line corners
                pieceCorners.add(pieceX.get(bleft));
                pieceCorners.add(pieceY.get(bleft));
                for(int k = columnIntersectPoint.get(bleft); k > columnIntersectPoint.get(tleft); k--){
                    pieceCorners.add(columnX.get(i).get(k));
                    pieceCorners.add(columnY.get(i).get(k));
                }
                Double[] pieceCoordinateArray = new Double[pieceCorners.size()];
                for(int k = 0; k < pieceCorners.size(); k++){
                    pieceCoordinateArray[k] = pieceCorners.get(k);
                    if(i*5+j == 5){
//                        System.out.print(pieceCoordinateArray[k] + ", ");
                    }

                }
                boardPieces.add(new Piece(pieceID, pieceCoordinateArray));
                pieceID++;
                if(i*5+j == 5){
//                    System.out.println();
//                    System.out.println(pieceCorners);
                }
            }
        }

        controller.setBoardPieces(boardPieces);
    }

    // Method assigning neighbours to pieces
    public void setAdjacentPieces() {
        int rows = controller.ROWS;
        int columns = controller.COLUMNS;
        ArrayList<Integer> topRowIndexes = new ArrayList<Integer>(0);
        ArrayList<Integer> bottomRowIndexes = new ArrayList<Integer>(rows-1);

        for(int i = 1; i < rows; i++) {
            topRowIndexes.add(i*rows);
            bottomRowIndexes.add(i*rows + (rows-1));
        }

        for(Piece p : boardPieces) {
            int pieceID = p.getPieceID();
            // Corners
            if(pieceID == 0) {
                p.addAdjacentPiece(boardPieces.get(pieceID+1));
                p.addAdjacentPiece(boardPieces.get(pieceID+rows));
            } else if(pieceID == rows-1) {
                p.addAdjacentPiece(boardPieces.get(pieceID-1));
                p.addAdjacentPiece(boardPieces.get(pieceID+rows));
            } else if(pieceID == rows*(columns-1)) {
                p.addAdjacentPiece(boardPieces.get(pieceID+1));
                p.addAdjacentPiece(boardPieces.get(pieceID-rows));
            } else if(pieceID == rows*columns-1) {
                p.addAdjacentPiece(boardPieces.get(pieceID-1));
                p.addAdjacentPiece(boardPieces.get(pieceID-rows));
            } else {
                // Left border
                if(pieceID < rows) {
                    Piece pTemp = boardPieces.get(p.getPieceID()+rows);
                    if(!p.getAdjacentPieces().containsKey(pTemp)) {
                        p.addAdjacentPiece(pTemp);
                        p.addAdjacentPiece(boardPieces.get(pieceID-1));
                        p.addAdjacentPiece(boardPieces.get(pieceID+1));
                    }
                }
                // Right border
                else if(pieceID >= rows*(columns-1)) {
                    Piece pTemp = boardPieces.get(p.getPieceID()-rows);
                    if(!p.getAdjacentPieces().containsKey(pTemp)) {
                        p.addAdjacentPiece(pTemp);
                        p.addAdjacentPiece(boardPieces.get(pieceID-1));
                        p.addAdjacentPiece(boardPieces.get(pieceID+1));
                    }
                }
                // Top border
                else if(topRowIndexes.contains(pieceID)) {
                    Piece pTemp = boardPieces.get(p.getPieceID()+1);
                    if(!p.getAdjacentPieces().containsKey(pTemp)) {
                        p.addAdjacentPiece(pTemp);
                        p.addAdjacentPiece(boardPieces.get(pieceID+rows));
                        p.addAdjacentPiece(boardPieces.get(pieceID-rows));
                    }
                }
                // Bottom border
                else if(bottomRowIndexes.contains(pieceID)) {
                    Piece pTemp = boardPieces.get(p.getPieceID()-1);
                    if(!p.getAdjacentPieces().containsKey(pTemp)) {
                        p.addAdjacentPiece(pTemp);
                        p.addAdjacentPiece(boardPieces.get(pieceID+rows));
                        p.addAdjacentPiece(boardPieces.get(pieceID-rows));
                    }
                }
                // For all pieces in the middle
                else {
                    p.addAdjacentPiece(boardPieces.get(p.getPieceID()-1));
                    p.addAdjacentPiece(boardPieces.get(p.getPieceID()+1));
                    p.addAdjacentPiece(boardPieces.get(p.getPieceID()-rows));
                    p.addAdjacentPiece(boardPieces.get(p.getPieceID()+rows));
                }
            }
        }

    }
}
