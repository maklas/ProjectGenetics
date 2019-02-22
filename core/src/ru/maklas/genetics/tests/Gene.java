package ru.maklas.genetics.tests;

import ru.maklas.genetics.utils.BinaryUtils;
import ru.maklas.genetics.utils.Gray;
import ru.maklas.genetics.utils.Utils;

/** Ген. Представлят собой какой-то признак в хромосоме **/
public class Gene {

    private byte[] data; //Encoded as Gray Code.
    private final int length; //Длинна гена в байтах
    private String name = "Gene"; //Название гена
    private double minDouble = 0; //минимальное значение при перекодировании в double
    private double maxDouble = 1; //максимальное значение при перекодировании в double

    public Gene(byte[] data) {
        this.data = data;
        this.length = data.length;
    }

    public Gene(int byteSize){
        this.data = new byte[byteSize];
        this.length = byteSize;
    }

    Gene(Gene gene) {
        this.length = gene.length;
        this.name = gene.name;
        this.data = new byte[gene.length];
        this.minDouble = gene.minDouble;
        this.maxDouble = gene.maxDouble;
        System.arraycopy(gene.data, 0, data, 0, gene.length);
    }

    /** Данные в коде грея, так как они хранятся**/
    public byte[] getRawData() {
        byte[] buffer = new byte[data.length];
        System.arraycopy(data, 0, buffer, 0, length);
        return buffer;
    }

    public Gene setRawData(byte[] data) {
        if (this.data.length != data.length) throw new RuntimeException("Data doesn't match length");
        System.arraycopy(data, 0, this.data, 0, data.length);
        return this;
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
        Utils.rand.nextBytes(data);
        return this;
    }

    /** Декодирует данные из кода грея и возвращает**/
    public byte[] getDecodedData(){
        return Gray.decode(data);
    }

    /** Длинна массива байтов из которых состоит ген**/
    public int length() {
        return length;
    }

    /** декодирует данные из GrayCode и превращаем в Long **/
    public long decodeAsLong(){
        return toLong(getDecodedData());
    }

    /** Перекодируем Long в GrayCode и помещаем в ген xxxx000...**/
    public void set(long l){
        for (int j = 8; j < data.length; j++) {
            data[j] = 0;
        }
        byte[] newData = Gray.encode(fromLong(l));
        int srcPos = 8 - length < 0 ? 0 : 8 - length;
        int destPos = length - 8 < 0 ? 0 : length - 8;
        System.arraycopy(newData, srcPos, data, destPos, Math.min(8, data.length));
    }

    /** декодирует данные из GrayCode и превращаем в Double**/
    public double decodeAsDouble(){
        return toDouble(getDecodedData(), minDouble, maxDouble);
    }

    /** Перекодируем Double в GrayCode и помещаем в ген xxxx000...**/
    public void set(double d){
        for (int j = 8; j < data.length; j++) {
            data[j] = 0;
        }
        byte[] newData = Gray.encode(fromDouble(d, minDouble, maxDouble, data.length));
        int srcPos = 8 - length < 0 ? 0 : 8 - length;
        int destPos = length - 8 < 0 ? 0 : length - 8;
        System.arraycopy(newData, srcPos, data, destPos, Math.min(8, data.length));
    }

    public boolean getBit(int position) {
        int byteP = position / 8;
        int bitP = position % 8;
        return BinaryUtils.getBit(data[byteP], 7 - bitP) == 1;
    }

    public Gene setBit(int position, boolean on){
        int byteP = position / 8;
        int bitP = position % 8;
        data[byteP] = BinaryUtils.setBit(data[byteP], 7 - bitP, (byte)(on ? 1 : 0));
        return this;
    }

    public static long toLong(byte[] bytes){
        long result = 0;
        int iterations = Math.min(bytes.length, 8);
        for (int i = 0; i < iterations; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static byte[] fromLong(long i){
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (i >>> 56);
        bytes[1] = (byte) (i >>> 48);
        bytes[2] = (byte) (i >>> 40);
        bytes[3] = (byte) (i >>> 32);
        bytes[4] = (byte) (i >>> 24);
        bytes[5] = (byte) (i >>> 16);
        bytes[6] = (byte) (i >>> 8);
        bytes[7] = (byte)  i;
        return bytes;
    }

    public static double toDouble(byte[] bytes, double min, double max){
        long i = toLong(bytes);
        long division = (1L << (bytes.length * 8)) - 1;
        return (((max - min) / division) * i) + min;
    }

    public static byte[] fromDouble(double d, double min, double max, int byteLength){
        if (d > max || d < min) throw new RuntimeException("f = " + d + " while range is [" + min + ", " + max + "]");
        long division = (1L << (byteLength * 8)) - 1;
        return fromLong((long)(((d - min) / (max - min)) * division));
    }

    public Gene cpy(){
        return new Gene(this);
    }

    @Override
    public String toString() {
        return "{" + '\n' +
                "name = '" + name + '\'' + '\n' +
                "gray = " + BinaryUtils.toBinString(getRawData()) + '\n' +
                "decoded = " + BinaryUtils.toBinString(getDecodedData()) + '\n' +
                "realGap = (" + minDouble + ", " + maxDouble + ")\n" +
                "asLong = " + decodeAsLong() + '\n' +
                "asDouble = " + decodeAsDouble() + '\n' +
                '}';
    }
}
