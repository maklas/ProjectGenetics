package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.genetics.functions.GraphFunction;
import ru.maklas.mengine.Component;

public class FunctionComponent implements Component {

    public GraphFunction graphFunction;
    public Color color = Color.WHITE.cpy();
    public boolean trackMouse = true;
    public double precision = 1d;
    public float lineWidth = 1f; //1..2

    public FunctionComponent(GraphFunction graphFunction) {
        this.graphFunction = graphFunction;
    }


}
