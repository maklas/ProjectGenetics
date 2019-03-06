package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;

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


    public Children cross(Chromosome a, Chromosome b, int[] crossingPoints){
        //5
        //0, 3, 5, 10
        //0, 3, 5, 10, 16

        Chromosome childA = a.cpy();
        Chromosome childB = b.cpy();


        int currentPosition = 0;
        boolean switching = false;
        for (int i = 0; i < crossingPoints.length; i++) {
            int crossingPoint = crossingPoints[i];
            while (currentPosition < crossingPoint){
                if (switching){
                    childA.set(currentPosition, b.getBit(currentPosition));
                    childB.set(currentPosition, a.getBit(currentPosition));
                }
                currentPosition++;
            }
            switching = !switching;
        }
        while (currentPosition < a.length()){
            if (switching){
                childA.set(currentPosition, b.getBit(currentPosition));
                childB.set(currentPosition, a.getBit(currentPosition));
            }
            currentPosition++;
        }

        return new Children(childA, childB);
    }

    public static class Children {
        public Chromosome childA;   //a + b
        public Chromosome childB;   //b + a

        public Children(Chromosome childA, Chromosome childB) {
            this.childA = childA;
            this.childB = childB;
        }
    }

}
