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


}
