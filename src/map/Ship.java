package map;

import AI.AI;

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

import static java.lang.Math.PI;

public class Ship {

    private final static float _FLIGHT_HEIGHT = 3.f;
    private final static float _SHIP_RADIUS = 1.2f;

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

    public void update(final AI ai) {
        if (this._locationTunnel != null) {
            this._distanceInTunnel++;
            if (this._distanceInTunnel >= this._locationTunnel.getLength()) {
                this._locationTunnel.moveShip(this);
                this._locationTunnel = null;
            }
        } else {
            int chosenTunnel = ai.chooseTunnel(this._owner, this._locationPlanet);
            if (chosenTunnel >= 0) {
                this._locationTunnel = this._locationPlanet.getTunnel(chosenTunnel);
                this._locationPlanet.moveShip(this, this._locationTunnel.getDestination());
                this._distanceInTunnel = 0;
                this._locationPlanet = this._locationTunnel.getDestination();
            }
        }
    }

    public void draw(Graphics2D g, Color color, Random generator) {
        g.setColor(color);
        double angle = generator.nextDouble() * 2*PI;
        float x = (float)Math.cos(angle);
        float y = (float)Math.sin(angle);
        if (this._locationTunnel != null) {
            float radius = _FLIGHT_HEIGHT;
            x *= radius;
            y *= radius;

            float fullTunnelX = this._locationTunnel.getDestination().getX() - this._locationTunnel.getSource().getX();
            float fullTunnelY = this._locationTunnel.getDestination().getY() - this._locationTunnel.getSource().getY();
            float fullTunnelLength = (float)Math.sqrt(Math.pow(fullTunnelX, 2) + Math.pow(fullTunnelY, 2));

            float sourcePlanetByTunnelFraction = (this._locationTunnel.getSource().getRadius() + _FLIGHT_HEIGHT) / fullTunnelLength;
            float sourcePlanetX = fullTunnelX * sourcePlanetByTunnelFraction;
            float sourcePlanetY = fullTunnelY * sourcePlanetByTunnelFraction;

            float destinationPlanetByTunnelFraction = (this._locationTunnel.getDestination().getRadius() + _FLIGHT_HEIGHT) / fullTunnelLength;
            float destinationPlanetX = fullTunnelX * destinationPlanetByTunnelFraction;
            float destinationPlanetY = fullTunnelY * destinationPlanetByTunnelFraction;

            float tunnelX = fullTunnelX - sourcePlanetX - destinationPlanetX;
            float tunnelY = fullTunnelY - sourcePlanetY - destinationPlanetY;

            float flown_fraction = (float)this._distanceInTunnel / (float)this._locationTunnel.getLength();
            x += this._locationTunnel.getSource().getX() + sourcePlanetX + (tunnelX * flown_fraction);
            y += this._locationTunnel.getSource().getY() + sourcePlanetY + (tunnelY * flown_fraction);
        } else {
            float radius = _FLIGHT_HEIGHT + (this._locationPlanet.getRadius());
            x *= radius;
            y *= radius;
            x += this._locationPlanet.getX();
            y += this._locationPlanet.getY();
        }
        g.fill(new Ellipse2D.Float((int)x - _SHIP_RADIUS, (int)y - _SHIP_RADIUS, 2*_SHIP_RADIUS, 2*_SHIP_RADIUS));
    }

}
