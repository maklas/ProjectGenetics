package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.functions.FunctionFromPoints;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class HistoryRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private ImmutableArray<Entity> functions;
    private OrthographicCamera cam;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
        functions = entitiesFor(FunctionComponent.class);
    }

    @Override
    public void render() {
        sr.setColor(Color.SKY);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity function : functions) {
            if (function.get(M.fun).graphFunction instanceof FunctionFromPoints) {
                FunctionFromPoints fun = (FunctionFromPoints) function.get(M.fun).graphFunction;
                int from = Math.max(0, ((int) Utils.camLeftX(cam)));
                int to = Math.min(fun.size(), (MathUtils.ceil(Utils.camRightX(cam))));
                if (to > from) {
                    for (int i = from; i < to; i++) {
                        sr.circle(i, ((float) fun.f(i)), 3 * cam.zoom, 12);
                    }
                }
            }
        }
        sr.end();
    }
}
