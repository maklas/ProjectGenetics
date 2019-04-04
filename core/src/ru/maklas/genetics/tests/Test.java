package ru.maklas.genetics.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.engine.genetics.Chromosome;
import ru.maklas.genetics.engine.genetics.Crossover;
import ru.maklas.genetics.engine.genetics.Gene;
import ru.maklas.genetics.functions.bi_functions.SerovNashBiFunction;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.utils.BitArray;
import ru.maklas.genetics.utils.ClassUtils;
import ru.maklas.genetics.utils.StringUtils;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {

    public static void main(String[] args){
        //Chromosome a = new Chromosome().add(new Gene(BitArray.fromString("101101001101110011110000"))).add(new Gene(BitArray.fromString("010000111110010110101110")));
        //Chromosome b = new Chromosome().add(new Gene(BitArray.fromString("110110100101111000100010"))).add(new Gene(BitArray.fromString("101000110111000111000001")));
        Chromosome a = new Chromosome().add(new Gene(BitArray.fromString("101010101010101010101010"))).add(new Gene(BitArray.fromString("101010101010101010101010")));
        Chromosome b = new Chromosome().add(new Gene(BitArray.fromString("010101010101010101010101"))).add(new Gene(BitArray.fromString("010101010101010101010101")));
        Crossover.Children children = new Crossover().cross(a, b, new int[]{6, 38});
        System.out.println(a.byteCode());
        System.out.println(b.byteCode());
        System.out.println("   V");
        System.out.println(children.childA.byteCode());
        System.out.println(children.childB.byteCode());


        SerovNashBiFunction f1 = new SerovNashBiFunction(100, 250, 0.2, 0.8);
        SerovNashBiFunction f2 = new SerovNashBiFunction(250, 100, 0.8, 0.2);

        double x = 200;
        for (double y = 0; y < 300; y++) {
            System.out.println(new Vector2((float) x, (float) y) + "F1: " + StringUtils.dfSigDigits(f1.f(x, y), 1, 3) + ", F2: " + StringUtils.dfSigDigits(f2.f(x, y), 1, 3));
        }

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
