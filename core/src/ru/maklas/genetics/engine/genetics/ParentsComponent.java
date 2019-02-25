package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;

/** stores parents of the Chromosome which are Chromosomes themselves from previous generations **/
public class ParentsComponent implements Component {

    public Array<Entity> parents = new Array<>();

    public ParentsComponent add(Entity e){
        this.parents.add(e);
        return this;
    }

    public ParentsComponent(Entity... parents) {
        this.parents.addAll(parents);
    }

}
