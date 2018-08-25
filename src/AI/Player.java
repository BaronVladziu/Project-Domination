package AI;

import map.Planet;

public class Player {

    private final static int _NUMBER_OF_LAYERS = 3;
    private final static int _NUMBER_OF_GENES_IN_LAYER = 4;

    private DNA _dna;
    private NeuralNetwork _neuralNetwork;

    Player() {
        this._dna = new DNA(_NUMBER_OF_LAYERS, _NUMBER_OF_GENES_IN_LAYER);
        this._neuralNetwork = new NeuralNetwork(this._dna);
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
        neuralNetworkInput[0] = planet.getSize();
        if (planet.getOwner() == playerID) {
            neuralNetworkInput[1] = 1;
        } else {
            neuralNetworkInput[1] = -1;
        }
        neuralNetworkInput[2] = planet.getNumberOfShips(playerID);
        neuralNetworkInput[3] = planet.getNumberOfShips() - neuralNetworkInput[2];
        return neuralNetworkInput;
    }

}
