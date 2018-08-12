package map;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class Planet {

    private final static int _MIN_SIZE = 1;
    private final static int _MAX_SIZE = 10;
    private final static int _FIGHT_SCALING_FACTOR = 5;
    private final static int _PLANET_SIZE_FACTOR = 5;
    private final static int _PLANET_SIZE_CONSTANT = 30;
    private final static int _MAX_PLANET_SIZE = _MAX_SIZE*_PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;

    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;

    private int _size;
    private Ellipse2D.Float _shape = new Ellipse2D.Float(0,0,_size,_size);
    private int _owner = -1;
    private int[] _shipsByPlayer;

    Planet(int numberOfPlayers, int mapWidth, int mapHeight) {
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
        this._size = 0;
        this._shipsByPlayer = new int[numberOfPlayers];
    }

    final int getSize() { return this._size; }
    void setSize(int size) {
        this._size = size;
        this._shape.height = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.width = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
    }
    final float getPlanetSize() { return this._shape.height; }
    final int getOwner() { return this._owner; }
    void setOwner(int owner) { this._owner = owner; }
    final int[] getShipsByPlayer() { return this._shipsByPlayer; }
    void setShips(int playerId, int ships) { this._shipsByPlayer[playerId] = ships; }
    final float getX() { return this._shape.x; }
    final float getY() { return this._shape.y; }
    void move(float x, float y) {
        if (_shape.x + x > _MAX_PLANET_SIZE && _shape.x + x < this._MAP_WIDTH - _MAX_PLANET_SIZE &&
                _shape.y + y > _MAX_PLANET_SIZE && _shape.y + y < this._MAP_HEIGHT - _MAX_PLANET_SIZE) {
            _shape.x += x;
            _shape.y += y;
        }
    }

    void randomize(Random generator) {
        this._size = generator.nextInt(_MAX_SIZE - _MIN_SIZE) + _MIN_SIZE;
        this._shape.height = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.width = this._size * _PLANET_SIZE_FACTOR + _PLANET_SIZE_CONSTANT;
        this._shape.x = generator.nextFloat() * (this._MAP_WIDTH - 2*_MAX_PLANET_SIZE) + _MAX_PLANET_SIZE;
        this._shape.y = generator.nextFloat() * (this._MAP_HEIGHT - 2*_MAX_PLANET_SIZE) + _MAX_PLANET_SIZE;
        for (int i = 0; i < this._shipsByPlayer.length; i++) {
            this._shipsByPlayer[i] = 0;
        }
    }

    void executeFight() {
        int sum = 0;
        for (int v : this._shipsByPlayer) {
            sum += v;
        }
        for (int i = 0; i < this._shipsByPlayer.length; i++) {
            int act = this._shipsByPlayer[i];
            int killed = (sum - act) / _FIGHT_SCALING_FACTOR + 1;
            if (act - killed >= 0) {
                this._shipsByPlayer[i] = act - killed;
            } else {
                this._shipsByPlayer[i] = 0;
            }
        }
    }

    void draw(Graphics g) {
        g.drawOval((int)(this._shape.x - this._shape.width/2), (int)(this._shape.y - this._shape.height/2), (int)this._shape.width, (int)this._shape.height);
    }

}
