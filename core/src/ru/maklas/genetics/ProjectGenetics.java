package ru.maklas.genetics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.libs.SimpleProfiler;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.assets.Asset;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.mnw.*;
import ru.maklas.genetics.states.MainMenuState;
import ru.maklas.genetics.statics.Game;
import ru.maklas.genetics.utils.Log;
import ru.maklas.genetics.utils.gsm_lib.EmptyStateManager;
import ru.maklas.genetics.utils.gsm_lib.MultilayerStateManager;
import ru.maklas.genetics.utils.gsm_lib.State;
import ru.maklas.genetics.utils.Utils;

public class ProjectGenetics extends ApplicationAdapter {

    private State launchState;
    private Batch batch;

    public ProjectGenetics(State state) {
        this.launchState = state;
    }

    public ProjectGenetics() {
        this(new MainMenuState());
    }

    @Override
    public void create () {
        float scale = (float) Gdx.graphics.getWidth() / Game.width;
        Game.height = Math.round((float) Gdx.graphics.getHeight() / scale);
        Game.hHeight = Game.height / 2;

        State launchState = this.launchState;
        this.launchState = null;
        try {
            initialize();
            MNW.gsm.launch(launchState, batch);
        } catch (Exception e) {
            if (MNW.crash != null) MNW.crash.report(e);
            e.printStackTrace();
            Gdx.app.exit();
            MNW.gsm = new EmptyStateManager();
        }
    }

    private void initialize(){
        SimpleProfiler.start();
        batch = new SpriteBatch();
        MNW.gsm = new MultilayerStateManager();
        MNW.save = new GameSave();
        MNW.save.loadFromFile();
        if (MNW.device == null) MNW.device = new PCDevice();
        if (MNW.analytics == null) MNW.analytics = new NoAnalytics();
        if (MNW.crash == null) MNW.crash = new NoCrashReport();
        if (MNW.statistics == null) MNW.statistics = new NoStatistics();
        if (MNW.ads == null) MNW.ads = new NoAds();

        if (!MNW.save.firstLanguageSet){
            MNW.save.language = Strings.identifyDeviceLocale(Language.ENGLISH);
            MNW.save.firstLanguageSet = true;
        }
        MNW.strings = new Strings(MNW.save.language);
        M.initialize();

        A.images.load();
        A.skins.load();

        Log.trace("Initialized in " + SimpleProfiler.getTimeAsString());
    }


    @Override
    public void render () {
        Color bg = MNW.backgroundColor;
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        MNW.gsm.update(Utils.getDeltaTime());
    }

    @Override
    public void pause() {
        MNW.gsm.toBackground();
    }

    @Override
    public void resume() {
        MNW.gsm.toForeground();
    }

    @Override
    public void dispose () {
        try {
            MNW.save.onExit();

            MNW.gsm.dispose();
            batch.dispose();
            A.getAllAssets().foreach(Asset::dispose);
            Log.logger.dispose();
            Utils.executor.shutdown();
        } catch (Exception e) {
            if (MNW.crash != null) MNW.crash.report(e);
        }
    }


}
