package model;

import org.kynosarges.tektosyne.geometry.*;

public class VoronoiBoard {
    private final int points;
    private final int width;
    private final int height;
    private final PointD[] randomPoints;
    private final VoronoiResults v;
    private Piece[] pieces;
    private final RectD clip;

    public VoronoiBoard(int points, int width, int height) throws Exception {
        this.points = points;
        this.width = width;
        this.height = height;
        randomPoints = createRandomPoints();
        this.clip = new RectD(0,0, width, height);
        v = Voronoi.findAll(randomPoints, clip); //Use of the library tektosyne to find the voronoi regions
        pieces = new Piece[points];
        createPiecesArray();
        findNeighbours();
    }

    public Piece[] getPieces(){return pieces;}

    public PointD[] getRandomPoints(){return randomPoints;}

    private void createPiecesArray(){
        for(LineD p : v.delaunayEdges()) {
            System.out.println(p);
        }
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

    private PointD[] createRandomPoints(){
        PointD[] pointsArray = new PointD[points];
        for(int i = 0; i < points; i++){
            //double x = (0.1*width) + Math.random()*(width*0.9-0.1*width);
            //double y = (0.1*height) + Math.random()*(height*0.9-0.1*height);
            double x = Math.random()*width;
            double y = Math.random()*height;
            PointD pointD = new PointD(x, y);
            pointsArray[i] = pointD;
        }
        return pointsArray;
    }

    private void findNeighbours(){
        for(int i = 0; i < v.delaunayEdges().length; i++){
            int piece1 = findPieceID(v.delaunayEdges()[i].start.x, v.delaunayEdges()[i].start.y);
            int piece2 = findPieceID(v.delaunayEdges()[i].end.x, v.delaunayEdges()[i].end.y);
            if(piece1 == -1 || piece2 == -1){
                System.out.println("Error finding matching x and y to a piece");
            }
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
