package ru.maklas.genetics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import ru.maklas.genetics.ProjectGenetics;

import javax.swing.*;
import java.awt.*;

public class SwingLauncher extends JFrame {

    public SwingLauncher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JSplitPane jPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        LwjglAWTCanvas canvas = new LwjglAWTCanvas(new ProjectGenetics());
        jPanel.setLeftComponent(new JTextPane());
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(canvas.getCanvas(), BorderLayout.CENTER);
        jPanel.setRightComponent(container);
        setContentPane(jPanel);

        pack();
        setVisible(true);
        setSize(800, 600);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingLauncher::new);
    }

}
