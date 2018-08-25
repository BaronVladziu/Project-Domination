package AI;

import map.Planet;

public class AI {

    private Player[] _players;

    public AI(int numberOfPlayers) {
        this._players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            this._players[i] = new Player();
        }
    }

    public int chooseTunnel(int owner, Planet shipLocation) {
        return this._players[owner].chooseTunnel(owner, shipLocation);
    }

}
