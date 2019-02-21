package ru.maklas.genetics.utils;

public class Gray {

    public static byte encode(byte n){
        int i = n >= 0 ? n : n + 256;
        return (byte) (i ^ (i >>> 1));
    }

    public static byte[] encode(byte[] bytes){
        if (bytes == null) return null;
        byte[] gray = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            gray[i] = encode(bytes[i]);
        }
        return gray;
    }

    public static byte[] decode(byte[] gray){
        if (gray == null) return null;
        byte[] bytes = new byte[gray.length];
        for (int i = 0; i < gray.length; i++) {
            bytes[i] = decode(gray[i]);
        }
        return bytes;
    }

    public static byte decode(byte n) {
        int i = n >= 0 ? n : n + 256;
        int p = i;
        while ((i >>>= 1) != 0)
            p ^= i;
        return ((byte) p);
    }

}
