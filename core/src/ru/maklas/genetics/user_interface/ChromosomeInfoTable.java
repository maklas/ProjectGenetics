package ru.maklas.genetics.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import org.jetbrains.annotations.Nullable;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.EngineUtils;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.ChromosomeComponent;
import ru.maklas.genetics.tests.Gene;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class ChromosomeInfoTable extends VisTable {

    public ChromosomeInfoTable() {
        setBackground(new TextureRegionDrawable(A.images.whiteBox10pxHalfAlpha));
        setColor(Color.LIGHT_GRAY);
        defaults().top().left().pad(1f);
    }

    public void set(@Nullable Entity entity){
        clearChildren();
        if (entity == null) {
            return;
        }

        ChromosomeComponent cc = entity.get(M.chromosome);

        label("Id: " + entity.id);
        label("Gen: " + cc.generation);
        label("Pos: " + StringUtils.ff(entity.x, 2) + ", " + StringUtils.ff(entity.y, 2));
        label("Genes");
        for (Gene gene : cc.chromosome.getGenes()) {
            label("   " + gene.getName() + ": " + gene.getRawData().toStringSmart(4));
        }
        if (entity.isInEngine()) {
            Engine engine = entity.getEngine();
            Entity generation = EngineUtils.getGeneration(engine, cc.generation);
            double fitness = engine.getBundler().get(B.params).getFitnessFunction().calculateFitness(engine, entity, generation.get(M.generation).chromosomes);

            label("Fitness: " + StringUtils.df(fitness, 3));
        }
    }

    private void label(String text){
        VisLabel label = new VisLabel(text);
        label.setColor(Color.BLACK);
        add(label);
        row();
    }
}
