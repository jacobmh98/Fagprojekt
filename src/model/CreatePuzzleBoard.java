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

    //Getters
    public ArrayList<ArrayList<Double>> getColumnX() { return columnX; }

    public ArrayList<ArrayList<Double>> getColumnY() { return columnY; }

    public ArrayList<ArrayList<Double>> getRowX() { return rowX; }

    public ArrayList<ArrayList<Double>> getRowY() { return rowY; }

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
                    y += (piecewidth-maxWidth) + Math.random() * (0.5*pieceHeight-(piecewidth-maxWidth));
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
                    x += (pieceHeight-maxHeight) + Math.random()*(0.5*piecewidth-(pieceHeight-maxHeight));
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
}
