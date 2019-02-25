package ru.maklas.genetics.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.utils.*;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {

    public static void main(String[] args){
        BitArray bits = new BitArray(16);
        for (int i = 0; i < Math.pow(2, 12); i++) {
            bits.setAsLong(i);
            String s = bits.toString();
            BitArray gray = bits.binToGray();
            System.out.println(i + ": " + s + " -> " + gray.toString(8) + " -> " + (s.equals(gray.grayToBin().toString()) ? " Ok" : " Fail"));
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
