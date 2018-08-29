package AI;

import map.Planet;

import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class AI {

    private final static Random _GENERATOR = new Random();
    private final static int _NUMBER_OF_MATCHES_PER_GENERATION = 5;
    private final static float _PERCENT_OF_DELETED = 15.f;
    private final static int _NUMBER_OF_DNA_LAYERS = 10;

    private final int _NUMBER_OF_PLAYERS;

    private Player[] _players;
    private Vector<Participant> _participants = new Vector<>();
    private int _actMatch = 0;
    private int _numberOfGeneration = 0;

    public AI(int numberOfPlayers) {
        //Init
        this._NUMBER_OF_PLAYERS = numberOfPlayers;
        this._players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            this._players[i] = new Player(_NUMBER_OF_DNA_LAYERS);
        }

        //First generation
        while (this._participants.size() < _NUMBER_OF_MATCHES_PER_GENERATION * this._NUMBER_OF_PLAYERS) {
            this._participants.add(new Participant(new DNA(_NUMBER_OF_DNA_LAYERS, Player._NUMBER_OF_GENES_IN_LAYER)));
        }
        System.out.println("--- --- Generation " + Integer.toString(this._numberOfGeneration) + " started --- ---");
        //Prepare players
        for (int i = 0; i < this._players.length; i++) {
            this._players[i].setParticipant(this._participants.get(this._actMatch * _NUMBER_OF_PLAYERS + i));
        }
        this._actMatch++;
        System.out.println("Match " + Integer.toString(this._actMatch));
    }

    public int getGenerationNumber() { return this._numberOfGeneration; }
    public int getMatchNumber() { return this._actMatch; }
    public void setPlanetsAndShortestPathsMatrix(Planet[] planets, float[][] matrix) {
        for (Player p : _players) {
            p.setPlanetsAndShortestPathsMatrix(planets, matrix);
        }
    }

    public int chooseTunnel(int owner, Planet shipLocation) {
        return this._players[owner].chooseTunnel(owner, shipLocation);
    }

    public void improvePlayers(Planet[] planets) {
        //Check results
        int[] planetsControlled = new int[_NUMBER_OF_PLAYERS];
        int numberOfControlledPlanets = 0;
        for (Planet planet : planets) {
            if (planet != null) {
                if (planet.getOwner() >= 0) {
                    planetsControlled[planet.getOwner()]++;
                }
                numberOfControlledPlanets++;
            }
        }
        int mean = numberOfControlledPlanets / _NUMBER_OF_PLAYERS;
        for (int i = 0; i < _NUMBER_OF_PLAYERS; i++) {
            this._players[i].getParticipant().raiseControlledPlanetsCounter(planetsControlled[i]);
            this._players[i].getParticipant().raiseRequiredPlanetsCounter(mean);
        }

        //Change players
        if (this._actMatch >= _NUMBER_OF_MATCHES_PER_GENERATION) {
            //Delete bad participants
            Collections.sort(this._participants);
            this.printResults();
            while (this._participants.size() >
                    _NUMBER_OF_MATCHES_PER_GENERATION * this._NUMBER_OF_PLAYERS * (100 - _PERCENT_OF_DELETED) / 100) {
                this._participants.remove(this._participants.size() - 1);
            }
            //Add new participants
            while (this._participants.size() < _NUMBER_OF_MATCHES_PER_GENERATION * this._NUMBER_OF_PLAYERS) {
                int geneID1 = _GENERATOR.nextInt(this._participants.size());
                int geneID2;
                do {
                    geneID2 = _GENERATOR.nextInt(this._participants.size());
                } while (geneID1 == geneID2);
                this._participants.add(new Participant(new DNA(this._participants.get(geneID1).getDNA(),
                        this._participants.get(geneID2).getDNA())));
            }
            Collections.sort(this._participants);
            //Reset values
            this._actMatch = 0;
            this._numberOfGeneration++;
            System.out.println("--- --- Generation " + Integer.toString(this._numberOfGeneration) + " started --- ---");
        }
        //Prepare players
        for (int i = 0; i < this._players.length; i++) {
            this._players[i].setParticipant(this._participants.get(this._actMatch * _NUMBER_OF_PLAYERS + i));
        }
        this._actMatch++;
        System.out.println("Match " + Integer.toString(this._actMatch));
    }

    private void printResults() {
        System.out.println("Results:");
        for (Participant p : this._participants) {
            System.out.println(p.getControlledPlanetsRatio());
        }
    }

}
