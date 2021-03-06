package ru.maklas.genetics.engine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.genetics.ChromosomeSystem;
import ru.maklas.genetics.engine.genetics.GenerationComponent;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.genetics.engine.rendering.Animation;
import ru.maklas.genetics.engine.rendering.AnimationComponent;
import ru.maklas.genetics.engine.rendering.TextureUnit;
import ru.maklas.genetics.engine.rendering.RenderComponent;

public class EngineUtils {

    /**
     * @param minFitness 0..1
     * @return 0..1
     */
    public static double getElitePointsPercent(Engine engine, double minFitness){
        ChromosomeSystem chromosomeSystem = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class);
        if (chromosomeSystem == null || chromosomeSystem.currentGeneration == null) return 0;
        Array<Entity> currGenChromosomes = chromosomeSystem.currentGeneration.get(M.generation).chromosomes;
        if (currGenChromosomes.size == 0) return 0;

        int good = 0;
        for (Entity chromosome : currGenChromosomes) {
            double fitness = chromosome.get(M.chromosome).fitness;
            if (fitness >= minFitness){
                good++;
            }
        }
        return ((double) good) / currGenChromosomes.size;
    }

    /** Adds new RenderUnit with animation attached **/
    public static Animation addAnimation(Entity e, TextureRegion[] regions, float cycleTime) {
        return addAnimation(e, null, 0, 0, regions, cycleTime);
    }

    /** Adds new RenderUnit with animation attached **/
    public static Animation addAnimation(Entity e, String name, float localX, float localY, TextureRegion[] regions, float cycleTime){
        RenderComponent rc = e.get(M.render);
        AnimationComponent ac = e.get(M.anim);
        if (rc == null){
            rc = new RenderComponent();
            e.add(rc);
        }
        if (ac == null){
            ac = new AnimationComponent();
            e.add(ac);
        }
        TextureUnit ru = new TextureUnit(regions[0])
                .pos(localX, localY);
        ru.name = name;
        rc.add(ru);
        Animation animation = new Animation(regions, ru, cycleTime);
        ac.add(animation);
        return animation;
    }

    public static Entity getGeneration(Engine engine, int generation) {
        ImmutableArray<Entity> generations = engine.entitiesFor(GenerationComponent.class);
        for (Entity entity : generations) {
            if (entity.get(M.generation).generation == generation){
                return entity;
            }
        }
        return null;
    }

    public boolean batchSmartBegin(Batch batch){
        boolean wasDrawingBefore = batch.isDrawing();
        if (!wasDrawingBefore){
            batch.begin();
        }
        return wasDrawingBefore;
    }


    public void batchSmartEnd(Batch batch, boolean smartBegin){
       if (!batch.isDrawing() || smartBegin) return;
        batch.end();
    }



    /** Stops drawing if this batch was drawing. Continue drawing with {@link #batchSmartContinue(Batch, boolean)} **/
    public boolean batchSmartInterrupt(Batch batch){
        if (batch.isDrawing()){
            batch.end();
            return true;
        }
        return false;
    }

    public void batchSmartContinue(Batch batch, boolean smartInterrupt){
        if (smartInterrupt){ //was interrupted
            batch.begin();
        }
    }

    /** Starts drawing with this batch if it wasn't drawing. **/
    public void batchSafeStart(SpriteBatch batch){
        if (!batch.isDrawing()){
            batch.begin();
        }
    }


    /** Stops drawing with this batch if it was drawing. **/
    public void batchSafeEnd(SpriteBatch batch){
        if (batch.isDrawing()){
            batch.end();
        }
    }

    /**
     * Wave that tries to be smooth as square wave by combining 2 sinusoid waves (fourier)
     * @param x x coordinate
     * @param amp difference between lowest and highest point
     * @param periodicTime distance between 2 peaks
     * @param centerY center of the sinusoid
     */
    public static float smoothSquareWave(float x, float amp, float periodicTime, float centerY) {
        amp *= 1.682935038f;
        periodicTime /= 2;

        float first = (1 / MathUtils.PI) * MathUtils.sin((MathUtils.PI * x)/(periodicTime));
        float second = (1 / (15 * MathUtils.PI)) * MathUtils.sin((3 * MathUtils.PI * x)/(periodicTime));
        return (first + second) * amp + centerY;
    }

    /**
     * @param a amplitude
     * @param f frequency
     * @param b x position adjustment. [-a ... a] for [-halfCycle ... halfCycle]
     * @param h y position adjustment
     */
    public static double triangularWave(double x, double a, double f, double b, double h){
        return Math.abs(mod((x * f) + b, a * 2) - a) + h;
    }

    private static double mod(double a, double b){
        double remainder = a % b;
        return remainder > 0 ? remainder : remainder + b;
    }

    public static Array<Entity> getEntitiesOfType(Engine engine, int entityType){
        return engine.getEntities().cpyArray().filter(e -> e.type == entityType);
    }
}
