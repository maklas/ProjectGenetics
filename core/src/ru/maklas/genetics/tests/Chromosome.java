package ru.maklas.genetics.tests;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.utils.BinaryUtils;

/**
 * Хромосома (особь, генотип).
 * Является совокупностью генов и представляет одну особь
 */
public class Chromosome {

    private Array<Gene> genes = new Array<>(); //Гены

    public Chromosome add(Gene gene){
        genes.add(gene);
        return this;
    }

    public Gene get(String geneName){
        if (geneName == null) return null;
        for (Gene gene : genes) {
            if (geneName.equals(gene.getName())){
                return gene;
            }
        }
        return null;
    }

    public int length(){
        int len = 0;
        for (Gene gene : genes) {
            len += gene.length();
        }
        return len;
    }

    public Array<Gene> getGenes() {
        return genes;
    }


    @Override
    public String toString() {
        return "{" + '\n' +
                "genes=" + genes + '\n' +
                '}';
    }

    public String byteCode(){
        if (genes.size > 0) {
            StringBuilder sb = new StringBuilder();
            for (Gene gene : genes) {
                sb.append(gene.getRawData());
                sb.append(" ");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
        return "";
    }
}
