package map;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import static java.lang.Math.PI;

public class Explosion {

    private final static float _HEIGHT = 5.f;
    private final static float _CENTER_RADIUS = 3.f;
    private final static int _NUMBER_OF_PHASES = 3;
    private final static Color _COLOR = Color.WHITE;

    private final Planet _location;

    Explosion(Planet location) {
        this._location = location;
    }

    void draw(Graphics2D g, Random generator) {
        float radius = _HEIGHT + (this._location.getRadius());

        double angle = generator.nextDouble() * 2*PI;
        float x = (float)Math.cos(angle);
        float y = (float)Math.sin(angle);
        x *= radius;
        y *= radius;
        x += this._location.getX();
        y += this._location.getY();

        for (int f = 0; f < _NUMBER_OF_PHASES; f++) {
            g.setColor(new Color(_COLOR.getRed(), _COLOR.getGreen(), _COLOR.getBlue(), 255 / (f+1)));
            g.fill(new Ellipse2D.Float((int)x - _CENTER_RADIUS*(f+1), (int)y - _CENTER_RADIUS*(f+1), 2*_CENTER_RADIUS*(f+1), 2*_CENTER_RADIUS*(f+1)));
        }
    }

}
