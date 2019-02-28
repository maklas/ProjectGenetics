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


    // Helper selectedFunction to xor
    // two characters
    private static char xor_c(char a, char b) {
        return (a == b) ? '0' : '1';
    }

    // Helper selectedFunction to flip the bit
    private static char flip(char c) {
        return (c == '0') ? '1' : '0';
    }

    // selectedFunction to convert binary
    // string to gray string
    public static String binarytoGray(String binary) {
        String gray = "";

        // MSB of gray code is same
        // as binary code
        gray += binary.charAt(0);

        // Compute remaining bits, next bit is
        // comuted by doing XOR of previous
        // and current in Binary
        for (int i = 1; i < binary.length(); i++) {
            // Concatenate XOR of previous bit
            // with current bit
            gray += xor_c(binary.charAt(i - 1),
                    binary.charAt(i));
        }

        return gray;
    }

    // selectedFunction to convert gray code
    // string to binary string
    public static String graytoBinary(String gray) {
        String binary = "";

        // MSB of binary code is same
        // as gray code
        binary += gray.charAt(0);

        // Compute remaining bits
        for (int i = 1; i < gray.length(); i++) {
            // If current bit is 0,
            // concatenate previous bit
            if (gray.charAt(i) == '0')
                binary += binary.charAt(i - 1);

                // Else, concatenate invert of
                // previous bit
            else
                binary += flip(binary.charAt(i - 1));
        }
        return binary;
    }

}
