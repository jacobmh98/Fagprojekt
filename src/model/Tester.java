package model;

public class Tester {
    public static void main(String[] args) {
        Graph graph = new Graph();
        Piece p0 = new Piece(0, new Double[]{});
        Piece p1 = new Piece(1, new Double[]{});
        Piece p2 = new Piece(2, new Double[]{});
        Piece p3 = new Piece(3, new Double[]{});

        graph.addVertex(p0);
        graph.addVertex(p1);
        graph.addVertex(p2);
        graph.addVertex(p3);

        graph.addEdge(p0, p1);
        graph.addEdge(p0, p1);

        System.out.println("Traversed: ");
        for(Piece p : graph.depthFirstTraversal(p2)) {
            System.out.print(p.getPieceID()+", ");
        }
    }
}
