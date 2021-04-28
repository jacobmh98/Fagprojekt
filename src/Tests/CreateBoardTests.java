import model.CreatePuzzleBoard;
import model.Piece;
import model.VoronoiBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        for(int i = 0; i < pieces.size(); i++){
            int neighbours = pieces.get(i).getAdjacentPieces().size();
            assertTrue(neighbours >= 2 && neighbours <= 4);
            if(neighbours == 2){
                corner--;
            } else if(neighbours == 3){
                side--;
            } else if(neighbours == 4){
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
}
