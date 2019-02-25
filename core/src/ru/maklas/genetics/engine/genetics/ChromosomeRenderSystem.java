package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
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

    public boolean renderParents = true;
    public boolean renderArrows = true;
    public int currentGeneration = 0;
    private ImmutableArray<Entity> parentE;
    private ImmutableArray<Entity> genE;
    private BitmapFont font;


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().getAssert(B.sr);
        cam = engine.getBundler().getAssert(B.cam);
        batch = engine.getBundler().getAssert(B.batch);
        parentE = entitiesFor(ParentsComponent.class);
        genE = entitiesFor(GenerationComponent.class);
        font = new BitmapFont();
        font.setUseIntegerPositions(false);

    }

    @Override
    public void render() {
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        if (renderArrows){
            sr.setColor(arrowColor);
            for (Entity entity : parentE) {
                if (entity.get(M.generation).generation == currentGeneration) {
                    ParentsComponent pc = entity.get(M.parents);
                    for (Entity parent : pc.parents) {
                        drawArrow(sr, parent, entity);
                    }
                }
            }
        }

        if (renderParents && currentGeneration > 0){
            sr.setColor(parentColor);
            sr.set(ShapeRenderer.ShapeType.Filled);
            final int generationToRender = currentGeneration - 1;
            for (Entity entity : genE) {
                if (entity.get(M.generation).generation == generationToRender){
                    drawParentChromosome(sr, entity);
                }
            }
        }

        sr.setColor(currentGenerationColor);
        for (Entity entity : genE) {
            if (entity.get(M.generation).generation == currentGeneration){
                drawChromosome(sr, entity);
            }
        }

        sr.end();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        printChromosomes();
        batch.end();
    }

    private void printChromosomes() {
        float scale = 1f * cam.zoom;

        float x = Utils.camLeftX(cam) + (5 * cam.zoom);
        float y = Utils.camTopY(cam) - (5 * cam.zoom);
        float dy = 16 * scale;

        font.getData().setScale(scale);

        engine.entitiesFor();

        font.draw(batch, "id: "  + e.id + ", type: " + EntityType.typeToString(e.type) + ", x: " + ff(e.x) + ", y: " + ff(e.y) + ", ang: " + ff(e.getAngle()), x, y, 10, Align.left, false);
        for (Component c : components) {
            y -= dy;
            font.draw(batch, StringUtils.componentToString(c), x, y,10, Align.left, false);
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
