package map;

import AI.AI;
import drawing.PlayerColors;

import java.awt.*;
import java.util.*;

import static java.lang.Math.PI;

public class Map {

    private final static int _MAX_NUMBER_OF_VERTICES_PER_PLAYER = 100;
    private final static int _NUMBER_OF_PLAYERS = 6;
    private final static int _MAX_NUMBER_OF_VERTICES = _MAX_NUMBER_OF_VERTICES_PER_PLAYER * _NUMBER_OF_PLAYERS;
    private final static float _UNIT_TUNNEL_LENGTH = 4.f;
    private final static Random _GENERATOR = new Random();
    private final static int _MAX_SEARCH_ITERATIONS = 1000;
    private final static float _GRAVITY_STRENGTH = 0.01f;
    private final static int _MAX_NUMBER_OF_TURNS = 700;

    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;

    private Tunnel[][] _edges = new Tunnel[_MAX_NUMBER_OF_VERTICES][_MAX_NUMBER_OF_VERTICES];
    private Planet[] _vertices = new Planet[_MAX_NUMBER_OF_VERTICES];
    private Vector<Ship> _ships = new Vector<>();
    private AI _ai = new AI(_NUMBER_OF_PLAYERS);
    private int _numberOfPassedTurns;

    public Map (int mapWidth, int mapHeight) {
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
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

        //Null planets
        for (int i = 0; i < this._vertices.length; i++) {
            this._vertices[i] = null;
        }

        //Reset rest of objects
        this._ships.clear();
        this._numberOfPassedTurns = 0;

        //Calculate vertex position ranges
        double alpha = 2*PI/_NUMBER_OF_PLAYERS;
        double maxPlanetPositionRadius = Math.min((double)_MAP_WIDTH/2, (double)_MAP_HEIGHT/2) - Planet._MAX_PLANET_SIZE;
        double minPlanetPositionRadius;
        if (_NUMBER_OF_PLAYERS >= 3) {
            minPlanetPositionRadius = (Math.sqrt((double)(2*Planet._MAX_PLANET_SIZE*Planet._MAX_PLANET_SIZE)/(1 - Math.cos(alpha))));
        } else {
            minPlanetPositionRadius = Planet._MAX_PLANET_SIZE;
        }

        //Generate vertices
        int numberOfGeneratedVertices = 0;
        for (int v = 0; v < _MAX_NUMBER_OF_VERTICES_PER_PLAYER; v++) {
            //Generate vertex for first player
            this._vertices[_NUMBER_OF_PLAYERS*v] = new Planet(_NUMBER_OF_PLAYERS, _MAP_WIDTH, _MAP_HEIGHT);
            double distanceFromMapCenter;
            int numberOfSearchIterations = 0;
            do {
                this._vertices[_NUMBER_OF_PLAYERS*v].randomize(_GENERATOR);
                double rx = this._vertices[_NUMBER_OF_PLAYERS*v].getX() - (double)_MAP_WIDTH/2;
                double ry = this._vertices[_NUMBER_OF_PLAYERS*v].getY() - (double)_MAP_HEIGHT/2;
                distanceFromMapCenter = Math.sqrt(rx*rx + ry*ry);
                numberOfSearchIterations++;
                if (numberOfSearchIterations >= _MAX_SEARCH_ITERATIONS) {
                    this._vertices[_NUMBER_OF_PLAYERS*v] = null;
                    break;
                }
            } while (distanceFromMapCenter < minPlanetPositionRadius || distanceFromMapCenter > maxPlanetPositionRadius || ifVertexCanCollide(_NUMBER_OF_PLAYERS*v));
            numberOfGeneratedVertices = v*_NUMBER_OF_PLAYERS;
            if (numberOfSearchIterations >= _MAX_SEARCH_ITERATIONS) {
                break;
            }

            //Generate vertices for remaining players
            double betaX = Math.acos(((double)_MAP_WIDTH/2 - this._vertices[_NUMBER_OF_PLAYERS*v].getX()) / distanceFromMapCenter);
            double betaY = Math.asin(((double)_MAP_HEIGHT/2 - this._vertices[_NUMBER_OF_PLAYERS*v].getY()) / distanceFromMapCenter);

            double beta;
            if (betaX >= PI/2) {
                beta = (PI - betaY);
            } else {
                beta = betaY;
            }

            for (int j = 1; j < _NUMBER_OF_PLAYERS; j++) {
                double gamma = beta + j*alpha;
                this._vertices[_NUMBER_OF_PLAYERS*v + j] = new Planet(_NUMBER_OF_PLAYERS, _MAP_WIDTH, _MAP_HEIGHT);
                this._vertices[_NUMBER_OF_PLAYERS*v + j].randomize(_GENERATOR);
                this._vertices[_NUMBER_OF_PLAYERS*v + j].setPosition((float)((double)_MAP_WIDTH/2 - (double)distanceFromMapCenter*Math.cos(gamma)),
                        (float)((double)_MAP_HEIGHT/2 - (double)distanceFromMapCenter*Math.sin(gamma)));
            }
        }

        //Set motherworlds
        for (int j = 0; j < _NUMBER_OF_PLAYERS; j++) {
            this._vertices[j].setOwner(j);
        }
//        System.out.println("Generated " + Integer.toString(numberOfGeneratedVertices) + " planets.");

        //Calculate distances
        float[][] distances = calculateVertexDistances();

        //Create edges
        PriorityQueue<Edge> edgesForSorting = new PriorityQueue<>();
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES - 1; i++) {
            for (int j = i+1; j < _MAX_NUMBER_OF_VERTICES; j++) {
                if (this._vertices[i] != null && this._vertices[j] != null) {
                    edgesForSorting.add(new Edge(i, j, distances[i][j]));
                }
            }
        }

