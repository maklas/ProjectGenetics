package ru.maklas.genetics.mnw;

public interface CrashReport {

    void report(Exception e);

    void report(String error);

}
