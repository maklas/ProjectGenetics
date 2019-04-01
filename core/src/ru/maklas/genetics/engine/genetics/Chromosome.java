package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import org.intellij.lang.annotations.MagicConstant;

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

    public Gene get(@MagicConstant(flagsFromClass = GeneNames.class) String geneName){
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

    public String genesString(){
        if (genes.size == 0) return "";
        StringBuilder sb = new StringBuilder();

        for (Gene gene : genes) {
            sb.append("(").append(gene.getName()).append("|")
                    .append(gene.getRawData().toStringSmart(8)).append(")");
        }

        return sb.toString();
    }

    public Chromosome cpy() {
        Chromosome chromosome = new Chromosome();
        for (Gene gene : genes) {
            chromosome.add(gene.cpy());
        }
        return chromosome;
    }

    public boolean getBit(int pos) {
        for (int i = genes.size - 1; i >= 0; i--) {
            Gene gene = genes.get(i);
            if (gene.length() <= pos){
                pos-= gene.length();
            } else {
                return gene.getBit(pos);
            }
        }
        return false;
    }

    public void set(int pos, boolean set) {
        for (int i = genes.size - 1; i >= 0; i--) {
            Gene gene = genes.get(i);
            if (pos >= gene.length()){
                pos -= gene.length();
            } else {
                gene.setBit(pos, set);
            }
        }
    }

    public void changeBit(int pos) {
        set(pos, !getBit(pos));
    }
}
