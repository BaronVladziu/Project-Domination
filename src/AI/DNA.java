package AI;

import java.util.Random;

public class DNA {

    private final static Random _GENERATOR = new Random();
    private final static float _MAX_GENE_VALUE = 2.f;

    private final int _NUMBER_OF_LAYERS;
    private final int _NUMBER_OF_GENES_IN_LAYER;

    private float[][] _genes;

    DNA(int numberOfLayers, int numberOfGenesInLayer) {
        this._NUMBER_OF_LAYERS = numberOfLayers;
        this._NUMBER_OF_GENES_IN_LAYER = numberOfGenesInLayer;
        this._genes = new float[_NUMBER_OF_LAYERS][_NUMBER_OF_GENES_IN_LAYER];
        //Randomize genes
        for (int l = 0; l < _NUMBER_OF_LAYERS; l++) {
            for (int g = 0; g < _NUMBER_OF_GENES_IN_LAYER; g++) {
                this._genes[l][g] = _GENERATOR.nextFloat()*2*_MAX_GENE_VALUE - _MAX_GENE_VALUE;
            }
        }
    }

    DNA(DNA dna1, DNA dna2) {
        if (dna1.getNumberOfLayers() != dna2.getNumberOfLayers() ||
                dna1.getNumberOfGenesInLayer() != dna2.getNumberOfGenesInLayer()) {
            System.out.println("ERROR: Genes not mergable!");
        }
        this._NUMBER_OF_LAYERS = dna1.getNumberOfLayers();
        this._NUMBER_OF_GENES_IN_LAYER = dna1.getNumberOfGenesInLayer();
        this._genes = new float[_NUMBER_OF_LAYERS][_NUMBER_OF_GENES_IN_LAYER];
        //Merge genes randomly
        for (int l = 0; l < _NUMBER_OF_LAYERS; l++) {
            for (int g = 0; g < _NUMBER_OF_GENES_IN_LAYER; g++) {
                if (_GENERATOR.nextBoolean()) {
                    this._genes[l][g] = dna1.getGene(l, g);
                } else {
                    this._genes[l][g] = dna2.getGene(l, g);
                }
            }
        }
        //Randomize one gene
        int randomLayer = _GENERATOR.nextInt(_NUMBER_OF_LAYERS);
        int randomGene = _GENERATOR.nextInt(_NUMBER_OF_GENES_IN_LAYER);
        this._genes[randomLayer][randomGene] = _GENERATOR.nextFloat()*2*_MAX_GENE_VALUE - _MAX_GENE_VALUE;
    }

    final int getNumberOfLayers() { return _NUMBER_OF_LAYERS; }
    final int getNumberOfGenesInLayer() { return _NUMBER_OF_GENES_IN_LAYER; }
    final float getGene(int layer, int gene) { return this._genes[layer][gene]; }

}
