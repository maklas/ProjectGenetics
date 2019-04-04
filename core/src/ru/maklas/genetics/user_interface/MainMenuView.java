package ru.maklas.genetics.user_interface;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class MainMenuView extends BaseStage {

    private VisTextButton functionBtn;
    private VisTextButton paretoBtn;
    private VisTextButton nashBtn;
    private VisTextButton graphBtn;

    public MainMenuView() {
        functionBtn = new VisTextButton("Минимум функции");
        paretoBtn = new VisTextButton("Оптимальность по Парето");
        graphBtn = new VisTextButton("Графики");
        nashBtn = new VisTextButton("Равновесие Нэша");

        VisTable table = new VisTable();
        table.setFillParent(true);
        addActor(table);

        table.add(functionBtn).padBottom(20);
        table.row();
        table.add(nashBtn).padBottom(20);
        table.row();
        table.add(paretoBtn).padBottom(20);
        table.row();
        table.add(graphBtn);
    }


    public void onFunction(Runnable r){
        functionBtn.addChangeListener(r);
    }

    public void onNash(Runnable r){
        nashBtn.addChangeListener(r);
    }

    public void onPareto(Runnable r){
        paretoBtn.addChangeListener(r);
    }

    public void onGraph(Runnable r){
        graphBtn.addChangeListener(r);
    }
}
