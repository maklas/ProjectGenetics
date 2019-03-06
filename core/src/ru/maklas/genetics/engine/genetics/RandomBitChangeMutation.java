package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;

public class RandomBitChangeMutation implements MutationFunction {

    int bitsToMutateMin = 1;
    int bitsToMutateMax = 1;
    IntArray memory = new IntArray();

    public RandomBitChangeMutation() {

    }

    public RandomBitChangeMutation(int bitsToMutateMin, int bitsToMutateMax) {
        this.bitsToMutateMin = bitsToMutateMin;
        this.bitsToMutateMax = bitsToMutateMax;
    }

    public RandomBitChangeMutation(int bitsToMutate) {
        this(bitsToMutate, bitsToMutate);
    }

    @Override
    public void mutate(Engine engine, Chromosome child, Chromosome parentA, Chromosome parentB) {
        memory.clear();
        int bitsToMutate = MathUtils.random(bitsToMutateMin, bitsToMutateMax);

        for (int i = 0; i < bitsToMutate; i++) {
            int bitId = Utils.rand.nextInt(child.length());
            while (memory.contains(bitId)){
                bitId = Utils.rand.nextInt(child.length());
            }
            memory.add(bitId);
        }

        for (int i = 0; i < memory.size; i++) {
            child.changeBit(memory.get(i));
        }
    }


}
