package ru.maklas.genetics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.genetics.ProjectGenetics;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.utils.Log;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 640;
        config.width = 360;
        config.resizable = true;
        config.samples = 4;
        config.title = MNW.GAME_NAME;
        Log.logger = new FileLogger();
        new LwjglApplication(new ProjectGenetics(), config);
    }
}
