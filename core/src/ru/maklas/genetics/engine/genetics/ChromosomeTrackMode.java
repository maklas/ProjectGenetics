package ru.maklas.genetics.engine.genetics;

public enum ChromosomeTrackMode {

    CURRENT_GEN,
    SELECTED
    ;



    public String asText(){
        switch (this){
            case CURRENT_GEN:
                return "Current generation";
            case SELECTED:
                return "Selected";
        }
        return "";
    }
}
