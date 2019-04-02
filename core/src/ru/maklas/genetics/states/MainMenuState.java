package ru.maklas.genetics.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.functions.CustomExpressionFunction;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.user_interface.MainMenuView;
import ru.maklas.genetics.utils.gsm_lib.State;

public class MainMenuState extends State {

    MainMenuView view;

    @Override
    protected void onCreate() {
        view = new MainMenuView();
        view.onFunction(() -> pushState(new FunctionGeneticsSetupState()));
        view.onPareto(() -> pushState(new ParetoSetupState()));
        view.onGraph(() -> pushState(new FunctionGraphState(Array.with(new CustomExpressionFunction("x^2"), new CustomExpressionFunction("x * log(x, 2)")))));
        MNW.backgroundColor.set(0.95f, 0.95f, 0.95f, 1);
    }

    @Override
    protected void update(float dt) {
        view.act();
    }

    @Override
    protected InputProcessor getInput() {
        return view;
    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
    }

    @Override
    protected void render(Batch batch) {
        view.draw();
    }

    @Override
    protected void dispose() {
        view.dispose();
    }
}
