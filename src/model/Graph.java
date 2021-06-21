package model;

import java.util.*;

// This class creates a undirected graph using an adjacency list and it is downloaded from the internet and
// modified to work with our code.
// https://www.geeksforgeeks.org/implementing-generic-graph-in-java/

public class Graph {
    private Map<Vertex, List<Vertex>> adjVertices;
    public Graph() {
        this.adjVertices = new HashMap<Vertex, List<Vertex>>();
    }

    // Getter method for the adjacent vertixes
    public Map<Vertex, List<Vertex>> getAdjVertices(){return adjVertices;}

    class Vertex {
        Piece piece;
        public Vertex(Piece p) {
            this.piece = p;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((piece == null) ? 0 : piece.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vertex other = (Vertex) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (piece == null) {
                if (other.piece != null)
                    return false;
            } else if (!piece.equals(other.piece))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return Integer.toString(piece.getPieceID());
        }

        private Graph getOuterType() {
            return Graph.this;
        }

    }

    // Getter method for the adjacent vertices from a specific piece
    List<Vertex> getAdjVertices(Piece p) {
        return adjVertices.get(new Vertex(p));
    }

    // Method to add a new vertex representing a piece to the graph.
    void addVertex(Piece p) {
        adjVertices.putIfAbsent(new Vertex(p), new ArrayList<>());
    }

    // Method to add an edge between two pieces
    void addEdge(Piece p1, Piece p2) {
        Vertex v1 = new Vertex(p1);
        Vertex v2 = new Vertex(p2);
        if(!adjVertices.get(v1).contains(v2)) {
            adjVertices.get(v1).add(v2);
        }
        if(!adjVertices.get(v2).contains(v1)) {
            adjVertices.get(v2).add(v1);
        }
    }

    // Method to remove an edge between to pieces
    void removeEdge(Piece p1, Piece p2) {
        Vertex v1 = new Vertex(p1);
        Vertex v2 = new Vertex(p2);
        List<Vertex> eV1 = adjVertices.get(v1);
        List<Vertex> eV2 = adjVertices.get(v2);
        if(eV1 != null) {
            eV1.remove(v2);
        }
        if(eV2 != null) {
            eV2.remove(v2);
        }
    }

    // Depth first traversal from a piece. It takes a Piece as root and returns a set of all the Pieces connected
    // Input - a root piece
    // Output - A set containing all of the pieces that is connected to the root
    Set<Piece> depthFirstTraversal(Piece root) {
        Set<Piece> visited = new LinkedHashSet<Piece>();
        Stack<Piece> stack = new Stack<Piece>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Piece vertex = stack.pop();
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                for (Vertex v : this.getAdjVertices(vertex)) {
                    stack.push(v.piece);
                }
            }
        }
        return visited;
    }
}

