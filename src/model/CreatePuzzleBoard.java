package model;


import java.util.ArrayList;

public class CreatePuzzleBoard {
    private int rows;
    private int columns;
    private int totalPieces;
    private int height;
    private int width;
    private double piecewidth;
    private double pieceHeight;
    private double maxWidth;
    private double maxHeight;
    private ArrayList<ArrayList<Double>> columnX = new ArrayList<>();
    private ArrayList<ArrayList<Double>> columnY = new ArrayList<>();
    private ArrayList<ArrayList<Double>> rowX = new ArrayList<>();
    private ArrayList<ArrayList<Double>> rowY = new ArrayList<>();

    ArrayList<Double> pieceX = new ArrayList<>();
    ArrayList<Double> pieceY = new ArrayList<>();

    //Getters
    public ArrayList<ArrayList<Double>> getColumnX() { return columnX; }

    public ArrayList<ArrayList<Double>> getColumnY() { return columnY; }

    public ArrayList<ArrayList<Double>> getRowX() { return rowX; }

    public ArrayList<ArrayList<Double>> getRowY() { return rowY; }

    public ArrayList<Double> getPieceX() {return pieceX;}

    public ArrayList<Double> getPieceY() {return pieceY;}

    public CreatePuzzleBoard(int rows, int columns, int height, int width){
        this.rows = rows;
        this.columns = columns;
        this.totalPieces = rows*columns;
        this.height = height;
        this.width = width;
        this.piecewidth=(double)width/columns;
        this.pieceHeight=(double)height/rows;
        this.maxWidth = piecewidth*0.3;
        this.maxHeight = pieceHeight*0.3;
    }

    public void createOneRowPuzzle(){
        createColumn(columns);
        createRows(rows);
        assignCornerPoints();
    }

    private void createColumn(int totalColumns) {
        for(int i = 0; i < totalColumns+1; i++){
            ArrayList<Double> columnListX = new ArrayList<>();
            ArrayList<Double> columnListY = new ArrayList<>();
            if(i != 0 && i != totalColumns) {
                double y = 0;
                columnListX.add(i*piecewidth);
                columnListY.add(y);
                while (y < height) {
                    double x = (i * piecewidth - maxWidth) + Math.random() * (i * piecewidth + maxWidth - (i * piecewidth - maxWidth));
                    //y needs to be atleast the same length as x can vary (angle can vary atmost 45 degrees from vertical line)
                    y += (maxWidth*2) + Math.random() * (0.5*pieceHeight-maxWidth*2)*0;
                    if(y > height) {
                        y = height;
                    }
                    columnListY.add(y);
                    columnListX.add(x);
                }
            } else {
                columnListX.add(i*piecewidth);
                columnListX.add(i*piecewidth);
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
                    x += (maxHeight*2) + Math.random()*(0.5*piecewidth-maxHeight*2)*0;
                    double y = (i * pieceHeight - maxHeight) + Math.random() * (i * pieceHeight + maxHeight - (i * pieceHeight - maxWidth));
                    if(x > width){
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

    public void assignCornerPoints(){
        //row[i] & col[i]
        //row[i] & col[i+1]
        //row[i+1] & col[i]
        //row[i+1] & col[i+1]
        //When not on first col - col[i][1] = col[i-1][2] and col[i][4] = col[i-1][3]
        //When not on first row - row[i][1] = row[i-1][4] and row[i][2] = row[i-1][3]
        
        for(int col = 0; col < 1; col++){
            for(int row = 0; row < 1; row++){
                for(int i = 0; i < 2; i++){
                    for(int j = 0; j < 2; j++){
                        int currentColumnPoint = 0;
                        boolean foundIntersect = false;
                        while(!foundIntersect && currentColumnPoint < columns-1){
                            int currentRowPoint = 0;
                            while(rowX.get(row+i).get(currentRowPoint) < ((i+col)*piecewidth+maxWidth) && !foundIntersect){ //checks if row x coordinate has crossed max for current column
                                double[] intersectingPoints = intersect(columnX.get(col+j).get(currentColumnPoint),columnY.get(col+j).get(currentColumnPoint),
                                                                        columnX.get(col+j).get(currentColumnPoint+1), columnY.get(col+j).get(currentColumnPoint+1),
                                                                        rowX.get(row+i).get(currentRowPoint), rowY.get(row+i).get(currentRowPoint),
                                                                        rowX.get(row+i).get(currentRowPoint+1), rowY.get(row+i).get(currentRowPoint+1));
                                if(intersectingPoints[0] < 0){ // if it didn't intersect
                                    currentRowPoint++;
                                } else {
                                    foundIntersect = true;
                                    pieceX.add(intersectingPoints[0]);
                                    pieceY.add(intersectingPoints[1]);
                                }
                            }
                            currentColumnPoint++;
                        }
                    }
                }
            }
        }
        System.out.println(pieceX);
        System.out.println(pieceY);
    }
    //Column points, row points
    public double[] intersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
        double[] intersectingPoints = new double[2];
        double m1, m2, b1, b2;
        if(x1-x2 == 0) {
            m2 = (y4-y3)/(x4-x3);
            b2 = y3-x3*m2;
            double yAtX = m2*x1+b2;
            if(yAtX < y2 && yAtX > y1){
                intersectingPoints[0] = x1;
                intersectingPoints[1] = yAtX;
            }
        }   else {
            //Finding the formula for the two lines
            m1 = (y2-y1)/(x2-x1);
            m2 = (y4-y3)/(x4-x3);
            b1 = y1-x1*m1;
            b2 = y3-x3*m2;
            //Check the x value the two lines intersect
            double intersectX = ((b2-b1)/(m1-m2));
            if((intersectX >= x3 && intersectX <= x4) && ((intersectX >= x1 && intersectX <= x2) || (intersectX <= x1 && intersectX >= x2))){ //We know that x3 < x4 always
                intersectingPoints[0] = intersectX;
                intersectingPoints[1] = m1*intersectX+b1;
            } else {
                intersectingPoints[0] = -1; //Used as a boolean
            }
        }
        return intersectingPoints;
    }
}
