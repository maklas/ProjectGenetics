package ru.maklas.genetics.utils;

public class BinaryUtils {

    public static String toBinString(byte b){
        int i = b >= 0 ? b : b + 256;
        return StringUtils.addSpacesLeft(Integer.toBinaryString((i)), 8).replaceAll(" ", "0");
    }

    public static String toBinString(byte[] bytes){
        if (bytes == null || bytes.length == 0) return "";

        StringBuilder sb = new StringBuilder(toBinString(bytes[0]));
        for (int i = 1; i < bytes.length; i++) {
            sb.append(" ").append(toBinString(bytes[i]));
        }
        return sb.toString();
    }

    public static String toBinStringNoSplits(byte[] bytes){
        if (bytes == null || bytes.length == 0) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(toBinString(bytes[i]));
        }
        return sb.toString();
    }

    public static byte[] merge(byte[] a, byte[] b, int bitPosition){
        int length = a.length;
        byte[] result = new byte[length];
        int byteP = 0;
        while (bitPosition >= 8){
            result[byteP] = a[byteP];
            bitPosition -= 8;
            byteP++;
        }
        if (bitPosition > 0){
            byte mergeByte = 0;
            for (int i = 0; i < 8; i++) {
                mergeByte = setBit(mergeByte, 7 - i, getBit((i < bitPosition ? a : b)[byteP], 7 - i));
            }
            result[byteP] = mergeByte;
            byteP++;
        }
        while (byteP < length){
            result[byteP] = b[byteP];
            byteP++;
        }
        return result;
    }

    public static byte getBit(byte b, int position) {
        return (byte)((b >> position) & 1);
    }

    public static byte setBit(byte b, int position, byte val) {
        return (byte) (val == 1 ?
                b | (1 << position) :
                b & ~(1 << position));
    }


}
