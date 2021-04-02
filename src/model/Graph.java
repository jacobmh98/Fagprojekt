package model;

import java.util.*;

public class Graph {
    private Map<Vertex, List<Vertex>> adjVertices;
    public Graph() {
        this.adjVertices = new HashMap<Vertex, List<Vertex>>();
    }

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

    void addVertex(Piece p) {
        adjVertices.putIfAbsent(new Vertex(p), new ArrayList<>());
    }

    void removeVertex(Piece p) {
        Vertex v = new Vertex(p);
        adjVertices.values().stream().forEach(e -> e.remove(v));
        adjVertices.remove(new Vertex(p));
    }

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

    List<Vertex> getAdjVertices(Piece p) {
        return adjVertices.get(new Vertex(p));
    }

    Set<Piece> depthFirstTraversal(Graph graph, Piece root) {
        Set<Piece> visited = new LinkedHashSet<Piece>();
        Stack<Piece> stack = new Stack<Piece>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Piece vertex = stack.pop();
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                for (Vertex v : graph.getAdjVertices(vertex)) {
                    stack.push(v.piece);
                }
            }
        }
        return visited;
    }
}

