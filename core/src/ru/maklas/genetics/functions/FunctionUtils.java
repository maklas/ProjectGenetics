package ru.maklas.genetics.functions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Consumer;
import org.jetbrains.annotations.Nullable;
import ru.maklas.genetics.functions.bi_functions.GraphBiFunction;

public class FunctionUtils {

    /**
     * Возвращает точку X в диапозоне minX..maxX в которой фунция имеет минимальное значение.
     * Точность значения определяется шагом интеграции и количеством итерацией.
     */
    public static double findMinimalPoint(GraphFunction fun, double minX, double maxX, double step, int iterations){
        if (minX > maxX) throw new RuntimeException("minX > maxX");
        if (step <= 0) throw new RuntimeException("Step <= 0");
        if (iterations <= 0) throw new RuntimeException("Iterations >= 0");
        return _findMinimalPoint(fun, minX, maxX, step, iterations);
    }

    private static double _findMinimalPoint(GraphFunction fun, double minX, double maxX, double step, int iterations){
        double x = minX;
        double lowestX = x;
        double lowestY = fun.f(x);
        x += step;
        while (x <= maxX){
            double y = fun.f(x);
            if (y < lowestY){
                lowestY = y;
                lowestX = x;
            }
            x += step;
        }

        if (iterations == 1) return lowestX;
        return _findMinimalPoint(fun, Math.max(minX, lowestX - step), Math.min(maxX, lowestX + step), step / (maxX - minX), iterations - 1);
    }


    private static final Array<Color> goodColors = Array.with(Color.BLUE, Color.RED, Color.FOREST, Color.BROWN, Color.VIOLET, Color.ORANGE);
    public static Color goodFunctionColor(int id){
        id = id >= 0 ? id : -id;
        id = id % goodColors.size;
        return goodColors.get(id);
    }


    public static Array<Vector2> findParetoMinimal(GraphBiFunction f1, GraphBiFunction f2, double leftX, double rightX, double botY, double topY, double step, @Nullable Consumer<Float> progressCallback){
        int width = (int) Math.ceil((rightX - leftX) / step);
        int height = (int) Math.ceil((topY - botY) / step);
        double[][] v1 = new double[height][width];
        double[][] v2 = new double[height][width];


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = leftX + i * step;
                double y = botY + j * step;
                {
                    v1[j][i] = f1.f(x, y);
                    v2[j][i] = f2.f(x, y);
                }
            }
        }
        Array<Vector2> result = new Array<>();
        for (int i = 0; i < width; i++) {
            double x = leftX + i * step;
            double ySum = 0;
            int count = 0;

            for (int j = 0; j < height; j++) {
                double y = botY + j * step;
                double val_1 = v1[j][i];
                double val_2 = v2[j][i];

                boolean foundAnyBetter = false;

                findingBetterPoint:
                for (int k = 0; k < width; k++) {
                    for (int l = 0; l < height; l++) {
                        if (v1[l][k] < val_1 && v2[l][k] < val_2){
                            foundAnyBetter = true;
                            break findingBetterPoint;
                        }
                    }
                }
                if (!foundAnyBetter){
                    ySum += y;
                    count++;
                }
            }
            if (count > 0){
                result.add(new Vector2((float)x, (float)(ySum / count)));
            }
            if (progressCallback != null) progressCallback.accept(((float) i) / width);
        }
        return result;
    }

    public static void renderPoints(ShapeRenderer sr, Array<Vector2> points){
        for (int i = 1; i < points.size; i++) {
            sr.line(points.get(i - 1), points.get(i));
        }
    }


}
