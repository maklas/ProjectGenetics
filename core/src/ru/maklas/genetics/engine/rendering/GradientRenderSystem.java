package ru.maklas.genetics.engine.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.functions.bi_functions.GraphBiFunction;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.libs.Timer;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.RenderEntitySystem;

public class GradientRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private Params params;
    private OrthographicCamera cam;
    private double max = 1_000_000;
    private double min = Double.MAX_VALUE;

    @Override
    public void onAddedToEngine(Engine engine) {
        sr = engine.getBundler().get(B.sr);
        params = engine.getBundler().get(B.params);
        cam = engine.getBundler().get(B.cam);
    }

    @Override
    public void render() {
        if (!Gdx.input.isKeyPressed(Input.Keys.G)){
            return;
        }
        float leftX = Utils.camLeftX(cam);
        float rightX = Utils.camRightX(cam);
        float botY = Utils.camBotY(cam);
        float topY = Utils.camTopY(cam);
        float step = cam.zoom;
        ShapeRenderer sr = this.sr;
        double min = this.min;
        double max = this.max;
        double newMin =  0x1.fffffffffffffP+1000;
        double newMax = 0;

        GraphBiFunction f1 = params.getBiFunction1();
        GraphBiFunction f2 = params.getBiFunction2();

        sr.begin(ShapeRenderer.ShapeType.Point);
        {
            float x = leftX;
            while (x < rightX){
                float y = botY;
                while (y < topY) {
                    double v1 = f1.f(x, y);
                    double v2 = f2.f(x, y);
                    if (v1 == 0 || v2 == 0){
                        y += step;
                        continue;
                    }
                    //double val = v1 + v2; double power = 2; //Показывает точку черещ которую пройдут хромосомы
                    double val = Math.max(v1, v2); double power = 0.1; //Показывает кривую которую пересекут хромосомы
                    //double val = (v1 * v2); double power = 0.1;
                    if (val > newMax){
                        newMax = val;
                    }
                    if (val < newMin){
                        newMin = val;
                    }
                    double intensity = ((val - min) / (max - min));
                    intensity = intensity > 1 ? 1 : Math.pow(intensity, power);
                    intensity = 1 - intensity;
                    sr.setColor((float) intensity, (float) intensity, (float) intensity, 1);
                    sr.point(x, y, 0);
                    y += step;
                }
                x += step;
            }
        }
        sr.end();
        this.max = newMax;
        this.min = newMin;
    }
}
