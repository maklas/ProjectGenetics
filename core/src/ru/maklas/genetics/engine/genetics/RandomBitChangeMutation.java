package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;

public class RandomBitChangeMutation implements MutationFunction {

    float mutationChance;
    int bitsToMutateMin = 1;
    int bitsToMutateMax = 1;
    IntArray memory = new IntArray();

    public RandomBitChangeMutation() {

    }

    public RandomBitChangeMutation(int bitsToMutateMin, int bitsToMutateMax, float mutationChance) {
        this.bitsToMutateMin = bitsToMutateMin;
        this.bitsToMutateMax = bitsToMutateMax;
        this.mutationChance = MathUtils.clamp(mutationChance, 0, 1);
    }

    public RandomBitChangeMutation(int bitsToMutate, float mutationChance) {
        this(bitsToMutate, bitsToMutate, mutationChance);
    }

    @Override
    public void mutate(Engine engine, Chromosome child, Chromosome parentA, Chromosome parentB) {
        if (MathUtils.random() > mutationChance) return;
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
