package ru.maklas.genetics.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import org.jetbrains.annotations.Nullable;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.ChromosomeComponent;
import ru.maklas.genetics.engine.genetics.Gene;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.mengine.Entity;

public class ChromosomeInfoTable extends VisTable {

    boolean printFitness = true;
    boolean printFunctionValue = true;

    public ChromosomeInfoTable() {
        setBackground(new TextureRegionDrawable(A.images.whiteBox10pxHalfAlpha));
        setColor(Color.LIGHT_GRAY);
        defaults().top().left().pad(1f);
    }

    public void set(@Nullable Entity entity){
        clear();
        if (entity == null) {
            return;
        }

        ChromosomeComponent cc = entity.get(M.chromosome);

        label("Id: " + entity.id);
        label("Gen: " + cc.generation);
        label("Pos: " + StringUtils.ffSigDigits(entity.x, 2, 3) + ", " + StringUtils.ffSigDigits(entity.y, 2, 3));
        label("Genes");
        for (Gene gene : cc.chromosome.getGenes()) {
            label("   " + gene.getName() + ": " + gene.getRawData().toStringSmart(4));
        }
        if (printFitness) label("Fitness: " + StringUtils.dfSigDigits(entity.get(M.chromosome).fitness, 2, 2));
        if (printFunctionValue) label("Function value: " + StringUtils.dfSigDigits(entity.get(M.chromosome).functionValue, 2, 2));
    }

    public void clear(){
        clearChildren();
    }

    public ChromosomeInfoTable setPrintFitness(boolean print) {
        this.printFitness = print;
        return this;
    }

    public ChromosomeInfoTable setPrintFunctionValue(boolean print) {
        this.printFunctionValue = print;
        return this;
    }


    private void label(String text){
        VisLabel label = new VisLabel(text);
        label.setColor(Color.BLACK);
        add(label);
        row();
    }
}
