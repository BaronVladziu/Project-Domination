package map;

public class Army implements Comparable<Army> {

    final int _owner;

    final int _numberOfShips;

    Army(int owner, int numberOfShips) {
        this._owner = owner;
        this._numberOfShips = numberOfShips;
    }

    @Override
    public int compareTo(Army army) {
        return army._numberOfShips - this._numberOfShips;
    }

}
