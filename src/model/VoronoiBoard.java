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

    public Piece[] getPieces(){return pieces;}

    public PointD[] getRandomPoints(){return randomPoints;}

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

    private PointD findIntersect(double x1, double y1, double x2, double y2){
        PointD newPoint = null;
        double m = (y1-y2)/(x1-x2);
        double b = (y1-m*x1);
        if(x1 < 0){
            if(b > 0 && b < HEIGHT){
                newPoint = new PointD(0, b);
            }
        } else if (x1 > WIDTH){
            double yAtX = m*WIDTH+b;
            if(yAtX > 0 && yAtX < HEIGHT){
                newPoint = new PointD(WIDTH, yAtX);
            }
        }
        if (y1 < 0){
            double xAtY = -b/m;
            if(xAtY > 0 && xAtY < WIDTH){
                newPoint = new PointD(xAtY,0);
            }
        } else if (y1 > HEIGHT){
            double xAtY = (HEIGHT-b)/m;
            if(xAtY > 0 && xAtY < WIDTH){
                newPoint = new PointD(xAtY,HEIGHT);
            }
        }
        return newPoint;
    }

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

    private void findNeighbours(){
        for(int i = 0; i < v.delaunayEdges().length; i++){
            int piece1 = findPieceID(v.delaunayEdges()[i].start.x, v.delaunayEdges()[i].start.y);
            int piece2 = findPieceID(v.delaunayEdges()[i].end.x, v.delaunayEdges()[i].end.y);
            pieces[piece1].addAdjacentPiece(pieces[piece2]);
            pieces[piece2].addAdjacentPiece(pieces[piece1]);
        }
    }

    private int findPieceID(double x, double y){
        for(int i = 0; i < v.generatorSites.length; i++){
            if(v.generatorSites[i].x == x && v.generatorSites[i].y == y){
                return i;
            }
        }
        return -1;
    }


}
