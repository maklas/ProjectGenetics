package ru.maklas.genetics.tests;

import com.badlogic.gdx.math.MathUtils;
import ru.maklas.genetics.states.GenerationDistribution;
import ru.maklas.genetics.utils.BitArray;
import ru.maklas.genetics.utils.Utils;

/** Ген. Представлят собой какой-то признак в хромосоме **/
public class Gene {

    private BitArray bits;
    private String name = "Gene"; //Название гена
    private double minDouble = 0; //минимальное значение при перекодировании в double
    private double maxDouble = 1; //максимальное значение при перекодировании в double


    public Gene(int bits) {
        this.bits = new BitArray(bits);
    }

    Gene(Gene gene) {
        bits = gene.bits.cpy();
        this.name = gene.name;
        this.minDouble = gene.minDouble;
        this.maxDouble = gene.maxDouble;
    }

    /** Данные в коде грея, так как они хранятся **/
    public BitArray getRawData() {
        return bits;
    }
    public String getName() {
        return name;
    }

    public Gene setName(String name) {
        this.name = name;
        return this;
    }

    public double getMinDouble() {
        return minDouble;
    }

    public void setMinDouble(double minDouble) {
        this.minDouble = minDouble;
    }

    public double getMaxDouble() {
        return maxDouble;
    }

    public void setMaxDouble(double maxDouble) {
        this.maxDouble = maxDouble;
    }

    public Gene setMinMaxDouble(double min, double max){
        this.minDouble = min;
        this.maxDouble = max;
        return this;
    }

    /** рандомно заполняет данные **/
    public Gene randomize() {
        for (int i = 0; i < bits.length(); i++) {
            bits.set(i, Utils.rand.nextBoolean());
        }
        return this;
    }

    public Gene randomize(GenerationDistribution distribution) {
        switch (distribution){
            case EVEN:
                randomize();
                break;
            case RIGHT:
                set(MathUtils.randomTriangular(((float) (getMinDouble() + ((getMaxDouble() - getMinDouble()) / 2d))), ((float) getMaxDouble()), ((float) getMaxDouble())));
                break;
            case LEFT:
                float center = (float) (getMinDouble() + ((getMaxDouble() - getMinDouble()) / 2d));
                set(MathUtils.randomTriangular(((float) getMinDouble()), center, center));
                break;
            case CENTER:
                float middleLeft = (float) (getMinDouble() + ((getMaxDouble() - getMinDouble()) / 4d));
                float middleRight = (float) (getMaxDouble() - ((getMaxDouble() - getMinDouble()) / 4d));
                set(MathUtils.randomTriangular(middleLeft, middleRight));
                break;
        }
        return this;
    }

    /** Декодирует данные из кода грея и возвращает **/
    public BitArray getDecodedData(){
        return bits.grayToBin();
    }

    /** Длинна массива битов из которых состоит ген**/
    public int length() {
        return bits.length();
    }

    /** декодирует данные из GrayCode и превращаем в Long **/
    public long decodeAsLong(){
        return bits.grayToBin().asLong();
    }

    /** Перекодируем Long в GrayCode и помещаем в ген xxxx000...**/
    public void set(long l){
        bits.setAsLong(l);
        bits = bits.binToGray();
    }

    /** декодирует данные из GrayCode и превращаем в Double**/
    public double decodeAsDouble(){
        long i = decodeAsLong();
        long maxVal = bits.maxValue();
        return (((maxDouble - minDouble) / maxVal) * i) + minDouble;
    }

    /** Перекодируем Double в GrayCode и помещаем в ген xxxx000...**/
    public void set(double d){
        bits.setAsLong(fromDouble(d, minDouble, maxDouble, length()));
        bits = bits.binToGray();
    }

    public boolean getBit(int position) {
        return bits.get(position);
    }

    public Gene setBit(int position, boolean set){
        bits.set(position, set);
        return this;
    }

    public static long fromDouble(double d, double min, double max, int bitLength){
        if (d > max || d < min) throw new RuntimeException("f = " + d + " while range is [" + min + ", " + max + "]");
        long division = (1L << (bitLength)) - 1;
        return (long)(((d - min) / (max - min)) * division);
    }

    public Gene cpy(){
        return new Gene(this);
    }

    @Override
    public String toString() {
        return "{" + '\n' +
                "name = '" + name + '\'' + '\n' +
                "gray = " + bits + '\n' +
                "decoded = " + bits.grayToBin() + '\n' +
                "realGap = (" + minDouble + ", " + maxDouble + ")\n" +
                "asLong = " + decodeAsLong() + '\n' +
                "asDouble = " + decodeAsDouble() + '\n' +
                '}';
    }

    public Gene fill(boolean set) {
        bits.fill(set);
        return this;
    }
}
