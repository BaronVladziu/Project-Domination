package map;

import drawing.PlayerColors;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class Planet {

    private final static int _MIN_SIZE = 3;
    private final static int _MAX_SIZE = 6;
    private final static int _FIGHT_SCALING_FACTOR = 20; //The lower the faster are fights
    private final static int _PLANET_SIZE_FACTOR = 5;
    private final static int _PLANET_SIZE_CONSTANT = 20;

    final static int _MAX_PLANET_SIZE = _MAX_SIZE*_PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;

    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;
    private final Explosion _explosion = new Explosion(this);
    private final int _ID;

    private int _size = 0;
    private Ellipse2D.Float _shape = new Ellipse2D.Float(0,0,
            this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT,
            this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT);
    private int _owner = -1;
    private Tunnel[] _tunnels;
    private Vector<ArrayList<Ship>> _shipsByPlayer;
    private int _killedInLastFight = 0;

    Planet(int numberOfPlayers, int mapWidth, int mapHeight, int planetID) {
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
        this._ID = planetID;
        this._shipsByPlayer = new Vector<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            this._shipsByPlayer.add(new ArrayList<>());
        }
    }
    void setTunnels(Tunnel[] tunnels) {
        this._tunnels = tunnels;
    }

    public final int getSize() { return this._size; }
//    private void setSize(int size) {
//        this._size = size;
//        this._shape.height = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
//        this._shape.width = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
//    }
    final float getPlanetSize() { return this._shape.height; }
    public final int getOwner() { return this._owner; }
    void setOwner(int owner) { this._owner = owner; }
    public final int getID() { return this._ID; }
    final float getX() { return this._shape.x + this._shape.width / 2; }
    final float getY() { return this._shape.y + this._shape.height / 2; }
    final float getRadius() { return this._shape.height / 2; }
    public final Tunnel getTunnel(int tunnel) { return this._tunnels[tunnel]; }
    public final int getNumberOfTunnels() { return this._tunnels.length; }
    public final int getNumberOfShips() {
        int number_of_ships = 0;
        for (ArrayList<Ship> ships : this._shipsByPlayer) {
            number_of_ships += ships.size();
        }
        return number_of_ships;
    }
    public final int getNumberOfShips(int playerID) {
        return this._shipsByPlayer.get(playerID).size();
    }
    void setPosition(float x, float y) {
        _shape.x = x;
        _shape.y = y;
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
        if (_MAX_SIZE > _MIN_SIZE) {
            this._size = generator.nextInt(_MAX_SIZE - _MIN_SIZE) + _MIN_SIZE;
        } else {
            this._size = _MAX_SIZE;
        }
        this._shape.height = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.width = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.x = generator.nextFloat() * (this._MAP_WIDTH - 2*_MAX_PLANET_SIZE) + _MAX_PLANET_SIZE - this._shape.width/2;
        this._shape.y = generator.nextFloat() * (this._MAP_HEIGHT - 2*_MAX_PLANET_SIZE) + _MAX_PLANET_SIZE - this._shape.height/2;
    }

    boolean updateAndCheckIfOwnerChanged(Vector<Ship> ships) {
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
            int killed = 0;
            if (numberOfFightingPlayers >= 2) {
                killed = (sum - act) / _FIGHT_SCALING_FACTOR + 1;
            }
            this._killedInLastFight = killed;
            if (act - killed >= 0) {
                for (int k = 0; k < killed; k++) {
                    Ship shipToKill = this._shipsByPlayer.get(i).get(0); //remove first ship
                    this._shipsByPlayer.get(i).remove(0);
                    ships.remove(shipToKill);
                }
            } else {
                for (Ship ship : this._shipsByPlayer.get(i)) {
                    ships.remove(ship);
                }
                this._shipsByPlayer.get(i).clear();
            }
        }
        //Update owner
        boolean ifOwnerChanged = false;
        if (numberOfFightingPlayers == 1) {
            if (this._owner != lastFightingPlayer) {
                ifOwnerChanged = true;
            }
            this._owner = lastFightingPlayer;
        }
        //Spawn new ship
        if (this._owner >= 0) {
            ships.add(new Ship(this._owner, this));
            this._shipsByPlayer.get(this._owner).add(ships.get(ships.size() - 1));
        }
        return ifOwnerChanged;
    }

    void addShip(Ship ship) {
        this._shipsByPlayer.get(ship.getOwner()).add(ship);
    }

    public void moveShip(Ship ship, Planet destination) {
        this._shipsByPlayer.get(ship.getOwner()).remove(ship);
    }

    void draw(Graphics2D g, PlayerColors playerColors, Random generator) {
        //Draw planet
        if (this._owner >= 0) {
            //Count armies
            Vector<Army> armies = new Vector<>();
            int numberOfShips = 0;
            int numberOfOwnerShips = 0;
            for (int p = 0; p < this._shipsByPlayer.size(); p++) {
                if (this._shipsByPlayer.get(p).size() > 0) {
                    if (p == this._owner) {
                        numberOfOwnerShips = this._shipsByPlayer.get(p).size();
                    } else {
                        armies.add(new Army(p, this._shipsByPlayer.get(p).size()));
                    }
                    numberOfShips += this._shipsByPlayer.get(p).size();
                }
            }
            //Sort armies
            Collections.sort(armies);
            armies.add(new Army(this._owner, numberOfOwnerShips));
            //Draw armies
            float actRadius = this._shape.width / 2;
            for (Army army : armies) {
                Color normal = playerColors.getColor(army._owner);
                Color transparent = new Color(normal.getRed(), normal.getGreen(), normal.getBlue(), 100);
                g.setColor(transparent);
                g.fill(new Ellipse2D.Float(this.getX() - actRadius, this.getY() - actRadius, 2*actRadius, 2*actRadius));
                actRadius -= this._shape.width / 2 * army._numberOfShips / numberOfShips;
            }
        }
        //Draw planet outline
        g.setColor(playerColors.getColor(this._owner));
        g.draw(_shape);
        //Draw explosions
        for (int i = 0; i < this._killedInLastFight; i++) {
            _explosion.draw(g, generator);
        }
    }

}
