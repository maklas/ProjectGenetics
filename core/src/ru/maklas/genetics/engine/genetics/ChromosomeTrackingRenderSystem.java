package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class ChromosomeTrackingRenderSystem extends RenderEntitySystem {

    public ChromosomeTrackMode trackMode = ChromosomeTrackMode.SELECTED;
    private ChromosomeSystem chromosomeSystem;
    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private Color lineColor = Color.BLUE;
    private Color bestLineColor = Color.CORAL;


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        chromosomeSystem = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class);
        if (chromosomeSystem == null) throw new RuntimeException("Chromosome system is required");
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
    }

    @Override
    public void render() {
        Gdx.gl.glLineWidth(2f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Line);

        sr.setColor(lineColor);
        if (trackMode == ChromosomeTrackMode.CURRENT_GEN && chromosomeSystem.selectedChromosome == null){
            float left = Utils.camLeftX(cam);
            float right = Utils.camRightX(cam);
            for (Entity chromosome : chromosomeSystem.currentGeneration.get(M.generation).chromosomes) {
                if (chromosome.x > left && chromosome.x < right) {
                    draw(sr, chromosome);
                }
            }
            if (chromosomeSystem.bestChromosomeOfGeneration != null) {
                sr.setColor(bestLineColor);
                drawWithCircle(sr, chromosomeSystem.bestChromosomeOfGeneration);
            }
        } else if ((trackMode == ChromosomeTrackMode.SELECTED || trackMode == ChromosomeTrackMode.CURRENT_GEN) && chromosomeSystem.selectedChromosome != null){
            if (chromosomeSystem.selectedChromosome == chromosomeSystem.bestChromosomeOfGeneration){
                sr.setColor(bestLineColor);
            }
            drawWithCircle(sr, chromosomeSystem.selectedChromosome);
        }

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glLineWidth(1f);
    }

    private void draw(ShapeRenderer sr, Entity chromosome) {
        Vector2 start = new Vector2(chromosome.x, chromosome.y);
        Vector2 end = new Vector2(chromosome.x, ((float) chromosome.get(M.chromosome).functionValue));
        sr.line(start, end);
    }

    private void drawWithCircle(ShapeRenderer sr, Entity chromosome) {
        Vector2 start = new Vector2(chromosome.x, chromosome.y);
        Vector2 end = new Vector2(chromosome.x, ((float) chromosome.get(M.chromosome).functionValue));
        sr.line(start, end);
        sr.circle(end.x, end.y, 3 * cam.zoom, 12);
    }

    public ChromosomeTrackingRenderSystem setMode(ChromosomeTrackMode mode) {
        this.trackMode = mode;
        return this;
    }
}
