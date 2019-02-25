package ru.maklas.genetics.engine.genetics;

public enum ChromosomeRenderMode {

    /** Рендерить только последнее поколение **/
    LAST_GEN,
    /** Рендерить последнее поколение и их родителей **/
    LAST_AND_PARENTS,
    /** Рендерить дерево родителей выбранной хромосомы **/
    TARGET_TREE,


}
