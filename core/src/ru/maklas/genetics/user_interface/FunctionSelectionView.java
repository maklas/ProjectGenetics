package ru.maklas.genetics.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Consumer;
import com.badlogic.gdx.utils.MapFunction;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import ru.maklas.genetics.utils.functions.*;
import ru.maklas.genetics.utils.persistance.BiConsumer;

public class FunctionSelectionView extends BaseStage {

    VisTable mainTable;
    VisSelectBox<String> selectBox;
    Container<ParamTable> paramTableContainer;
    Consumer<GraphFunction> changeListener = f -> {};

    ParamTable<LinearFunction> linearFunctionTable;
    ParamTable<ParabolaFunction> quadraticFunctionTable;
    ParamTable<SineFunction> sinFunctionTable;
    ParamTable<DampedSineWaveFunction> dampedSinFunctionTable;

    public FunctionSelectionView(GraphFunction function) {

        mainTable = new VisTable();
        mainTable.setFillParent(true);
        mainTable.top();

        selectBox = new VisSelectBox<>();
        selectBox.setItems("Linear", "Quadratic", "Sin", "Damped Sin");
        selectBox.setSelected("Linear");

        paramTableContainer = new Container<>();

        addActor(mainTable);
        mainTable.add(selectBox).left();
        mainTable.add().width(200);
        mainTable.add(paramTableContainer).right();


        linearFunctionTable = new ParamTable<>(new LinearFunction(1, 0))
                .addParam("k", 1, (f, k) -> f.k = k, f -> f.k)
                .addParam("b", 1, (f, b) -> f.b = b, f -> f.b)
                .update()
                .onChanged(f -> changeListener.accept(getFunction()));

        quadraticFunctionTable = new ParamTable<>(new ParabolaFunction(1, 1, 1))
                .addParam("a", 1, (f, a) -> f.a = a, f -> f.a)
                .addParam("b", 1, (f, b) -> f.b = b, f -> f.b)
                .addParam("c", 1, (f, c) -> f.c = c, f -> f.c)
                .update()
                .onChanged(f -> changeListener.accept(getFunction()));

        sinFunctionTable = new ParamTable<>(new SineFunction(10, 5, 0))
                .addParam("amp", 10, (f, a) -> f.amp = a, f -> f.amp)
                .addParam("wave length", 5, (f, l) -> f.waveLen = l, f -> f.waveLen)
                .addParam("shift", 0, (f, s) -> f.shift = s, f -> f.shift)
                .update()
                .onChanged(f -> changeListener.accept(getFunction()));

        dampedSinFunctionTable = new ParamTable<>(new DampedSineWaveFunction(100, 10, 0, 0.01f))
                .addParam("amp", 10, (f, a) -> f.amp = a, f -> f.amp)
                .addParam("wave length", 3, (f, l) -> f.waveLen = l, f -> f.waveLen)
                .addParam("Y-shift", 1, (f, s) -> f.shift = s, f -> f.shift)
                .addParam("decay", 1, (f, d) -> f.decay = d, f -> f.decay)
                .update()
                .onChanged(f -> changeListener.accept(getFunction()));

        paramTableContainer.setActor(linearFunctionTable);
        mainTable = linearFunctionTable;


        if (function instanceof LinearFunction){
            selectBox.setSelected("Linear");
            linearFunctionTable.function = (LinearFunction) function;
            set("Linear");
        } else if (function instanceof ParabolaFunction){
            selectBox.setSelected("Quadratic");
            quadraticFunctionTable.function = (ParabolaFunction) function;
            set("Quadratic");
        } else if (function instanceof SineFunction){
            selectBox.setSelected("Sin");
            sinFunctionTable.function = (SineFunction) function;
            set("Sin");
        } else if (function instanceof DampedSineWaveFunction){
            selectBox.setSelected("Damped Sin");
            dampedSinFunctionTable.function = (DampedSineWaveFunction) function;
            set("Damped Sin");
        }

        paramTableContainer.getActor().update();
        selectBox.addChangeListener(this::set);
    }


    private void set(String choice){
        ParamTable table;
        switch (choice){
            default:
            case "Linear": table = linearFunctionTable; break;
            case "Quadratic": table = quadraticFunctionTable; break;
            case "Sin": table = sinFunctionTable; break;
            case "Damped Sin": table = dampedSinFunctionTable; break;
        }

        paramTableContainer.setActor(table);
        if (changeListener != null){
            changeListener.accept(getFunction());
        }
    }


    public GraphFunction getFunction(){
        return paramTableContainer.getActor().getFunction();
    }

    public void onFunctionChange(Consumer<GraphFunction> functionConsumer){
        changeListener = functionConsumer;
    }




    private class ParamTable<T extends GraphFunction> extends VisTable {

        private T function;
        private Consumer<T> changeListener;
        private Array<ValueReader> readers = new Array<>();

        public ParamTable(T function) {
            this.function = function;
            right();
            defaults().padBottom(15);
        }

        public ParamTable<T> addParam(String name, float defaultValue, BiConsumer<T, Double> writeFunction, MapFunction<T, Double> readFunction){
            VisLabel label = new VisLabel(name, Color.BLACK);
            VisTextField field = new VisValidatableTextField(new Validators.FloatValidator());
            field.setText(String.valueOf(defaultValue));
            field.addChangeListener(() -> {
                String text = field.getText();
                double val = 0;
                try {
                    val = Double.parseDouble(text);
                } catch (NumberFormatException e) {}

                writeFunction.consume(function, val);
                if (changeListener != null){
                    changeListener.accept(function);
                }
            });
            readers.add(new ValueReader(field, readFunction));
            add(label).padRight(10);
            add(field).width(150);
            row();
            return this;
        }

        ParamTable<T> onChanged(Consumer<T> consumer){
            changeListener = consumer;
            return this;
        }

        ParamTable<T> update(){
            readers.foreach(ValueReader::read);
            return this;
        }

        public T getFunction() {
            return function;
        }

        private class ValueReader {
            VisTextField field;
            MapFunction<T, ?> readFunction;

            public ValueReader(VisTextField field, MapFunction<T, ?> readFunction) {
                this.field = field;
                this.readFunction = readFunction;
            }

            void read(){
                field.setText(String.valueOf(readFunction.map(function)));
            }
        }
    }


}
