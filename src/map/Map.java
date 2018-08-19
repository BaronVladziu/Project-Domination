package map;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Map {

    private final static int _NUMBER_OF_VERTICES = 30;
    private final static int _NUMBER_OF_PLAYERS = 3;
    private final static float _UNIT_TUNNEL_LENGTH = 10.f;
    private final static Random _GENERATOR = new Random();
    private final static int _MAX_DENSITY_REDUCTION_ITERATIONS = 100000;
    private final static float _GRAVITY_STRENGTH = 0.01f;

    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;

    private Tunnel[][] _edges = new Tunnel[_NUMBER_OF_VERTICES][_NUMBER_OF_VERTICES];
    private Planet[] _vertices = new Planet[_NUMBER_OF_VERTICES];

    public Map (int mapWidth, int mapHeight) {
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = 0; j < this._edges[i].length; j++) {
                if (j > i) {
                    this._edges[i][j] = new Tunnel(1);
                } else if (i > j){
                    this._edges[i][j] = this._edges[j][i];
                }
            }
        }
        for (int i = 0; i < this._vertices.length; i++) {
            this._vertices[i] = new Planet(_NUMBER_OF_PLAYERS, _MAP_WIDTH, _MAP_HEIGHT);
        }
    }

    final Tunnel getEdge(int from, int to) { return this._edges[from][to]; }
    final Planet getVertex(int id) { return this._vertices[id]; }

    public void randomize() {
        //Null edges
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = 0; j < this._edges[i].length; j++) {
                this._edges[i][j] = null;
            }
        }
        //Create vertices
        for (Planet vertex : this._vertices) {
            vertex.randomize(_GENERATOR);
        }
        //Reduce density
        float[][] distances = calculateVertexDistances();
        int it = 0;
        boolean ifAnyCollision = true;
        while (ifAnyCollision && it < _MAX_DENSITY_REDUCTION_ITERATIONS) {
            ifAnyCollision = false;
            for (int i = 0; i < _NUMBER_OF_VERTICES - 1; i++) {
                for (int j = i + 1; j < _NUMBER_OF_VERTICES; j++) {
                    if (this.ifCollide(i, j, distances)) {
                        float rx = this._vertices[i].getX() - this._vertices[j].getX();
                        float ry = this._vertices[i].getY() - this._vertices[j].getY();
                        float force = _GRAVITY_STRENGTH * this._vertices[i].getPlanetSize();
                        float x = rx * force;
                        float y = ry * force;
                        if (!this._vertices[i].move(x, y) && !this._vertices[j].move(-x, -y)) {
                            this._vertices[i].randomize(_GENERATOR);
                            this._vertices[j].randomize(_GENERATOR);
                            System.out.println("Planet changed");
                        }
                        ifAnyCollision = true;
                    }
                }
            }
            distances = calculateVertexDistances();
            it++;
        }
        if (it == _MAX_DENSITY_REDUCTION_ITERATIONS) {
            System.out.println("Error: Could not prevent planet collisions!");
        }
        //Create edges
        ArrayList<Edge> edgesForSorting = new ArrayList<>();
        for (int i = 0; i < _NUMBER_OF_VERTICES - 1; i++) {
            for (int j = i+1; j < _NUMBER_OF_VERTICES; j++) {
                edgesForSorting.add(new Edge(i, j, distances[i][j]));
            }
        }
        Collections.sort(edgesForSorting);

        int[] subgraphIDs = new int[_NUMBER_OF_VERTICES];
        for (int i = 0; i < _NUMBER_OF_VERTICES; i++) {
            subgraphIDs[i] = i;
        }
        int numberOfZeros = 1;

        for (Edge e : edgesForSorting) { //Minimum spanning tree-like
            this._edges[e._vertex1][e._vertex2] = new Tunnel((int)(e._length / _UNIT_TUNNEL_LENGTH));
            this._edges[e._vertex2][e._vertex1] = new Tunnel((int)(e._length / _UNIT_TUNNEL_LENGTH));
            int lowerSubgraphID, higherSubgraphID;
            if (subgraphIDs[e._vertex1] <= subgraphIDs[e._vertex2]) {
                lowerSubgraphID = subgraphIDs[e._vertex1];
                higherSubgraphID = subgraphIDs[e._vertex2];
            } else {
                lowerSubgraphID = subgraphIDs[e._vertex2];
                higherSubgraphID = subgraphIDs[e._vertex1];
            }
            for (int i = 0; i < _NUMBER_OF_VERTICES; i++) {
                if (subgraphIDs[i] == higherSubgraphID) {
                    subgraphIDs[i] = lowerSubgraphID;
                    if (higherSubgraphID != 0 && lowerSubgraphID == 0) {
                        numberOfZeros++;
                    }
                }
            }
            if (numberOfZeros >= _NUMBER_OF_VERTICES) {
                break;
            }
        }
    }

    private boolean ifAnyVertexCollisions(float[][] distances) {
        for (int i = 0; i < _NUMBER_OF_VERTICES; i++) {
            for (int j = 0; j < _NUMBER_OF_VERTICES; j++) {
                if (i != j) {
                    if (this.ifCollide(i, j, distances)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean ifCollide(int a, int b, float[][] distances) {
        if (distances[a][b] < this._vertices[a].getPlanetSize() + this._vertices[b].getPlanetSize()) {
            return true;
        }
        return false;
    }

    private float[][] calculateVertexDistances() {
        float[][] distances = new float[_NUMBER_OF_VERTICES][_NUMBER_OF_VERTICES];
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = i; j < this._edges[i].length; j++) {
                float dx = this._vertices[i].getX() - this._vertices[j].getX();
                float dy = this._vertices[i].getY() - this._vertices[j].getY();
                float d = (float)Math.sqrt(dx*dx + dy*dy);
                distances[i][j] = d;
                distances[j][i] = d;
            }
        }
        return distances;
    }

    private Vector2f calculateGravity(int vertexID, float[][] distances) {
        float x = 0;
        float y = 0;
        for (int i = 0; i < _NUMBER_OF_VERTICES; i++) {
            if (i != vertexID) {
                float rx = this._vertices[i].getX() - this._vertices[vertexID].getX();
                float ry = this._vertices[i].getY() - this._vertices[vertexID].getY();
                float force = _GRAVITY_STRENGTH * this._vertices[i].getPlanetSize() / (distances[i][vertexID]*distances[i][vertexID]*distances[i][vertexID]);
                x += rx * force;
                y += ry * force;
            }
        }
        return new Vector2f(x, y);
    }

    private int[] findNIdsOfMinDistances(float[] v, final int n) {
        if (n > 0) {
            int[] ids = new int[n];
            for (int i = 0; i < n; i++) {
                ids[i] = -1;
            }
            for (int i = 0; i < v.length; i++) {
                if (v[i] > 0 && (ids[0] < 0 || v[i] < v[ids[0]])) {
                    ids[0] = i;
                    int act = 0;
                    while (act < n - 1 && (ids[act+1] < 0 || v[ids[act]] < v[ids[act+1]])) {
                        int temp = ids[act];
                        ids[act] = ids[act+1];
                        ids[act+1] = temp;
                        act++;
                    }
                }
            }
            return ids;
        }
        return null;
    }

    private void connectWholeGraph(float[][] distances) {
        int[] subgraphIDs = new int[_NUMBER_OF_VERTICES];
        for (int i = 0; i < _NUMBER_OF_VERTICES; i++) {
            subgraphIDs[i] = -1;
        }
        int actSubgraphID = 0;
        int visitedVerticesCounter = 0;
        while (visitedVerticesCounter < _NUMBER_OF_VERTICES) {
            visitedVerticesCounter = depthFirstSearch(subgraphIDs, actSubgraphID, actSubgraphID, visitedVerticesCounter);
            actSubgraphID++;
        }

        System.out.println("SubgraphIDs:");
        for (int i = 0; i < _NUMBER_OF_VERTICES; i++) {
            System.out.print(subgraphIDs[i]);
            System.out.print('\t');
        }
        System.out.print('\n');


    }

    private int depthFirstSearch(int[] subgraphIDs, int verticeID, int actSubgraphID, int visitedVerticesCounter) {
        if (subgraphIDs[verticeID] == -1) {
            subgraphIDs[verticeID] = actSubgraphID;
            visitedVerticesCounter++;
            for (int e = 0; e < _NUMBER_OF_VERTICES; e++) {
                if (this._edges[verticeID][e] != null) {
                    visitedVerticesCounter = depthFirstSearch(subgraphIDs, e, actSubgraphID, visitedVerticesCounter);
                }
            }
        }
        return visitedVerticesCounter;
    }

    public void update() {
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = i; j < this._edges[i].length; j++) {
                if (this._edges[i][j] != null) {
                    this._edges[i][j].update();
                }
            }
        }
        for (Planet vertex : this._vertices) {
            vertex.update();
        }
    }

    public void print() {
        System.out.println("--- Map ---");
        System.out.println("Vertices:");
        for (Planet vertex : this._vertices) {
            System.out.print(vertex.getSize());
            System.out.print('\t');
        }
        System.out.print('\n');
        System.out.println("Edges:");
        for (Tunnel[] edges : this._edges) {
            for (Tunnel edge : edges) {
                if (edge == null) {
                    System.out.print('-');
                    System.out.print('\t');
                } else {
                    System.out.print(edge.getLength());
                    System.out.print('\t');
                }
            }
            System.out.print('\n');
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = i; j < this._edges[i].length; j++) {
                if (this._edges[i][j] != null) {
                    g.drawLine((int)this._vertices[i].getX(), (int)this._vertices[i].getY(), (int)this._vertices[j].getX(), (int)this._vertices[j].getY());
                }
            }
        }
        for (Planet vertex : this._vertices) {
            g.setColor(Color.WHITE); //TODO
            vertex.draw(g);
        }
    }

}
