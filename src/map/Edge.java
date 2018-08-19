package map;

public class Edge implements Comparable<Edge> {

    int _vertex1;
    int _vertex2;
    float _length;

    Edge(int vertex1, int vertex2, float distance) {
        this._vertex1 = vertex1;
        this._vertex2 = vertex2;
        this._length = distance;
    }

    @Override
    public int compareTo(Edge e) {
        return (int)(this._length - e._length);
    }

}
