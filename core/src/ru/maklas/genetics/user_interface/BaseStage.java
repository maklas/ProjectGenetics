package ru.maklas.genetics.user_interface;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class BaseStage extends Stage implements View {



    public BaseStage() {
        super(new ScreenViewport());
    }

    @Override
    public InputProcessor getInput() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }
}
