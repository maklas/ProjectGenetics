package ru.maklas.genetics.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.utils.BitArray;
import ru.maklas.genetics.utils.ClassUtils;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {

    public static void main(String[] args){
        Chromosome a = new Chromosome().add(new Gene(BitArray.fromString("1110101")));
        Chromosome b = new Chromosome().add(new Gene(BitArray.fromString("0101100")));
        Crossover.Children children = new Crossover().cross(a, b, new int[]{1, 8});
        System.out.println(a.byteCode());
        System.out.println(b.byteCode());
        System.out.println("   V");
        System.out.println(children.childA.byteCode());
        System.out.println(children.childB.byteCode());
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
