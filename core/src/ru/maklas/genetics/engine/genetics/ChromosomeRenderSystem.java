package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class ChromosomeRenderSystem extends RenderEntitySystem {

    private static Color chromosomeColor = Color.GREEN;
    private static Color chromosomeBorderColor = Color.BLACK;
    private static Color parentColor = Color.BROWN;
    private static Color parentBorderColor = Color.BLACK;
    private static Color arrowColor = Color.RED;

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ChromosomeSystem chromosomeSystem;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        chromosomeSystem = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class);
        if (chromosomeSystem == null) throw new RuntimeException("ChromosomeRenderSystem requires ChromosomeSystem");
        sr = engine.getBundler().getAssert(B.sr);
        cam = engine.getBundler().getAssert(B.cam);
    }

    @Override
    public void render() {
        sr.setProjectionMatrix(cam.combined);
        sr.begin();
        if (chromosomeSystem.selectedChromosome != null){
            renderSelectedChromosome(sr, chromosomeSystem.selectedChromosome);
        } else if (chromosomeSystem.currentGeneration != null){
            renderGeneration(sr, chromosomeSystem.currentGeneration);
        }
        sr.end();
    }

    private void renderSelectedChromosome(ShapeRenderer sr, Entity chromosome) {
        ParentsComponent pc = chromosome.get(M.parents);
        if (pc != null && pc.parents.size > 0){
            sr.setColor(arrowColor);
            for (Entity parent : pc.parents) {
                drawArrow(sr, parent, chromosome, 5);
            }
            for (Entity parent : pc.parents) {
                drawParentChromosome(sr, parent);
            }
        }
        drawChromosome(sr, chromosome);
    }

    private void renderGeneration(ShapeRenderer sr, Entity generation) {
        GenerationComponent gc = generation.get(M.generation);
        for (Entity chromosome : gc.chromosomes) {
            drawChromosome(sr, chromosome);
        }
    }

    private void drawParentChromosome(ShapeRenderer sr, Entity e){
        sr.set(ShapeRenderer.ShapeType.Filled);
        sr.setColor(parentColor);
        sr.circle(e.x, e.y, 9 * cam.zoom, 6);
        sr.setColor(parentBorderColor);
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.circle(e.x, e.y, 9 * cam.zoom, 6);
    }
    private void drawChromosome(ShapeRenderer sr, Entity e){
        sr.set(ShapeRenderer.ShapeType.Filled);
        sr.setColor(chromosomeColor);
        sr.circle(e.x, e.y, 5 * cam.zoom, 6);
        sr.setColor(chromosomeBorderColor);
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.circle(e.x, e.y, 5 * cam.zoom, 6);
    }

    private void drawArrow(ShapeRenderer sr, Entity source, Entity target, float endpointAdjustment){
        endpointAdjustment *= cam.zoom;
        Vector2 from = Utils.vec1.set(source.x, source.y);
        Vector2 to = Utils.vec2.set(target.x, target.y);
        Vector2 dir = new Vector2(to).sub(from);
        if (to.dst2(from) > endpointAdjustment * endpointAdjustment){
            dir.setLength(dir.len() - endpointAdjustment);
            to.set(from.x + dir.x, from.y + dir.y);
        }
        //Main line
        sr.line(from, to);

        //wings, only if they are not very close to each other
        if (from.dst2(to) > (15 * 15) * cam.zoom) {
            Vector2 wing = dir.setLength(15 * cam.zoom).rotate(25);
            sr.line(to.x, to.y, to.x - wing.x, to.y - wing.y);
            wing.rotate(-50);
            sr.line(to.x, to.y, to.x - wing.x, to.y - wing.y);
        }
    }
}
