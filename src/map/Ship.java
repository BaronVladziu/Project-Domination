package map;

import map.Planet;
import map.Tunnel;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.PI;

public class Ship {

    private final static float _FLIGHT_HEIGHT = 3.f;

    Planet _locationPlanet;
    Tunnel _locationTunnel;
    int _distanceInTunnel;
    int _owner;

    public Ship(int owner, Planet birthPlace) {
        this._locationPlanet = birthPlace;
        this._locationTunnel = null;
        this._distanceInTunnel = 0;
        this._owner = owner;
    }

    final int getDistanceInTunnel() { return this._distanceInTunnel; }
    public final int getOwner() { return this._owner; }

    public void update(Random generator) {
        if (this._locationTunnel != null) {
            this._distanceInTunnel++;
            if (this._distanceInTunnel >= this._locationTunnel.getLength()) {
                this._locationTunnel.moveShip(this);
                this._locationTunnel = null;
            }
        } else {
            int randomTunnel = generator.nextInt(this._locationPlanet.getNumberOfTunnels());
            this._locationTunnel = this._locationPlanet.getTunnel(randomTunnel);
            this._locationPlanet.moveShip(this, this._locationTunnel.getDestination());
            this._distanceInTunnel = 0;
            this._locationPlanet = this._locationTunnel.getDestination();
        }
    }

    public void draw(Graphics g, Color color, Random generator) {
        g.setColor(color);
        double angle = generator.nextDouble() * 2*PI;
        float x = (float)Math.cos(angle);
        float y = (float)Math.sin(angle);
        if (this._locationTunnel != null) {
            float radius = _FLIGHT_HEIGHT;
            x *= radius;
            y *= radius;
            float tunnelX = this._locationTunnel.getDestination().getX() - this._locationTunnel.getSource().getX();
            float tunnelY = this._locationTunnel.getDestination().getY() - this._locationTunnel.getSource().getY();
            float flown_fraction = (float)this._distanceInTunnel / (float)this._locationTunnel.getLength();
            x += this._locationTunnel.getSource().getX() + (tunnelX * flown_fraction);
            y += this._locationTunnel.getSource().getY() + (tunnelY * flown_fraction);
        } else {
            float radius = _FLIGHT_HEIGHT + (this._locationPlanet.getRadius()/2);
            x *= radius;
            y *= radius;
            x += this._locationPlanet.getX();
            y += this._locationPlanet.getY();
        }
        g.drawLine((int)x, (int)y, (int)x, (int)y);
    }

}
