import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CreateBoardTests {

    @Test
    void testRowColTotalPieces(){
        TestData.setControllerValues();
        Controller.getInstance().setColumns(3);
        Controller.getInstance().setRows(3);
        CreatePuzzleBoard board = new CreatePuzzleBoard();
        board.createPuzzle();
        int totalPieces = board.getBoardPieces().size();
        assertEquals(9, totalPieces);
    }

    @Test
    void testRowColNeighbours(){
        //Requires rows and cols to be greater than 1
        TestData.setControllerValues();
        int rows = 3;
        int cols = 3;
        Controller.getInstance().setRows(rows);
        Controller.getInstance().setColumns(cols);
        CreatePuzzleBoard board = new CreatePuzzleBoard();
        board.createPuzzle();
        int corner = 4;
        int side = (rows-2)*2+(cols-2)*2;
        int middle = (rows*cols)-corner-side;
        ArrayList<Piece> pieces = board.getBoardPieces();
        for (Piece piece : pieces) {
            int neighbours = piece.getAdjacentPieces().size();
            System.out.println(neighbours);
            assertTrue(neighbours >= 2 && neighbours <= 4);
            if (neighbours == 2) {
                corner--;
            } else if (neighbours == 3) {
                side--;
            } else if (neighbours == 4) {
                middle--;
            }
        }
        assertEquals(0,corner);
        assertEquals(0, side);
        assertEquals(0, middle);
    }

    @Test
    void testOneRowNeighbours(){
        int row = 1;
        int cols = 5;
        TestData.setControllerValues();
        Controller.getInstance().setColumns(cols);
        Controller.getInstance().setRows(row);
        CreatePuzzleBoard board = new CreatePuzzleBoard();
        board.createPuzzle();
        int side = 2;
        int middle = cols-2;
        ArrayList<Piece> pieces = board.getBoardPieces();
        for(Piece piece : pieces){
            int neighbours = piece.getAdjacentPieces().size();
            assertTrue(neighbours == 1 || neighbours == 2);
            if(neighbours == 1){
                side--;
            } else {
                middle--;
            }
        }
        assertEquals(0, side);
        assertEquals(0, middle);
    }

    @Test
    void testVoronoiTotalPieces() throws Exception {
        int totalPieces = 59;
        VoronoiBoard board = new VoronoiBoard(59);
        Piece[] pieces = board.getPieces();
        assertEquals(totalPieces, pieces.length);
    }

    @Test
    void testComparePieces03(){
        ArrayList<Double[]> pieceList = TestData.getComparePieces03();
        //Pairs that match is (1,2) (3,4) (5,6) (7,8)
        for(int i = 0; i < 8; i++){
            for(int j = i; j < 8; j++){
                if(j != i){
                    if((i == 0 || i == 2 || i == 4 || i == 6) && j == i+1){
                        assertTrue(ComparePieces.comparePieces(pieceList.get(i), pieceList.get(j)));
                    } else {
                        assertFalse(ComparePieces.comparePieces(pieceList.get(i),pieceList.get(j)));
                    }
                }
            }
        }
    }

    @Test
    void testComparePieces04(){
        ArrayList<Double[]> pieceList = TestData.getComparePieces04();
        //All are equal but some has extra points put into them
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(j != i){
                    assertTrue(ComparePieces.comparePieces(pieceList.get(i),pieceList.get(j)));
                }
            }
        }
    }

    @Test
    void testMovePiece(){
        Piece piece = TestData.get1Piece();
        Double[] pieceOriginal = piece.getCorners().clone();
        Double moveX = 10.0;
        Double moveY = 3.0;
        piece.movePiece(moveX+piece.getCenter()[0], moveY+piece.getCenter()[1]);
        for(int i = 0; i < pieceOriginal.length; i+=2){
            assertEquals(pieceOriginal[i]+moveX, piece.getCorners()[i]);
            assertEquals(pieceOriginal[i+1]+moveY, piece.getCorners()[i+1]);
        }
    }

    @Test
    void testMoveUnconnectedNeighbourPieces(){
        ArrayList<Piece> pieceList = TestData.getUnconnectedNeighbourPieces();
        Double[] piece2Original = pieceList.get(1).getCorners().clone();
        pieceList.get(0).movePiece(10+pieceList.get(0).getCenter()[0],10+pieceList.get(0).getCenter()[1]);
        assertArrayEquals(piece2Original, pieceList.get(1).getCorners());
    }

    @Test
    void testMovingOfConnectedPieces(){
        ArrayList<Piece> pieceList = TestData.getConnectedNeighboursPieces();
        Double[] piece2Original = pieceList.get(1).getCorners().clone();
        Double moveX = 10.0;
        Double moveY = 3.0;
        pieceList.get(0).movePiece(moveX+pieceList.get(0).getCenter()[0], moveY+pieceList.get(0).getCenter()[1]);
        for(int i = 0; i < piece2Original.length; i+=2){
            assertEquals(piece2Original[i]+moveX, pieceList.get(1).getCorners()[i]);
            assertEquals(piece2Original[i+1]+moveY, pieceList.get(1).getCorners()[i+1]);
        }
    }


    @Test
    void testPieceUniqueness(){
        TestData.setControllerValues();
        int points = 50;
        VoronoiBoard board = new VoronoiBoard(50);
        Piece[] boardPieces = board.getPieces();
        assertFalse(ComparePieces.checkForDuplicates(boardPieces));
    }


    @Test
    void testNoSolution() throws Exception {
        TestData.setControllerValues();
        ArrayList<Piece> boardPieces = JsonImport.readJson("NoSolutionTest.json", false);
        Controller.getInstance().setBoardPieces(boardPieces);
        SolvePuzzle solver = new SolvePuzzle(false);
        solver.solveByCorners();
        assertFalse(solver.checkIfSolved(Controller.getInstance().getBoardPieces()));
    }


    @Test
    void testWithSolution() throws Exception {
        TestData.setControllerValues();
        ArrayList<Piece> boardPieces = JsonImport.readJson("WithSolutionTest.json", false);
        Controller.getInstance().setBoardPieces(boardPieces);
        SolvePuzzle solver = new SolvePuzzle(false);
        solver.solveByCorners();
        assertTrue(solver.checkIfSolved(Controller.getInstance().getBoardPieces()));
    }


    @Test
    void testMovingOfNonNeighbours(){
        ArrayList<Piece> pieces = TestData.getTwoPieces();
        Double[] piece2Old = pieces.get(1).getCenter().clone();
        pieces.get(0).checkForConnect();
        pieces.get(0).movePiece(10,10);
        Double[] piece2New = pieces.get(1).getCenter();
        for(int i = 0; i < piece2Old.length; i++){
            assertEquals(piece2Old[i],piece2New[i]);
        }
    }


    @Test
    void testMovingOfNeighbours(){
        ArrayList<Piece> pieces = TestData.getTwoPieces();
        Double[] piece2Old = pieces.get(1).getCenter().clone();
        pieces.get(0).addAdjacentPiece(pieces.get(1));
        pieces.get(0).checkForConnect();
        pieces.get(0).movePiece(10,10);
        Double[] piece2New = pieces.get(1).getCenter();
        for(int i = 0; i < piece2Old.length; i++){
            assertFalse(piece2Old == piece2New);
        }
    }


    @Test
    void testRotationOfNonNeighbours(){
        ArrayList<Piece> pieces = TestData.getTwoPieces();
        Double[] piece2Old = pieces.get(1).getCorners().clone();
        pieces.get(0).checkForConnect();
        pieces.get(0).rotatePiece(Math.PI/2);
        Double[] piece2New = pieces.get(1).getCorners();
        for(int i = 0; i < piece2Old.length; i++){
            assertEquals(piece2Old[i],piece2New[i]);
        }
    }


    @Test
    void testRotationOfNeighbours(){
        ArrayList<Piece> pieces = TestData.getTwoPieces();
        Double[] piece2Old = pieces.get(1).getCorners().clone();
        pieces.get(0).addAdjacentPiece(pieces.get(1));
        pieces.get(0).checkForConnect();
        pieces.get(0).rotatePiece(Math.PI/2);
        Double[] piece2New = pieces.get(1).getCorners();
        for(int i = 0; i < piece2Old.length; i++){
            assertFalse(piece2Old == piece2New);
        }
    }
}
