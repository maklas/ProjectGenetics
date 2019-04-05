package ru.maklas.genetics.engine.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.functions.FunctionUtils;
import ru.maklas.genetics.functions.bi_functions.GraphBiFunction;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.RenderEntitySystem;

public class ParetoMinimalRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private Array<Vector2> paretoMinimal = new Array<>();
    private Params params;
    private boolean inProgress;
    private float progress = 0;
    private GraphBiFunction f1;
    private GraphBiFunction f2;
    private BitmapFont font;
    private Batch batch;
    private OrthographicCamera cam;

    @Override
    public void onAddedToEngine(Engine engine) {
        cam = engine.getBundler().get(B.cam);
        sr = engine.getBundler().get(B.sr);
        batch = engine.getBundler().get(B.batch);
        params = engine.getBundler().get(B.params);
        font = A.images.font;

    }

    @Override
    public void render() {
        check();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GREEN);

        for (int i = 1; i < paretoMinimal.size; i++) {
            Vector2 a = paretoMinimal.get(i - 1);
            Vector2 b = paretoMinimal.get(i);
            sr.line(a, b);
        }

        sr.end();
        if (inProgress){
            batch.begin();
            font.setColor(Color.PINK);
            float x = Utils.camLeftX(cam) + 2 * cam.zoom;
            float y = Utils.camTopY(cam) - 15 * cam.zoom;
            font.draw(batch, String.valueOf(((int) (progress * 100))), x, y);
            batch.end();
        }
    }

    private void check() {
        if (inProgress) return;

        if ((params.getBiFunction1() != null && params.getBiFunction2() != null) && (params.getBiFunction1() != f1 || params.getBiFunction2() != f2)){
            paretoMinimal = new Array<>();
            startUpdate();
        }
    }


    private void startUpdate(){
        inProgress = true;
        progress = 0;
        GraphBiFunction bf1 = params.getBiFunction1();
        GraphBiFunction bf2 = params.getBiFunction2();

        new Thread(() -> {
            Array<Vector2> paretoMinimal = null;
            try {
                paretoMinimal = FunctionUtils.findParetoMinimal(
                        bf1, bf2,
                        params.getMinValue(), params.getMaxValue(),
                        params.getMinValue(), params.getMaxValue(),
                        (params.getMaxValue() - params.getMinValue()) / 300d, (p) -> {
                            if (bf1 != params.getBiFunction1()){
                                throw new RuntimeException();
                            } else {
                                Gdx.app.postRunnable(() -> this.progress = p);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                inProgress = false;
                this.paretoMinimal = new Array<>();
                return;
            }
            Array<Vector2> finalParetoMinimal = paretoMinimal;
            Gdx.app.postRunnable(() -> {
                inProgress = false;
                if (params.getBiFunction1() == bf1 && params.getBiFunction2() == bf2) {
                    this.paretoMinimal = finalParetoMinimal;
                    f1 = params.getBiFunction1();
                    f2 = params.getBiFunction2();
                }
            });
        }).start();
    }
}
