import controller.Controller;
import model.Piece;

import java.util.ArrayList;

public class TestData {

    public static Controller setControllerValues(){
        Controller controller = Controller.getInstance();
        controller.setBoardSize(1000,1000);
        return controller;
    }

    public static ArrayList<Double[]> getComparePieces03(){
        Double[] piece1 = {0.0,0.0,1.0,0.0,1.0,1.0,0.6,1.0,0.5,1.4,0.4,1.0,0.0,1.0};
        Double[] piece2 = {1.5622530923125946,2.192909539629902,2.4230311324075267,2.7018900556522025, 1.914050616385226, 3.5626680957471346, 1.5697394003472531, 3.3590758893382144, 1.2800693899288398, 3.652489053773957, 1.3975837923282668, 3.257279786133754, 1.053272576290294, 3.053687579724834};
        Double[] piece3 = {2.01, 1.33,3.01,1.33,3.01,2.33,2.61,2.33,2.51,1.9300000000000002,2.4099999999999997,2.33,2.01,2.33};
        Double[] piece4 = {2.029416162843801,2.2290932704044963,1.3302692219070182,2.944071280540989,0.6152912117705258,2.2449243396042062,0.8949499881452387,1.958933135549609,1.2508558862935144,2.1670941109106727,1.0347793763325954,1.8159375335223102,1.3144381527073088,1.5299463294677134};
        Double[] piece5 = {3.2,0.8,3.6,0.8,3.7,0.4,3.8000000000000003,0.8,4.2,0.8,4.2,1.8,3.8000000000000003,1.8,3.7,2.2,3.6,1.8,3.2,1.8};
        Double[] piece6 = {4.888549266696887,2.2370140597660138,4.610323936111726,1.9496281425383635,4.253381686237785,2.1560069938166113,4.471211270819146,1.8059351839245381,4.192985940233985,1.5185492666968874,4.911450733303111,0.8229859402339851,5.189676063888272,1.1103718574616357,5.546618313762212,0.9039930061833874,5.328788729180853,1.254064816075461,5.607014059766013,1.5414507333031113};
        Double[] piece7 = {-0.44111867475189065,4.102365866943431,-0.3496175516285069,3.712972050265303,0.06265154583046666,3.707124719219155,-0.30386699006681506,3.5182751419262392,-0.21236586694343135,3.128881325248112,0.7611186747518885,3.357634133056571,0.6696175516285048,3.747027949734699,0.25734845416953067,3.752875280780847,0.6238669900668128,3.941724858073763,0.532365866943429,4.331118674751891};
        Double[] piece8 = {1.33935931037606,4.666805462579237,1.064893401193941,4.375827001697967,1.2872553847796813,4.028616477295531,0.9276604466028814,4.230337771257332,0.6531945374207626,3.9393593103760622,1.380640689623938,3.2531945374207645,1.6551065988060571,3.5441729983020345,1.4327446152203163,3.8913835227044715,1.792339553397117,3.68966222874267,2.066805462579236,3.98064068962394};
        ArrayList<Double[]> pieceList = new ArrayList<>();
        pieceList.add(piece1);
        pieceList.add(piece2);
        pieceList.add(piece3);
        pieceList.add(piece4);
        pieceList.add(piece5);
        pieceList.add(piece6);
        pieceList.add(piece7);
        pieceList.add(piece8);
        return pieceList;
    }

    public static ArrayList<Double[]> getComparePieces04(){
        Double[] piece1 = {2.9748245360213286,5.787722341558183,2.338177873246547,5.856140371182937,2.2651754639786716,6.492277658441816,2.901822126753453,6.423859628817063};
        Double[] piece2 = {3.110251064210817,6.01293277489443,2.8298952031336535,5.858334614621798,2.5495393420564905,5.703736454349166,2.1248013342223566,6.18289991779467,2.685513056376683,6.4920962383399345};
        Double[] piece3 = {2.55443606254282,5.5975482156777066,2.334917088238596,6.02467398160669,2.261744096803854,6.167049236916352,2.7531053933378815,6.577614793518948,3.0457973590768477,6.008113772280303};
        Double[] piece4 = {3.227479429719061,6.522175428146554,3.0836704085164106,6.2361352816273365,3.011765897915085,6.093115208367728,2.9398613873137593,5.950095135108119,2.318598604309244,6.105119502397536,2.462407625511895,6.391159648916754,2.606216646714546,6.677199795435972};
        ArrayList<Double[]> pieceList = new ArrayList<>();
        pieceList.add(piece1);
        pieceList.add(piece2);
        pieceList.add(piece3);
        pieceList.add(piece4);
        return pieceList;
    }

    public static Piece get1Piece(){
        Double[] pieceCorners = {3.0, 3.0, 3.0, 5.0, 5.0, 6.0, 5.0, 3.0};
        return new Piece(0, pieceCorners);
    }

    public static ArrayList<Piece> getUnconnectedNeighbourPieces(){
        ArrayList<Piece> pieceList = new ArrayList<>();
        Double[] piece1Corners = {3.0, 3.0, 3.0, 5.0, 5.0, 6.0, 5.0, 3.0};
        Double[] piece2Corners = {3.0, 3.0, 2.0, 5.0, 2.0, 7.0, 3.0, 5.0};
        Piece piece1 = new Piece(0, piece1Corners);
        Piece piece2 = new Piece(1, piece2Corners);
        piece1.addAdjacentPiece(piece2);
        piece2.addAdjacentPiece(piece1);
        pieceList.add(piece1);
        pieceList.add(piece2);
        Controller.getInstance().setBoardPieces(pieceList);
        return pieceList;
    }

    public static ArrayList<Piece> getConnectedNeighboursPieces(){
        ArrayList<Piece> pieceList = new ArrayList<>();
        Double[] piece1Corners = {3.0, 3.0, 3.0, 5.0, 5.0, 6.0, 5.0, 3.0};
        Double[] piece2Corners = {3.0, 3.0, 2.0, 5.0, 2.0, 7.0, 3.0, 5.0};
        Piece piece1 = new Piece(0, piece1Corners);
        Piece piece2 = new Piece(1, piece2Corners);
        piece1.addAdjacentPiece(piece2);
        piece2.addAdjacentPiece(piece1);
        pieceList.add(piece1);
        pieceList.add(piece2);
        Controller.getInstance().setBoardPieces(pieceList);
        piece1.computeNearbyPieces();
        piece1.checkForConnect();
        return pieceList;
    }

}
