package ru.maklas.genetics.user_interface.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.utils.StringUtils;

public class Windows {
    
    public static Window textInfoWindow(String title, String text){
        MNWwindow window = new MNWwindow (title, A.skins.skin);
        window.getTitleTable().padBottom(-10);
        text = StringUtils.wrapString(text, 30);
        window.add(new Label(text, window.getSkin(), "acrobat_semibold", "d_brown"))
                .pad(25);
        window.setRemoveOnAnyClick(true);
        window.pack();
        return window;
    }

}
