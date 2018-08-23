package map;

import java.util.ArrayList;

public class Tunnel {

    private int _length;
    private ArrayList<Ship> _ships = new ArrayList<>();
    private final Planet _source;
    private final Planet _destination;

    Tunnel(int length, Planet source, Planet destination) {
        this._length = length;
        this._source = source;
        this._destination = destination;
    }

    public final int getLength() { return this._length; }
    public final Planet getSource() { return this._source; }
    public final Planet getDestination() { return this._destination; }

    public void moveShip(Ship ship) {
        this._ships.remove(ship);
        this._destination.addShip(ship);
    }

}
