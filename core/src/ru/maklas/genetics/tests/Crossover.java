package ru.maklas.genetics.tests;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.utils.BinaryUtils;

public class Crossover {

    public Chromosome cross(Chromosome a, Chromosome b, int crossPoint){
        if (a.length() != b.length()){
            throw new RuntimeException("Chromosome lengths don't match");
        }

        Array<Gene> genesA = a.getGenes();
        Array<Gene> genesB = b.getGenes();
        Chromosome child = new Chromosome();
        for (int i = 0; i < genesA.size; i++) {
            if (genesA.get(i).length() != genesB.get(i).length()){
                throw new RuntimeException("Genes don't match: \n " + genesA.get(i) + "\n" + genesB.get(i));
            }
            Gene geneA = genesA.get(i);
            Gene geneB = genesB.get(i);
            if (crossPoint <= 0){ //Прошли точку, просто переливаем что есть из второй хромосомы.
                child.add(geneB.cpy());
                crossPoint -= geneB.length();
            } else if (crossPoint > geneA.length()){ //До точки ещё далеко, берем из А.
                child.add(geneA.cpy());
                crossPoint -= geneA.length();
            } else { //Точка кроссовера находится где-то посередине этого гена.
                Gene cpy = geneA.cpy();
                for (int j = crossPoint; j < cpy.length(); j++) {
                    cpy.setBit(j, geneB.getBit(j));
                }
                crossPoint -= geneA.length();
                child.add(cpy);
            }
        }
        return child;
    }

}
