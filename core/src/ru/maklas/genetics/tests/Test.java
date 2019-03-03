package ru.maklas.genetics.tests;


import com.badlogic.gdx.math.CumulativeDistribution;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.utils.*;

import java.util.Arrays;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {

    public static void main(String[] args){
        CumulativeDistribution<Integer> cd = new CumulativeDistribution<>();
        for (int i = 0; i < 10; i++) {
            cd.add(i, (i + 1) / 10f);
        }
        cd.generate();
        int [] counts = new int[10];
        for (int i = 0; i < 1_000_000; i++) {
            Integer value = cd.value(Utils.rand.nextFloat() * 5.5f);
            counts[value]++;
        }

        System.out.println(Arrays.toString(counts));
    }

    private static String toString(BitArray bits){
        return bits.toString(4) + " (" + bits.asLong() + "/" + bits.maxValue() + ")";
    }

    private static void countStrings(){
        System.out.println(ClassUtils.countStrings(MNW.PACKAGE, false, false, false));
    }

    private static void testLerp(){
        Vector2 a = new Vector2();
        Vector2 b = new Vector2();
        Vector2 c = new Vector2();

        b.set(100, 100);
        for (int i = 0; i < 6; i++) {
            a.set(-100, -100);
            System.out.println(a.lerp(b, i * 0.2f));
        }
    }
}
