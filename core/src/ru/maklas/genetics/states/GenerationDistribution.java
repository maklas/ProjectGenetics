package ru.maklas.genetics.states;

public enum GenerationDistribution {

    EVEN,
    RIGHT,
    LEFT,
    CENTER;


    @Override
    public String toString(){
        switch (this){
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
