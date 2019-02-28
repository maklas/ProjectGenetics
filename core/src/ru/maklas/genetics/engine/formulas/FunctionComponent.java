package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.mengine.Component;

public class FunctionComponent implements Component {

    public GraphFunction graphFunction;
    public Color color = Color.WHITE.cpy();
    public boolean trackMouse = true;
    public float precision = 1f;

    public FunctionComponent(GraphFunction graphFunction) {
        this.graphFunction = graphFunction;
    }


}
