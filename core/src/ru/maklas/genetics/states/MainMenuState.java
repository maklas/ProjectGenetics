package ru.maklas.genetics.states;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.utils.gsm_lib.State;

public class MainMenuState extends State {

    @Override
    protected void onCreate() {
        A.skins.load();
    }


    @Override
    protected void update(float dt) {

    }

    @Override
    protected void render(Batch batch) {

    }

    @Override
    protected void dispose() {

    }
}
