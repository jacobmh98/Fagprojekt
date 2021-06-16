// This class uses the library tektosyne from https://github.com/kynosarges/tektosyne
package model;

import controller.Controller;
import org.kynosarges.tektosyne.geometry.*;

public class VoronoiBoard {
    private final int POINTS;
    private final int WIDTH;
    private final int HEIGHT;
    private PointD[] randomPoints;
    private VoronoiResults v;
    private Piece[] pieces;
    private final RectD CLIP;
    private Controller controller = Controller.getInstance();

    // Object initializer that also creates all the pieces based of the tektosyne library
    // Input - amount of points that it needs to generate pieces based of and it uses the Controller variables to set
    // other global variables
    // Output - This object and sets the global list containing all pieces
    // Written by Oscar
    public VoronoiBoard(int points) {
        this.POINTS = points;
        this.WIDTH = controller.getBoardSize()[0];
        this.HEIGHT = controller.getBoardSize()[1];
        randomPoints = createRandomPoints();
        this.CLIP = new RectD(0,0, WIDTH, HEIGHT);
        v = Voronoi.findAll(randomPoints, CLIP); //Use of the library tektosyne to find the voronoi regions
        pieces = new Piece[points];
        createPiecesArray();
        while(ComparePieces.checkForDuplicates(pieces)){
            randomPoints = createRandomPoints();
            v = Voronoi.findAll(randomPoints, CLIP);
            createPiecesArray();
        }
        findNeighbours();
    }

    // Getters
    public Piece[] getPieces(){return pieces;}

    public PointD[] getRandomPoints(){return randomPoints;}

    // Method that creates the Piece objects based of the voronoi regions that the tektosyne library created
    // input - Non, it uses the Voronoi object from the tektosyne library
    // Output - Non, it sets the entries of the global array pieces that contains all the Piece object
    // Written by Oscar
    private void createPiecesArray(){
        int pieceID = 0;
        for(PointD[] region : v.voronoiRegions()){
            double[] regionDoubles = PointD.toDoubles(region);
            Double[] pieceCorners = new Double[regionDoubles.length];
            for(int i = 0; i < regionDoubles.length; i++){
                pieceCorners[i] = regionDoubles[i];
            }
            Piece piece = new Piece(pieceID, pieceCorners);
            pieces[pieceID] = piece;
            pieceID++;
        }
    }


    // Method that creates the random points used to create the voronoi regions, all points are 5% of the boardsize away from the edge
    // Input - It uses the global variable points
    // Output - it returns an PointD object array (from the tektosyne library) that contains all the coordinates generated
    // Written by Oscar
    private PointD[] createRandomPoints(){
        PointD[] pointsArray = new PointD[POINTS];
        for(int i = 0; i < POINTS; i++){
            double x = (0.05*WIDTH) + Math.random()*(WIDTH*0.95-0.05*WIDTH);
            double y = (0.05*HEIGHT) + Math.random()*(HEIGHT*0.95-0.05*HEIGHT);
            PointD pointD = new PointD(x, y);
            pointsArray[i] = pointD;
        }
        return pointsArray;
    }

    // Method that sets the neighbours of all the pieces
    // Input - Non, it uses the global Voronoi object and global piece array
    // Output - Non, but it has set all the neighbours for all the Piece objects
    // Written by Oscar
    private void findNeighbours(){
        for(int i = 0; i < v.delaunayEdges().length; i++){
            int piece1 = findPieceID(v.delaunayEdges()[i].start.x, v.delaunayEdges()[i].start.y);
            int piece2 = findPieceID(v.delaunayEdges()[i].end.x, v.delaunayEdges()[i].end.y);
            pieces[piece1].addAdjacentPiece(pieces[piece2]);
            pieces[piece2].addAdjacentPiece(pieces[piece1]);
        }
    }

    // Method finds the pieceID based on the generator site of the piece (the point which the piece is made from
    // not the same as piece center)
    // Input - The x and y coordinate of the generator site
    // Output - The pieceID of the piece with that given generator site
    //Written by Oscar
    private int findPieceID(double x, double y){
        for(int i = 0; i < v.generatorSites.length; i++){
            if(v.generatorSites[i].x == x && v.generatorSites[i].y == y){
                return i;
            }
        }
        return -1;
    }


}
