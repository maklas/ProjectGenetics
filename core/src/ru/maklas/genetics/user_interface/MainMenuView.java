package ru.maklas.genetics.user_interface;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class MainMenuView extends BaseStage {

    private final VisTextButton start;
    private final VisTextButton selectFunction;
    private final VisLabel functionDescription;

    public MainMenuView() {
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.align(Align.topLeft);
        table.pad(20);
        table.defaults().padBottom(10).left();
        addActor(table);

        selectFunction = new VisTextButton("Функция");
        functionDescription = new VisLabel();
        start = new VisTextButton("start");


        table.add(selectFunction).padRight(10);
        table.add(functionDescription);
        table.row();
        table.add(start);
    }

    public void onStart(Runnable r){
        start.addChangeListener(r);
    }

    public void setFunctionDescription(String s){
        functionDescription.setText(s);
    }

    public void onSelectFunction(Runnable r){
        selectFunction.addChangeListener(r);
    }
}
