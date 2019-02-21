package ru.maklas.genetics.statics;

import ru.maklas.libs.Counter;

public class ID {

    public static final int background = 1;
    public static final int camera = 2;

    public static Counter counterForSockets(){
        return new Counter(200_000, 300_000);
    }

}
