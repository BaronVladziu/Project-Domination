package AI;

public class Participant implements Comparable<Participant> {

    private final DNA _dna;
    private int _got = 0;
    private int _shouldGet = 0;

    Participant(DNA dna) {
        this._dna = dna;
    }

    @Override
    public int compareTo(Participant p) {
        return (int)(p.getControlledPlanetsRatio()*100 - this.getControlledPlanetsRatio()*100);
    }

    float getControlledPlanetsRatio() {
        if (this.getNumberOfRequiredPlanets() == 0) {
            return 0;
        } else {
            return (float)(this._got) / (float)(this._shouldGet);
        }
    }
    int getNumberOfRequiredPlanets() { return this._shouldGet; }
    DNA getDNA() { return this._dna; }

    void raiseControlledPlanetsCounter(int n) { this._got += n; }
    void raiseRequiredPlanetsCounter(int n) { this._shouldGet += n; }

}
