package AI;

import map.Planet;

public class Player {

    final static int _NUMBER_OF_GENES_IN_LAYER = 9;

    private Participant _participant;
    private NeuralNetwork _neuralNetwork;
    private Planet[] _planets;
    private float[][] _shortestPathsMatrix;

    Player(int numberOfLayers) {
        this._participant = new Participant(new DNA(numberOfLayers, _NUMBER_OF_GENES_IN_LAYER));
        this._neuralNetwork = new NeuralNetwork(this._participant.getDNA());
    }

    void setParticipant(final Participant participant) {
        this._participant = participant;
        this._neuralNetwork = new NeuralNetwork(this._participant.getDNA());
    }
    Participant getParticipant() { return this._participant; }
    void setPlanetsAndShortestPathsMatrix(Planet[] planets, float[][] matrix) {
        this._planets = planets;
        this._shortestPathsMatrix = matrix;
    }

    int chooseTunnel(int playerID, Planet shipLocation) {
        int bestTunnel = -1;
        float bestImportance = this._neuralNetwork.getResult(this.createNetworkInput(playerID, shipLocation));
        for (int nearbyPlanetID = 0; nearbyPlanetID < shipLocation.getNumberOfTunnels(); nearbyPlanetID++) {
            Planet targetPlanet = shipLocation.getTunnel(nearbyPlanetID).getDestination();
            float targetImportance = this._neuralNetwork.getResult(this.createNetworkInput(playerID, targetPlanet));
            if (targetImportance > bestImportance) {
                bestImportance = targetImportance;
                bestTunnel = nearbyPlanetID;
            }
        }
        return bestTunnel;
    }

    private float[] createNetworkInput(int playerID, Planet planet) {
        float[] neuralNetworkInput = new float[_NUMBER_OF_GENES_IN_LAYER];
        neuralNetworkInput[0] = this.isPlanetMine(playerID, planet);
        neuralNetworkInput[1] = this.isPlanetNeutral(planet);
        neuralNetworkInput[2] = this.getMyShips(playerID, planet);
        neuralNetworkInput[3] = this.getEnemyShips(playerID, planet);

        float myShipsByDistance = 0;
        float enemyShipsByDistance = 0;
        float myPlanetsByDistance = 0;
        float enemyPlanetsByDistance = 0;
        float neutralPlanetsByDistance = 0;
        for (int p = 0; p < this._shortestPathsMatrix.length; p++) {
            if (p != planet.getID()) {
                float dist = this._shortestPathsMatrix[p][planet.getID()];
                int myShipsOnP = this._planets[p].getNumberOfShips(playerID);
                myShipsByDistance += myShipsOnP / dist;
                enemyShipsByDistance += (this._planets[p].getNumberOfShips() - myShipsOnP) / dist;
                if (this._planets[p].getOwner() == playerID) {
                    myPlanetsByDistance += 1 / dist;
                } else if (this._planets[p].getOwner() == -1) {
                    neutralPlanetsByDistance += 1 / dist;
                } else {
                    enemyPlanetsByDistance += 1 / dist;
                }
            }
        }
        neuralNetworkInput[4] = myShipsByDistance;
        neuralNetworkInput[5] = enemyShipsByDistance;
        neuralNetworkInput[6] = myPlanetsByDistance;
        neuralNetworkInput[7] = enemyPlanetsByDistance;
        neuralNetworkInput[8] = neutralPlanetsByDistance;

        return neuralNetworkInput;
    }

    private float isPlanetMine(int playerID, Planet planet) {
        if (planet.getOwner() == playerID) {
            return 1.f;
        } else {
            return -1.f;
        }
    }
    private float isPlanetNeutral(Planet planet) {
        if (planet.getOwner() == -1) {
            return 1.f;
        } else {
            return -1.f;
        }
    }
    private float getMyShips(int playerID, Planet planet) {
        return planet.getNumberOfShips(playerID);
    }
    private float getEnemyShips(int playerID, Planet planet) {
        return planet.getNumberOfShips() - planet.getNumberOfShips(playerID);
    }

}
