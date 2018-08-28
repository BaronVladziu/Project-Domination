package AI;

public class Participant implements Comparable<Participant> {

    private final DNA _dna;
    private int _wins = 0;
    private int _looses = 0;

    Participant(DNA dna) {
        this._dna = dna;
    }

    @Override
    public int compareTo(Participant p) {
        return (int)(p.getWinRatio()*100 - this.getWinRatio()*100);
    }

    float getWinRatio() {
        if (this.getNumberOfPlays() == 0) {
            return 0;
        } else {
            return (float)(this._wins) / (float)(this.getNumberOfPlays());
        }
    }
    int getNumberOfPlays() { return this._wins + this._looses; }
    DNA getDNA() { return this._dna; }

    void win() {
        this._wins++;
    }
    void loose() {
        this._looses++;
    }

}
