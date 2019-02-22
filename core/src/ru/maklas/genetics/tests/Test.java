package ru.maklas.genetics.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.utils.BinaryUtils;
import ru.maklas.genetics.utils.ClassUtils;
import ru.maklas.genetics.utils.StringUtils;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {

    public static void main(String[] args){
        Chromosome a = new Chromosome().add(new Gene(4)).add(new Gene(4));
        Chromosome b = new Chromosome().add(new Gene(new byte[]{-1, -1, -1, -1})).add(new Gene(new byte[]{-1, -1, -1, -1}));
        for (int i = 0; i < 64; i++) {
            String s = new Crossover().cross(a, b, i).byteCode();
            System.out.println(s + (StringUtils.countChars(s, '0') == i ? "" : " Fail"));
        }
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
