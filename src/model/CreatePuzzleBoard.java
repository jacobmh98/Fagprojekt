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
    ArrayList<Integer> rowIntersectPoint = new ArrayList<>();
    ArrayList<Integer> columnIntersectPoint = new ArrayList<>();

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
        System.out.println(rowX);
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
                    y += (maxWidth*2) + Math.random() * (0.5*pieceHeight-maxWidth*2);
                    if(y > height-10) {
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
                    x += (maxHeight*2) + Math.random()*(0.5*piecewidth-maxHeight*2);
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
    
    public void assignCornerPoints(){
        for(int col = 0; col <= columns; col++){
            for(int row = 0; row <= rows; row++){
                int currentColumnPoint = 0;
                boolean foundIntersect = false;
                while(!foundIntersect && currentColumnPoint < columnX.get(col).size()-1){
                    int currentRowPoint = 0;
                    while(!foundIntersect && rowX.get(row).get(currentRowPoint) < col*piecewidth+maxWidth && currentRowPoint < rowX.get(row).size()-1){
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
            if(y3 == 500){
                System.out.println("IntersectX " + intersectX + ", x1: " + x1 + ", x2: " + x2);
            }
            if((intersectX >= x3 && intersectX <= x4) && ((intersectX >= x1 && intersectX <= x2) || (intersectX <= x1 && intersectX >= x2))){
                intersectingPoints[0] = intersectX;
                intersectingPoints[1] = m1*intersectX+b1;
            } else {
                intersectingPoints[0] = -1; //Used as a boolean
            }
        }
        return intersectingPoints;
    }
}
