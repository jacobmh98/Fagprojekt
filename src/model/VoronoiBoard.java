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
        //fixPieceEdges();
        createPiecesArray();
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

    private void fixPieceEdges(){
        for(PointD[] region : v.voronoiRegions()){
            for(int i = 0; i < region.length; i++){
                PointD point1 = region[i];
                if(point1.x < 0 || point1.x > width || point1.y < 0 || point1.y > height) {
                    PointD point2;
                    if (i > 0) {
                        point2 = region[i - 1];
                        if(point2.x < 0 || point2.x > width || point2.y < 0 || point2.y > height){
                            point2 = region[i + 1];
                        }
                    } else {
                        point2 = region[i + 1];
                    }
                    PointD newPoint = findIntersect(point1.x, point1.y, point2.x, point2.y);
                    region[i] = newPoint;
                    System.out.println("HELLO");
                }
            }
        }
    }

    private PointD findIntersect(double x1, double y1, double x2, double y2){
        PointD newPoint = null;
        double m = (y1-y2)/(x1-x2);
        double b = (y1-m*x1);
        if(x1 < 0){
            if(b > 0 && b < height){
                newPoint = new PointD(0, b);
            }
        } else if (x1 > width){
            double yAtX = m*width+b;
            if(yAtX > 0 && yAtX < height){
                newPoint = new PointD(width, yAtX);
            }
        }
        if (y1 < 0){
            double xAtY = -b/m;
            if(xAtY > 0 && xAtY < width){
                newPoint = new PointD(xAtY,0);
            }
        } else if (y1 > height){
            double xAtY = (height-b)/m;
            if(xAtY > 0 && xAtY < width){
                newPoint = new PointD(xAtY,height);
            }
        }
        return newPoint;
    }

    private PointD[] createRandomPoints(){
        PointD[] pointsArray = new PointD[points];
        for(int i = 0; i < points; i++){
            double x = (0.05*width) + Math.random()*(width*0.95-0.05*width);
            double y = (0.05*height) + Math.random()*(height*0.95-0.05*height);
            //double x = Math.random()*width;
            //double y = Math.random()*height;
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
