package AI;

public class NeuralNetwork {

    private final DNA _dna;
    private final int _layerLength;

    NeuralNetwork(final DNA dna) {
        this._dna = dna;
        this._layerLength = this._dna.getNumberOfGenesInLayer();
    }

    final int getInputLength() { return this._layerLength; }

    final float getResult(float[] input) {
        float[] prevResultsLayer = input;

        for (int layer = 0; layer < this._dna.getNumberOfLayers(); layer++) {
            float[] actResultsLayer = new float[this._layerLength];
            for (int actN = 0; actN < this._layerLength; actN++) {
                actResultsLayer[actN] = 0;
                for (int prevN = 0; prevN < this._layerLength; prevN++) {
                    actResultsLayer[actN] += prevResultsLayer[prevN] * this._dna.getGene(layer, prevN);
                }
                actResultsLayer[actN] = this.transferFunction(actResultsLayer[actN]);
            }
            prevResultsLayer = actResultsLayer;
        }

        float result = 0;
        for (int i = 0; i < this._layerLength; i++) {
            result += prevResultsLayer[i];
        }
        return result;
    }

    private float transferFunction(float x) {
        return 1 / (float)(1 + Math.exp(-x)); //Sigmoid function
    }

}
