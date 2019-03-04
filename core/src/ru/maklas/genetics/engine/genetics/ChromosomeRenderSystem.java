package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class ChromosomeRenderSystem extends RenderEntitySystem {

    private static Color bestChromosomeColor = Color.CORAL;
    private static Color chromosomeColor = Color.GREEN;
    private static Color parentColor = Color.BROWN;
    private static Color arrowColor = Color.RED;
    private static Color minMaxColor;

    private ShapeRenderer sr;
    private Batch batch;
    private OrthographicCamera cam;
    private ChromosomeSystem chromosomeSystem;
    private Params params;

    private boolean renderMinMax = true;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        chromosomeSystem = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class);
        if (chromosomeSystem == null) throw new RuntimeException("ChromosomeRenderSystem requires ChromosomeSystem");
        sr = engine.getBundler().getAssert(B.sr);
        cam = engine.getBundler().getAssert(B.cam);
        batch = engine.getBundler().getAssert(B.batch);
        params = engine.getBundler().getAssert(B.params);
        minMaxColor = Color.VIOLET;
        minMaxColor.a = 0.6f;
    }

    @Override
    public void render() {
        if (renderMinMax) {
            renderMinMax();
        }
        if (chromosomeSystem.selectedChromosome != null){
            renderSelectedChromosome(chromosomeSystem.selectedChromosome);
        } else if (chromosomeSystem.currentGeneration != null){
            renderGeneration(chromosomeSystem.currentGeneration);
        }

    }

    private void renderMinMax() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(minMaxColor);
        float leftX = Utils.camLeftX(cam);
        float rightX = Utils.camRightX(cam);
        float topY = Utils.camTopY(cam);
        float botY = Utils.camBotY(cam);

        if (params.getMinValue() > leftX && params.getMinValue() < rightX) {
            renderVerticalDotted(sr, params.getMinValue(), topY, botY);
        }
        if (params.getMaxValue() > leftX && params.getMaxValue() < rightX) {
            renderVerticalDotted(sr, params.getMaxValue(), topY, botY);
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderVerticalDotted(ShapeRenderer sr, double x, float topY, float botY) {
        sr.line(((float) x), botY, ((float) x), topY);
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

        batch.setColor(chromosomeSystem.bestChromosomeOfGeneration == chromosome ? bestChromosomeColor : chromosomeColor);
        drawChromosome(batch, chromosome);

        batch.end();
    }

    private void renderGeneration(Entity generation) {
        batch.setColor(chromosomeColor);
        batch.begin();

        float left = Utils.camLeftX(cam);
        float right = Utils.camRightX(cam);

        GenerationComponent gc = generation.get(M.generation);
        for (Entity chromosome : gc.chromosomes) {
            if (chromosome.x > left && chromosome.x < right) {
                drawChromosome(batch, chromosome);
            }
        }

        if (chromosomeSystem.bestChromosomeOfGeneration != null) {
            batch.setColor(bestChromosomeColor);
            drawChromosome(batch, chromosomeSystem.bestChromosomeOfGeneration);
        }

        batch.end();
    }

    public ChromosomeRenderSystem setRenderMinMax(boolean enabled){
        renderMinMax = enabled;
        return this;
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
