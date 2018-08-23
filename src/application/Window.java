package application;

import drawing.DrawingSheet;
import map.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class Window extends JFrame {

    private final int _SCREEN_WIDTH = 800;
    private final int _SCREEN_HEIGHT = 800;

    private final Screenshoter _screenshoter = new Screenshoter(_SCREEN_WIDTH, _SCREEN_HEIGHT);

    private Map _map = new Map(_SCREEN_WIDTH, _SCREEN_HEIGHT);
    private DrawingSheet _drawPanel = new DrawingSheet(_SCREEN_WIDTH, _SCREEN_HEIGHT, this._map);

    Window() {
        super("Project-Domination");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(_SCREEN_WIDTH, _SCREEN_HEIGHT));
        setLayout(new BorderLayout());
        add(_drawPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
        _map.randomize();
        _map.print();
    }

    void loop() {
        while (true) {
            _map.update();
            _drawPanel.repaint();
            _screenshoter.getScreenshot();
        }
    }

}
