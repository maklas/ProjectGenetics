package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.tests.GeneNames;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;

public class ChromosomeSystem extends EntitySystem {

    private ImmutableArray<Entity> chromosomes;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        chromosomes = entitiesFor(ChromosomeComponent.class);
    }

    @Override
    public void update(float dt) {
        for (Entity chromosome : chromosomes) {
            ChromosomeComponent cc = chromosome.get(M.chromosome);
            if (cc.dirty){
                cc.dirty = false;
                chromosome.x = ((float) cc.chromosome.get(GeneNames.X).decodeAsDouble());
                chromosome.y = ((float) cc.chromosome.get(GeneNames.Y).decodeAsDouble());
            }
        }

    }
}
