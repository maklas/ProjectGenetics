package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class ChromosomeTrackingRenderSystem extends RenderEntitySystem {

    public ChromosomeTrackMode trackMode = ChromosomeTrackMode.SELECTED;
    private ChromosomeSystem chromosomeSystem;
    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ImmutableArray<Entity> functions;


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        chromosomeSystem = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class);
        if (chromosomeSystem == null) throw new RuntimeException("Chromosome system is required");
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
        functions = engine.entitiesFor(FunctionComponent.class);
    }

    @Override
    public void render() {
        Gdx.gl.glLineWidth(2f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Line);

        for (Entity function : functions) {
            FunctionComponent fc = function.get(M.fun);
            sr.setColor(Color.BLUE);
            if (trackMode == ChromosomeTrackMode.CURRENT_GEN && chromosomeSystem.selectedChromosome == null){
                for (Entity chromosome : chromosomeSystem.currentGeneration.get(M.generation).chromosomes) {
                    draw(sr, chromosome, fc.graphFunction);
                }
            } else if ((trackMode == ChromosomeTrackMode.SELECTED || trackMode == ChromosomeTrackMode.CURRENT_GEN) && chromosomeSystem.selectedChromosome != null){
                draw(sr, chromosomeSystem.selectedChromosome, fc.graphFunction);
            }
        }

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glLineWidth(1f);
    }

    private void draw(ShapeRenderer sr, Entity chromosome, GraphFunction graphFunction) {
        Vector2 start = new Vector2(chromosome.x, chromosome.y);
        Vector2 end = new Vector2(chromosome.x, (float) graphFunction.f(chromosome.x));
        sr.line(start, end);
        sr.circle(end.x, end.y, 3 * cam.zoom, 12);
    }

    public ChromosomeTrackingRenderSystem setMode(ChromosomeTrackMode mode) {
        this.trackMode = mode;
        return this;
    }
}
