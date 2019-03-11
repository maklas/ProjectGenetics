package ru.maklas.genetics.engine.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.assets.ImageAssets;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.formulas.BiFunctionComponent;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

import static java.lang.Math.*;

public class BiFunctionRenderSystem extends RenderEntitySystem {

    private OrthographicCamera cam;
    private ShapeRenderer sr;
    private Batch batch;
    private ImmutableArray<Entity> functions;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        cam = engine.getBundler().get(B.cam);
        sr = engine.getBundler().get(B.sr);
        batch = engine.getBundler().get(B.batch);
        functions = entitiesFor(BiFunctionComponent.class);
    }

    @Override
    public void render() {
        renderWithBatch();
        //renderWithSr();
    }

    private void renderWithBatch(){
        Batch batch = this.batch;
        TextureRegion img = A.images.pixel;
        batch.begin();

        double rightX = Utils.camRightX(cam);
        double leftX = Utils.camLeftX(cam);
        double botY = Utils.camBotY(cam);
        double topY = Utils.camTopY(cam);
        double step = cam.zoom;
        float scale = cam.zoom;


        for (Entity function : functions) {
            BiFunctionComponent bfc = function.get(M.biFun);
            batch.setColor(bfc.color);
            double resolutionMultiplier = bfc.resolutionMultiplier;
            double thickness = bfc.thickness;
            float imageScale = (float) (scale * thickness);
            double valMinMax = step * resolutionMultiplier * thickness;

            for (double x = leftX; x < rightX; x += step * resolutionMultiplier) {
                for (double y = botY; y < topY; y += step * resolutionMultiplier) {
                    double val = bfc.fun.f(x, y);
                    if (val < valMinMax && val > -valMinMax){
                        ImageAssets.draw(batch, img, (float) x, (float) y, 0.5f, 0.5f, imageScale, imageScale, 0);
                    }
                }
            }
        }

        batch.end();
    }

    private void renderWithSr(){
        sr.begin(ShapeRenderer.ShapeType.Line);

        double rightX = Utils.camRightX(cam);
        double leftX = Utils.camLeftX(cam);
        double botY = Utils.camBotY(cam);
        double topY = Utils.camTopY(cam);
        double step = cam.zoom;
        float scale = cam.zoom;


        for (Entity function : functions) {
            BiFunctionComponent bfc = function.get(M.biFun);
            sr.setColor(bfc.color);
            double resolution = bfc.resolutionMultiplier;
            double thickness = bfc.thickness;
            double valMinMax = step * thickness * resolution;

            for (double x = leftX; x < rightX; x += step * resolution) {
                for (double y = botY; y < topY; y += step * resolution) {
                    double val = bfc.fun.f(x, y);
                    if (val < valMinMax && val > -valMinMax){
                        sr.line((float) x, ((float) y), (float) x + scale * 2, ((float) y + scale * 2));
                        y += step * resolution * 2;
                    }
                }
            }
        }

        sr.end();
    }


    private static boolean plot(double x, double y, double precision){
        double left = sin(sin(x) + cos(y));
        double right = cos(sin(x * y) + cos(x));
        double v = left - right;
        return v > -precision && v < precision;
    }

    public static boolean test(double x, double y, double precision){
        return Math.abs((0.2 * pow(x - 70, 2)) + 0.8 * pow(y - 20, 2)) < precision;
    }


    public static boolean batmanEquation(double x, double y){
        if (pow(x, 2.0) / 49.0 + pow(y, 2.0) / 9.0 - 1.0 <= 0 && abs(x) >= 4.0 && -(3.0 * sqrt(33.0)) / 7.0 <= y && y <= 0) {
            return true;
        }
        if (pow(x, 2.0) / 49.0 + pow(y, 2.0) / 9.0 - 1.0 <= 0 && abs(x) >= 3.0 && -(3.0 * sqrt(33.0)) / 7.0 <= y && y >= 0) {
            return true;
        }
        if (-3.0 <= y
                && y <= 0
                && -4.0 <= x
                && x <= 4.0
                && (abs(x)) / 2.0 + sqrt(1.0 - pow(abs(abs(x) - 2.0) - 1.0, 2.0)) - 1.0 / 112.0 * (3.0 * sqrt(33.0) - 7.0)
                * pow(x, 2.0) - y - 3.0 <= 0) {
            return true;
        }

        if (y >= 0 && 3.0 / 4.0 <= abs(x) && abs(x) <= 1.0 && -8.0 * abs(x) - y + 9.0 >= 0) {
            return true;
        }

        if (1.0 / 2.0 <= abs(x) && abs(x) <= 3.0 / 4.0 && 3.0 * abs(x) - y + 3.0 / 4.0 >= 0 && y >= 0) {
            return true;
        }

        if (abs(x) <= 1.0 / 2.0 && y >= 0 && 9.0 / 4.0 - y >= 0) {
            return true;
        }

        if (abs(x) >= 1.0
                && y >= 0
                && -(abs(x)) / 2.0 - 3.0 / 7.0 * sqrt(10.0) * sqrt(4.0 - pow(abs(x) - 1.0, 2.0)) - y + (6.0 * sqrt(10.0)) / 7.0
                + 3.0 / 2.0 >= 0) {
            return true;
        }
        return false;
    }
}