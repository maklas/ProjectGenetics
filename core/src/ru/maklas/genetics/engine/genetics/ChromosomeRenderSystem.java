package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.dispatchable.GenerationChangedEvent;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class ChromosomeRenderSystem extends RenderEntitySystem {

    private static Color currentGenerationColor = Color.GREEN;
    private static Color parentColor = Color.BROWN;
    private static Color arrowColor = Color.WHITE;

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private Batch batch;

    public boolean renderArrows = true;
    public ChromosomeRenderMode renderMode = ChromosomeRenderMode.LAST_AND_PARENTS;
    public int targetChromosomeId;

    private BitmapFont font;
    private Entity currentGeneration;
    public final Array<Entity> chromosomesUnderMouse = new Array<>();
    private int generationNumber;


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().getAssert(B.sr);
        cam = engine.getBundler().getAssert(B.cam);
        batch = engine.getBundler().getAssert(B.batch);
        font = A.images.font;

        subscribe(GenerationChangedEvent.class, this::onGenerationChanged);

    }

    private void onGenerationChanged(GenerationChangedEvent e) {
        currentGeneration = e.getGeneration();
        generationNumber = e.getGenerationNumber();
    }

    public ChromosomeRenderSystem renderModeLast(){
        renderMode = ChromosomeRenderMode.LAST_GEN;
        return this;
    }

    public ChromosomeRenderSystem renderModeLastAndParents(){
        renderMode = ChromosomeRenderMode.LAST_AND_PARENTS;
        return this;
    }

    public ChromosomeRenderSystem renderModeTree(){
        return renderModeTree(targetChromosomeId);
    }

    public ChromosomeRenderSystem renderModeTree(int targetId){
        renderMode = ChromosomeRenderMode.TARGET_TREE;
        targetChromosomeId = targetId;
        return this;
    }

    @Override
    public void render() {
        chromosomesUnderMouse.clear();
        getChromosomesUnderMouse(chromosomesUnderMouse);


        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        switch (renderMode){
            case LAST_GEN:
                renderOnlyLastGen(sr);
                break;
            case LAST_AND_PARENTS:
                renderLastGenAndParents(sr);
                break;
            case TARGET_TREE:
                Entity targetChromosome = engine.findById(targetChromosomeId);
                if (targetChromosome != null){
                    renderTargetChromosomeTree(sr, targetChromosome);
                } else {
                    renderMode = ChromosomeRenderMode.LAST_AND_PARENTS;
                }
                break;
        }


        sr.end();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        printChromosomes();
        batch.end();
    }

    private void renderOnlyLastGen(ShapeRenderer sr) {
        if (currentGeneration == null){
            return;
        }

        sr.setColor(currentGenerationColor);
        sr.set(ShapeRenderer.ShapeType.Filled);
        for (Entity chromosome : currentGeneration.get(M.generation).chromosomes) {
            drawChromosome(sr, chromosome);
        }
    }

    private void renderLastGenAndParents(ShapeRenderer sr) {
        if (currentGeneration == null){
            return;
        }

        GenerationComponent gc = currentGeneration.get(M.generation);

        if (renderArrows){
            sr.setColor(arrowColor);
            sr.set(ShapeRenderer.ShapeType.Line);

            for (Entity chromosome : gc.chromosomes) {
                ParentsComponent pc = chromosome.get(M.parents);
                for (Entity parent : pc.parents) {
                    drawArrow(sr, parent, chromosome);
                }
            }
        }

        sr.set(ShapeRenderer.ShapeType.Filled);

        if (gc.previousGeneration != null){
            sr.setColor(parentColor);
            for (Entity chromosome : gc.previousGeneration.get(M.generation).chromosomes) {
                drawParentChromosome(sr, chromosome);
            }
        }

        sr.setColor(currentGenerationColor);
        for (Entity chromosome : gc.chromosomes) {
            drawChromosome(sr, chromosome);
        }
    }

    private void renderTargetChromosomeTree(ShapeRenderer sr, Entity targetChromosome) {
        Array<Entity> entities = new Array<>();
        getTree(targetChromosome, entities, 4);


        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(arrowColor);
        for (Entity entity : entities) {
            for (Entity parent : entity.get(M.parents).parents) {
                drawArrow(sr, parent, entity);
            }
        }

        entities.clear();
        getTree(targetChromosome, entities);
        entities.removeValue(targetChromosome, true);

        sr.setColor(parentColor);
        sr.set(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : entities) {
            drawParentChromosome(sr, entity);
        }

        sr.setColor(currentGenerationColor);
        sr.set(ShapeRenderer.ShapeType.Line);
        drawChromosome(sr, targetChromosome);
    }

    private void printChromosomes() {
        float scale = 1f * cam.zoom;

        float x = Utils.camLeftX(cam) + (5 * cam.zoom);
        float y = Utils.camTopY(cam) - (5 * cam.zoom);
        float dy = 16 * scale;

        font.getData().setScale(scale);
        font.setColor(Color.WHITE);

        font.draw(batch, "Generation N: " + generationNumber, x, y, 10, Align.left, false);
        y -= dy;
        String viewMode = "";
        switch (this.renderMode){
            case LAST_GEN:
                viewMode = "Last Generation";
                break;
            case LAST_AND_PARENTS:
                viewMode = "Last Generation and Parents";
                break;
            case TARGET_TREE:
                Entity target = engine.findById(targetChromosomeId);
                viewMode = "Target tree (id=" + targetChromosomeId + ", " + "gen=" + (target == null ? "null" : target.get(M.chromosome).generation) + ")";
                break;
        }
        font.draw(batch, "View mode: " + viewMode, x, y, 10, Align.left, false);
        for (Entity c : chromosomesUnderMouse) {
            y -= dy;
            ChromosomeComponent cc = c.get(M.chromosome);
            font.draw(batch, "{id=" + c.id + ", gen=" + cc.generation + ", pos=(" + StringUtils.ff(c.x, 2) + ", " + StringUtils.ff(c.y, 2) + "), " + cc.chromosome.byteCode() + "}", x, y,10, Align.left, false);
        }
    }

    private Array<Entity> getChromosomesUnderMouse(Array<Entity> entities){
        final Vector2 mouse = Utils.getMouse(cam);
        final float range = 10 * cam.zoom;
        final float range2 = range * range;

        if (currentGeneration == null) return entities;
        GenerationComponent gc = currentGeneration.get(M.generation);

        switch (renderMode){
            case LAST_GEN:
                for (Entity chromosome : gc.chromosomes) {
                    if (mouse.dst2(chromosome.x, chromosome.y) < range2){
                        entities.add(chromosome);
                    }
                }
                break;
            case LAST_AND_PARENTS:

                for (Entity chromosome : gc.chromosomes) {
                    if (mouse.dst2(chromosome.x, chromosome.y) < range2){
                        entities.add(chromosome);
                    }
                }

                if (gc.previousGeneration != null){
                    for (Entity chromosome : gc.previousGeneration.get(M.generation).chromosomes) {
                        if (mouse.dst2(chromosome.x, chromosome.y) < range2){
                            entities.add(chromosome);
                        }
                    }
                }

                break;
            case TARGET_TREE:
                Entity targetChromosome = engine.findById(targetChromosomeId);
                if (targetChromosome == null) return entities;

                getTree(targetChromosome, entities);
                entities.filter(e -> mouse.dst2(e.x, e.y) < range2);
                break;
        }

        return entities;
    }

    private void getTree(Entity chromosome, Array<Entity> entities) {
        getTree(chromosome, entities, 5);
    }
    private void getTree(Entity chromosome, Array<Entity> entities, int depth) {
        if (depth-- == 0) return;
        entities.add(chromosome);
        Array<Entity> parents = chromosome.get(M.parents).parents;
        for (Entity parent : parents) {
            getTree(parent, entities, depth);
        }
    }

    private void drawParentChromosome(ShapeRenderer sr, Entity e){
        sr.circle(e.x, e.y, 9 * cam.zoom, 6);
    }
    private void drawChromosome(ShapeRenderer sr, Entity e){
        sr.circle(e.x, e.y, 5 * cam.zoom, 6);
    }

    private void drawArrow(ShapeRenderer sr, Entity source, Entity target){
        sr.line(source.x, source.y, target.x, target.y);
        Vector2 dir = Utils.vec1.set(target.x - source.x, target.y - source.y).setLength(15 * cam.zoom).rotate(25);
        sr.line(target.x, target.y, target.x - dir.x, target.y - dir.y);
        dir.rotate(-50);
        sr.line(target.x, target.y, target.x - dir.x, target.y - dir.y);
    }
}
