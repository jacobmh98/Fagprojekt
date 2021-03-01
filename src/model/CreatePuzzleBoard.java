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

    //------------------ NOTE CURRENTLY TOP AND BOTTOM X VALUES CAN VARY ON THE COLUMNS -----------------------------
    //------------------ ONE ROW PUZZLE IS HARDCODED ----------------------------------------------------------------
    public void createOneRowPuzzle(){
        //[ROW][COLUMN]
        //We display points as they appear visually in their lines
        //create coordinates for columns
        for(int i = 0; i < columns+1; i++){
            ArrayList<Double> columnListX = new ArrayList<>();
            ArrayList<Double> columnListY = new ArrayList<>();
            ArrayList<Double> rowListX = new ArrayList<>();
            ArrayList<Double> rowListY = new ArrayList<>();
            for(int j = 0; j < rows+1; j++){
                if(i != 0 && i != columns){
                    double x = (i*piecewidth-maxWidth)+Math.random()*(i*piecewidth+maxWidth-(i*piecewidth-maxWidth));
                    columnListX.add(x);
                } else {
                    columnListX.add(i*piecewidth);
                }
                columnListY.add(j*pieceHeight);
                if(i <= rows) {
                    rowListX.add((double)j*width);
                    rowListY.add((double)i*height);
                }

            }
            columnX.add(columnListX);
            columnY.add(columnListY);
            if(i <= rows) {
                rowX.add(rowListX);
                rowY.add(rowListY);
            }
        }

    }
}
