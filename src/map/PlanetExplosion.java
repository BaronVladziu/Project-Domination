package map;

import drawing.PlayerColors;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class PlanetExplosion {

    private static final float _RAISE_FACTOR = 1.2f;
    private static final int _LIFE_LENGTH = 10;

    private int _lifeLength = 0;
    private float _x;
    private float _y;
    private float _r;
    private int _owner;

    PlanetExplosion(Planet planet) {
        this._x = planet.getX();
        this._y = planet.getY();
        this._r = planet.getRadius();
        this._owner = planet.getOwner();
    }

    void update() {
        this._lifeLength++;
        this._r *= _RAISE_FACTOR;
    }

    boolean isDead() {
        return (this._lifeLength >= _LIFE_LENGTH);
    }

    void draw(Graphics2D g, PlayerColors playerColors) {
        Color playerColor = playerColors.getColor(this._owner);
        g.setColor(new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(),
                (_LIFE_LENGTH - this._lifeLength) * 255 / _LIFE_LENGTH));
        g.fill(new Ellipse2D.Float(this._x - this._r, this._y - this._r, 2*this._r, 2*this._r));
    }

}
