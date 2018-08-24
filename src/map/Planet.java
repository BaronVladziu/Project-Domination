package map;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Planet {

    private final static int _MIN_SIZE = 3;
    private final static int _MAX_SIZE = 6;
    private final static int _FIGHT_SCALING_FACTOR = 20;
    private final static int _PLANET_SIZE_FACTOR = 8;
    private final static int _PLANET_SIZE_CONSTANT = 20;
    private final static int _MAX_PLANET_SIZE = _MAX_SIZE*_PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;

    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;

    private int _size;
    private Ellipse2D.Float _shape = new Ellipse2D.Float(0,0,
            this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT,
            this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT);
    private int _owner = -1;
    private Tunnel[] _tunnels;
    private Vector<ArrayList<Ship>> _shipsByPlayer;

    Planet(int numberOfPlayers, int mapWidth, int mapHeight) {
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
        this._size = 0;
        this._shipsByPlayer = new Vector<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            this._shipsByPlayer.add(new ArrayList<>());
        }
    }
    void setTunnels(Tunnel[] tunnels) {
        this._tunnels = tunnels;
    }

    final int getSize() { return this._size; }
    private void setSize(int size) {
        this._size = size;
        this._shape.height = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.width = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
    }
    final float getPlanetSize() { return this._shape.height; }
    final int getOwner() { return this._owner; }
    void setOwner(int owner) { this._owner = owner; }
    final float getX() { return this._shape.x; }
    final float getY() { return this._shape.y; }
    final float getRadius() { return this._shape.height / 2; }
    final Tunnel getTunnel(int tunnel) { return this._tunnels[tunnel]; }
    final int getNumberOfTunnels() { return this._tunnels.length; }
    final int getNumberOfShips() {
        int number_of_ships = 0;
        for (ArrayList<Ship> ships : this._shipsByPlayer) {
            number_of_ships += ships.size();
        }
        return number_of_ships;
    }

    boolean move(float x, float y) {
        if (_shape.x + x > _MAX_PLANET_SIZE && _shape.x + x < this._MAP_WIDTH - _MAX_PLANET_SIZE &&
                _shape.y + y > _MAX_PLANET_SIZE && _shape.y + y < this._MAP_HEIGHT - _MAX_PLANET_SIZE) {
            _shape.x += x;
            _shape.y += y;
            return true;
        }
        return false;
    }

    void randomize(Random generator) {
        this._size = generator.nextInt(_MAX_SIZE - _MIN_SIZE) + _MIN_SIZE;
        this._shape.height = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.width = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.x = generator.nextFloat() * (this._MAP_WIDTH - 2*_MAX_PLANET_SIZE) + _MAX_PLANET_SIZE;
        this._shape.y = generator.nextFloat() * (this._MAP_HEIGHT - 2*_MAX_PLANET_SIZE) + _MAX_PLANET_SIZE;
    }

    void update(Vector<Ship> ships) {
        //Execute fights
        int sum = 0;
        int numberOfFightingPlayers = 0;
        int lastFightingPlayer = -1;
        for (int i = 0; i < this._shipsByPlayer.size(); i++) {
            if (this._shipsByPlayer.get(i).size() > 0) {
                sum += this._shipsByPlayer.get(i).size();
                numberOfFightingPlayers++;
                lastFightingPlayer = i;
            }
        }
        for (int i = 0; i < this._shipsByPlayer.size(); i++) {
            int act = this._shipsByPlayer.get(i).size();
            int killed = (sum - act) / _FIGHT_SCALING_FACTOR + 1;
            if (act - killed >= 0) {
                for (int k = 0; k < killed; k++) {
                    Ship shipToKill = this._shipsByPlayer.get(i).get(0); //remove first ship
                    this._shipsByPlayer.get(i).remove(0);
                    ships.remove(shipToKill);
                }
            } else {
                this._shipsByPlayer.get(i).clear();
            }
        }
        //Update owner
        if (numberOfFightingPlayers == 1) {
            this._owner = lastFightingPlayer;
        }
        //Spawn new ships
        if (this._owner >= 0) {
            for (int s = 0; s < this._size; s++) {
                ships.add(new Ship(this._owner, this));
                this._shipsByPlayer.get(this._owner).add(ships.get(ships.size() - 1));
            }
        }
    }

    void addShip(Ship ship) {
        this._shipsByPlayer.get(ship.getOwner()).add(ship);
    }

    public void moveShip(Ship ship, Planet destination) {
        this._shipsByPlayer.get(ship.getOwner()).remove(ship);
    }

    void draw(Graphics2D g) {
        g.drawOval((int)(this._shape.x - this._shape.width/2), (int)(this._shape.y - this._shape.height/2), (int)this._shape.width, (int)this._shape.height);
    }

}