        int[] subgraphIDs = new int[_MAX_NUMBER_OF_VERTICES];
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
            subgraphIDs[i] = i;
        }
        int numberOfZeros = 1;

        while (numberOfZeros < numberOfGeneratedVertices) { //Minimum spanning tree-like
            if (this._edges[edgesForSorting.peek()._vertex1][edgesForSorting.peek()._vertex2] == null) {
                for (int p = 0; p < _NUMBER_OF_PLAYERS; p++) {
                    int v1 = edgesForSorting.peek()._vertex1;
                    if (v1 % _NUMBER_OF_PLAYERS + p >= _NUMBER_OF_PLAYERS) {
                        v1 -= _NUMBER_OF_PLAYERS;
                    }
                    int v2 = edgesForSorting.peek()._vertex2;
                    if (v2 % _NUMBER_OF_PLAYERS + p >= _NUMBER_OF_PLAYERS) {
                        v2 -= _NUMBER_OF_PLAYERS;
                    }
                    this._edges[v1 + p][v2 + p] = new Tunnel((int)(edgesForSorting.peek()._length / _UNIT_TUNNEL_LENGTH),
                            this._vertices[v1 + p], this._vertices[v2 + p]);
                    this._edges[v2 + p][v1 + p] = new Tunnel((int)(edgesForSorting.peek()._length / _UNIT_TUNNEL_LENGTH),
                            this._vertices[v2 + p], this._vertices[v1 + p]);
                    int lowerSubgraphID, higherSubgraphID;
                    if (subgraphIDs[v1 + p] <= subgraphIDs[v2 + p]) {
                        lowerSubgraphID = subgraphIDs[v1 + p];
                        higherSubgraphID = subgraphIDs[v2 + p];
                    } else {
                        lowerSubgraphID = subgraphIDs[v2 + p];
                        higherSubgraphID = subgraphIDs[v1 + p];
                    }
                    for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
                        if (subgraphIDs[i] == higherSubgraphID) {
                            subgraphIDs[i] = lowerSubgraphID;
                            if (higherSubgraphID != 0 && lowerSubgraphID == 0) {
                                numberOfZeros++;
                            }
                        }
                    }
                }
            }
            edgesForSorting.poll();
        }

        //Connect planets
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
            if (this._vertices[i] != null) {
                //Count tunnels
                int number_of_tunnels = 0;
                for (int j = 0; j < this._edges[i].length; j++) {
                    if (this._edges[i][j] != null) {
                        number_of_tunnels++;
                    }
                }
                //Create array of tunnels
                Tunnel[] tunnels_from_act_planet = new Tunnel[number_of_tunnels];
                int act_tunnel = 0;
                for (int j = 0; j < this._edges[i].length; j++) {
                    if (this._edges[i][j] != null) {
                        tunnels_from_act_planet[act_tunnel] = this._edges[i][j];
                        act_tunnel++;
                    }
                }
                this._vertices[i].setTunnels(tunnels_from_act_planet);
            }
        }
    }

    private boolean ifVertexCanCollide(int v) {
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
            if (i != v && this._vertices[i] != null) {
                float dx = this._vertices[i].getX() - this._vertices[v].getX();
                float dy = this._vertices[i].getY() - this._vertices[v].getY();
                if (Math.sqrt(dx*dx + dy*dy) < Planet._MAX_PLANET_SIZE + Planet._MAX_PLANET_SIZE) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ifAnyVertexCollisions(float[][] distances) {
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
            for (int j = 0; j < _MAX_NUMBER_OF_VERTICES; j++) {
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
        return distances[a][b] < this._vertices[a].getPlanetSize() + this._vertices[b].getPlanetSize();
    }

    private float[][] calculateVertexDistances() {
        float[][] distances = new float[_MAX_NUMBER_OF_VERTICES][_MAX_NUMBER_OF_VERTICES];
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = i; j < this._edges[i].length; j++) {
                if (this._vertices[i] != null && this._vertices[j] != null) {
                    float dx = this._vertices[i].getX() - this._vertices[j].getX();
                    float dy = this._vertices[i].getY() - this._vertices[j].getY();
                    float d = (float)Math.sqrt(dx*dx + dy*dy);
                    distances[i][j] = d;
                    distances[j][i] = d;
                }
            }
        }
        return distances;
    }

    private Vector2f calculateGravity(int vertexID, float[][] distances) {
        float x = 0;
        float y = 0;
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
            if (i != vertexID) {
                float rx = this._vertices[i].getX() - this._vertices[vertexID].getX();
                float ry = this._vertices[i].getY() - this._vertices[vertexID].getY();
                float force = _GRAVITY_STRENGTH * this._vertices[i].getPlanetSize() /
                        (distances[i][vertexID]*distances[i][vertexID]*distances[i][vertexID]);
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
        int[] subgraphIDs = new int[_MAX_NUMBER_OF_VERTICES];
        for (int i = 0; i < _MAX_NUMBER_OF_VERTICES; i++) {
            subgraphIDs[i] = -1;
        }
        int actSubgraphID = 0;
        int visitedVerticesCounter = 0;
        while (visitedVerticesCounter < _MAX_NUMBER_OF_VERTICES) {
            visitedVerticesCounter = depthFirstSearch(subgraphIDs, actSubgraphID, actSubgraphID,
                    visitedVerticesCounter);
            actSubgraphID++;
        }
    }

    private int depthFirstSearch(int[] subgraphIDs, int verticeID, int actSubgraphID, int visitedVerticesCounter) {
        if (subgraphIDs[verticeID] == -1) {
            subgraphIDs[verticeID] = actSubgraphID;
            visitedVerticesCounter++;
            for (int e = 0; e < _MAX_NUMBER_OF_VERTICES; e++) {
                if (this._edges[verticeID][e] != null) {
                    visitedVerticesCounter = depthFirstSearch(subgraphIDs, e, actSubgraphID, visitedVerticesCounter);
                }
            }
        }
        return visitedVerticesCounter;
    }

    public void update() {
        if (this._numberOfPassedTurns < _MAX_NUMBER_OF_TURNS) {
            this._numberOfPassedTurns++;
            //Update ships
            for (Ship ship : this._ships) {
                ship.update(_ai);
            }
            //Update planets
            for (Planet vertex : this._vertices) {
                if (vertex != null) {
                    vertex.update(this._ships);
                }
            }
        } else {
            this._ai.improvePlayers(this._vertices);
            this.randomize();
        }
    }

    public void print() {
        System.out.println("--- Map ---");
        System.out.println("Vertices:");
        for (Planet vertex : this._vertices) {
            if (vertex != null) {
                System.out.print(vertex.getSize());
                System.out.print('\t');
            }
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

    public void draw(Graphics2D g, PlayerColors playerColors) {
        //Draw tunnels
        g.setColor(Color.WHITE);
        for (int i = 0; i < this._edges.length; i++) {
            for (int j = i; j < this._edges[i].length; j++) {
                if (this._edges[i][j] != null) {
                    g.drawLine((int)this._vertices[i].getX(), (int)this._vertices[i].getY(),
                            (int)this._vertices[j].getX(), (int)this._vertices[j].getY());
                }
            }
        }
        //Draw ships
        for (int i = 0; i < this._ships.size(); i++) {
            Ship ship = this._ships.get(i);
            ship.draw(g, playerColors.getColor((ship.getOwner())), _GENERATOR);
        }
        //Draw planets
        for (Planet vertex : this._vertices) {
            if (vertex != null) {
                vertex.draw(g, playerColors, _GENERATOR);
            }
        }
    }

}
