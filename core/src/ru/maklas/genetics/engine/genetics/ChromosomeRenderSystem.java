package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class ChromosomeRenderSystem extends RenderEntitySystem {

    private static Color chromosomeColor = Color.GREEN;
    private static Color parentColor = Color.BROWN;
    private static Color arrowColor = Color.RED;

    private ShapeRenderer sr;
    private Batch batch;
    private OrthographicCamera cam;
    private ChromosomeSystem chromosomeSystem;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        chromosomeSystem = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class);
        if (chromosomeSystem == null) throw new RuntimeException("ChromosomeRenderSystem requires ChromosomeSystem");
        sr = engine.getBundler().getAssert(B.sr);
        cam = engine.getBundler().getAssert(B.cam);
        batch = engine.getBundler().getAssert(B.batch);
    }

    @Override
    public void render() {
        if (chromosomeSystem.selectedChromosome != null){
            renderSelectedChromosome(chromosomeSystem.selectedChromosome);
        } else if (chromosomeSystem.currentGeneration != null){
            renderGeneration(chromosomeSystem.currentGeneration);
        }

    }

    private void renderSelectedChromosome(Entity chromosome) {
        ParentsComponent pc = chromosome.get(M.parents);
        if (pc != null && pc.parents.size > 0){
            Gdx.gl.glLineWidth(2f);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(arrowColor);
            for (Entity parent : pc.parents) {
                drawArrow(sr, parent, chromosome, 5);
            }
            sr.end();
            Gdx.gl.glLineWidth(1f);

            batch.begin();
            batch.setColor(parentColor);
            for (Entity parent : pc.parents) {
                drawParentChromosome(batch, parent);
            }
        }

        if (!batch.isDrawing()){
            batch.begin();
        }

        batch.setColor(chromosomeColor);
        drawChromosome(batch, chromosome);

        batch.end();
    }

    private void renderGeneration(Entity generation) {
        batch.setColor(chromosomeColor);
        batch.begin();


        GenerationComponent gc = generation.get(M.generation);
        for (Entity chromosome : gc.chromosomes) {
            drawChromosome(batch, chromosome);
        }

        batch.end();
    }



    private void drawParentChromosome(Batch batch, Entity e){
        TextureRegion img = A.images.circle;
        float zoom = cam.zoom;
        batch.draw(img, e.x - (img.getRegionWidth()/2f) * zoom, e.y - (img.getRegionHeight()/2f) * zoom, 0, 0, img.getRegionWidth() * zoom, img.getRegionHeight() * zoom, 1, 1, 0);
    }
    private void drawChromosome(Batch batch, Entity e){
        TextureRegion img = A.images.circle;
        float zoom = cam.zoom;
        batch.draw(img, e.x - (img.getRegionWidth()/4f) * zoom, e.y - (img.getRegionHeight()/4f) * zoom, 0, 0, (img.getRegionWidth() / 2f)* zoom, (img.getRegionHeight() / 2f) * zoom, 1, 1, 0);
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
        if (from.dst2(to) > (10 * 10) * cam.zoom) {
            Vector2 wing = dir.setLength(15 * cam.zoom).rotate(25);
            sr.line(to.x, to.y, to.x - wing.x, to.y - wing.y);
            wing.rotate(-50);
            sr.line(to.x, to.y, to.x - wing.x, to.y - wing.y);
        }
    }
}
