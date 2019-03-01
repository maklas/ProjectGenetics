package ru.maklas.genetics.engine.genetics;

public enum ChromosomeRenderMode {

    /** Рендерить только последнее поколение **/
    LAST_GEN,
    /** Рендерить последнее поколение и их родителей **/
    LAST_AND_PARENTS,
    /** Рендерить дерево родителей выбранной хромосомы **/
    TARGET_TREE,
    ;


    public String asText() {
        switch (this){
            case LAST_GEN:
                return "Last generation";
            case LAST_AND_PARENTS:
                return "Last generation + parents";
            case TARGET_TREE:
                return "Chromosome tree";
        }
        return "null";
    }
}
