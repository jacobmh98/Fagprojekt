import model.ComparePieces;
import model.CreatePuzzleBoard;
import model.Piece;
import model.VoronoiBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CreateBoardTests {

    @Test
    void testRowColTotalPieces(){
        CreatePuzzleBoard board = new CreatePuzzleBoard(10, 10, 1000, 1000);
        board.createOneRowPuzzle();
        int totalPieces = board.getBoardPieces().size();
        assertEquals(100, totalPieces);
    }

    @Test
    void testRowColNeighbours(){
        int rows = 10;
        int cols = 10;
        CreatePuzzleBoard board = new CreatePuzzleBoard(rows,cols,1000,1000);
        board.createOneRowPuzzle();
        int corner = 4;
        int side = (rows-2)*2+(cols-2)*2;
        int middle = (rows*cols)-corner-side;
        ArrayList<Piece> pieces = board.getBoardPieces();
        for (Piece piece : pieces) {
            int neighbours = piece.getAdjacentPieces().size();
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
    void testVoronoiTotalPieces() throws Exception {
        int totalPieces = 59;
        VoronoiBoard board = new VoronoiBoard(59, 1000, 1000);
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
        Double[] piece2Original = pieceList.get(1).getCorners();
        pieceList.get(0).movePiece(10+pieceList.get(0).getCenter()[0],10+pieceList.get(0).getCenter()[1]);
        assertArrayEquals(piece2Original, pieceList.get(1).getCorners());
    }
}
