package ru.maklas.genetics.engine.genetics;

public enum GenerationDistribution {

    RANDOM,
    EVEN,
    RIGHT,
    LEFT,
    CENTER;


    @Override
    public String toString(){
        switch (this){
            case RANDOM:
                return "Random";
            case EVEN:
                return "Even";
            case RIGHT:
                return "Right side";
            case LEFT:
                return "Left side";
            case CENTER:
                return "Center";
        }
        return "";
    }

}
