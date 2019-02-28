package ru.maklas.genetics.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.user_interface.MainMenuView;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.genetics.utils.functions.ParabolaFunction;
import ru.maklas.genetics.utils.gsm_lib.State;

public class MainMenuState extends State {

    MainMenuView mm;
    GraphFunction function = new ParabolaFunction(0.1f, 0, 0);

    @Override
    protected void onCreate() {
        A.skins.load();
        mm = new MainMenuView();

        mm.onStart(() -> pushState(new GeneticsGenerationState()));
        mm.onSelectFunction(() -> pushState(new FunctionSelectionState(function)));

        setFunDesc(function);

    }

    private void setFunDesc(GraphFunction function) {
        mm.setFunctionDescription(function.toString());
    }


    @Override
    protected void update(float dt) {
        mm.act(dt);
    }

    @Override
    protected InputProcessor getInput() {
        return mm;
    }

    @Override
    protected void onResume(State from) {
        if (from instanceof FunctionSelectionState){
            GraphFunction selectedFunction = ((FunctionSelectionState) from).selectedFunction;
            function = selectedFunction;
            setFunDesc(function);
        }
    }

    @Override
    public void resize(int width, int height) {
        mm.resize(width, height);
    }

    @Override
    protected void render(Batch batch) {
        mm.draw();
    }

    @Override
    protected void dispose() {
        mm.dispose();
    }
}
