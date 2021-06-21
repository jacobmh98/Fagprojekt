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

    //Getter for board pieces
    public ArrayList<Piece> getBoardPieces() {return boardPieces;}


    // Object initialize
    // Inputs - Non, but it uses controller variables to set its own initial values
    // Output - This object
    // Written by Oscar
    public CreatePuzzleBoard(){
        this.rows = controller.getRows();
        this.columns = controller.getColumns();
        this.height = controller.getBoardSize()[1];
        this.width = controller.getBoardSize()[0];
        this.pieceWidth =(double)width/columns;
        this.pieceHeight=(double)height/rows;
        this.maxWidth = pieceWidth *0.3;
        this.maxHeight = pieceHeight*0.3;
    }

    // The main method that create the board pieces and also checks if the boardpieces are unique or not
    // Input - Non, it uses it's own variable set in the constructor
    // Output - Non, but it creates a list that can be extracted from the getters
    // Written by Oscar
    public void createPuzzle(){
        createColumn(columns);
        createRows(rows);
        findIntersectPoints();
        defineBoardPieceCorners();
        while(ComparePieces.checkForDuplicates(boardPieces)){
            clearData();
            createColumn(columns);
            createRows(rows);
            findIntersectPoints();
            defineBoardPieceCorners();
        }
        setAdjacentPieces();
    }

    // Method that generates all the points that are going to make up the column lines
    // Input - The amount of columns
    // Output - It sets the two global list of coordinates columnListX and columnListY
    // Written by Oscar
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
                    y += maxWidth*2;
                    if(y > height-10) { //Subtract 10 avoid having really small lines at the end
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

    // Method that generates all the points that are going to make up the row lines
    // Input - The amount of rows
    // Output - It sets the two global list of coordinates rowListX and rowListY
    // Written by Oscar
    private void createRows(int rows){
        for(int i = 0; i < rows+1; i++){
            ArrayList<Double> rowListX = new ArrayList<>();
            ArrayList<Double> rowListY = new ArrayList<>();
            if(i != 0 && i != rows){
                double x = 0;
                rowListX.add(x);
                rowListY.add(i*pieceHeight);
                while(x < width){
                    x += maxHeight*2;
                    double y = (i * pieceHeight - maxHeight) + Math.random() * (i * pieceHeight + maxHeight - (i * pieceHeight - maxWidth));
                    if(x > width-10){ //Subtract 10 avoid having really small lines at the end
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

    // Method that goes through all line segments to find where they intersect and which intersect
    // input - Non, Uses the global variables of amount of columns and rows, and the four lists that tells all the points that make up the board
    // Output - Non, It creates a global list that contains the index of the starting points of all intersecting line points
    // One for both the rows and columns. It also creates the intersection coordinates that it puts in two global lists
    // Written by Oscar
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

    // Method that given the coordinates of two line segments finds the coordinates where they intersect or
    // whether they don't intersect at all
    // input - Coordinates for four points, first two points makes up the column line segment second two point makes up
    // the row segment
    // Output - an array of the x coordinate and y coordinate where they intersect, if they do not intersect the first
    // value of the array is set to -1
    // Written by Oscar
    public double[] intersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
        double[] intersectingPoints = new double[2];
        double m1, m2, b1, b2;
        if(x1-x2 == 0) { //If column line is vertical we can't use the two point formula
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
                intersectingPoints[0] = -1; //Used as a "boolean" to check if there were an intersection
            }
        }
        return intersectingPoints;
    }

    // Method that creates the pieces themself by using the intersect corners it goes from
    // topleft->topright->bottomright->bottomleft->topleft and adds all corners inbetween them aswell to the piece
    // Input - Non, it uses the global variables for columns,rows and the lists that contain all the coordinates for
    // column and row points and the intersectpoints
    // Output - Non, but it sets the boardpieces in the controller class, and creates a global list that contains all the board pieces
    // Written by Oscar
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
                }
                boardPieces.add(new Piece(pieceID, pieceCoordinateArray));
                pieceID++;
            }
        }
        controller.setBoardPieces(boardPieces);
    }

    // Method assigning neighbours to pieces by using the way we have defined the pieceIDs
    // Input - Non, but it uses controller variables and the global list of boarpieces
    // Output - Non, but it has set all the neighbours of each piece object
    // Written by Jacob
    public void setAdjacentPieces() {
        int rows = controller.getRows();
        int columns = controller.getColumns();
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
                if(boardPieces.size() > pieceID+1) {  //Incase of 1 row puzzle
                    p.addAdjacentPiece(boardPieces.get(pieceID + 1));
                }
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

    // Method that clears all the global lists used in this class, used when generating a new board because the previous was not unique
    // Input - Non, but it uses all the global lists
    // Output - Non, but all the global lists will be empty
    // Written by Oscar
    private void clearData(){
        columnX = new ArrayList<>();
        columnY = new ArrayList<>();
        rowY = new ArrayList<>();
        rowX = new ArrayList<>();
        pieceX = new ArrayList<>();
        pieceY = new ArrayList<>();
        boardPieces = new ArrayList<>();
        rowIntersectPoint = new ArrayList<>();
        columnIntersectPoint = new ArrayList<>();
    }
}
