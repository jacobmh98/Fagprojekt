package model;

import org.kynosarges.tektosyne.geometry.PointD;
import org.kynosarges.tektosyne.geometry.RectD;
import org.kynosarges.tektosyne.geometry.Voronoi;
import org.kynosarges.tektosyne.geometry.VoronoiResults;


import java.util.ArrayList;
import java.util.Arrays;

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
        v = Voronoi.findAll(randomPoints, clip);
        pieces = new Piece[points];
        createPiecesArray();
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

    private PointD[] createRandomPoints(){
        PointD[] pointsArray = new PointD[points];
        for(int i = 0; i < points; i++){
            double x = (0.1*width) + Math.random()*(width*0.9-0.1*width);
            double y = (0.1*height) + Math.random()*(height*0.9-0.1*height);
            PointD pointD = new PointD(x, y);
            pointsArray[i] = pointD;
        }
        return pointsArray;
    }


}
