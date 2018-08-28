package application;

import drawing.DrawingSheet;
import map.Map;

import javax.swing.*;
import java.awt.*;

class Window extends JFrame {

    private final boolean _IS_SCREENSHOTING_ON = false;
    private final int _SCREEN_WIDTH = 1000;
    private final int _SCREEN_HEIGHT = 1000;

    private final Screenshoter _screenshoter = new Screenshoter(_SCREEN_WIDTH, _SCREEN_HEIGHT);

    private Map _map = new Map(_SCREEN_WIDTH, _SCREEN_HEIGHT);
    private DrawingSheet _drawPanel = new DrawingSheet(_SCREEN_WIDTH, _SCREEN_HEIGHT, this._map, _IS_SCREENSHOTING_ON);

    Window() {
        super("Project-Domination");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(_SCREEN_WIDTH + 115, _SCREEN_HEIGHT + 135));
        setLayout(new BorderLayout());
        add(_drawPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
        _map.randomize();
        //_map.print();
    }

    void loop() {
        while (true) {
            _map.update();
            _drawPanel.repaint();
            if (_IS_SCREENSHOTING_ON) {
                _screenshoter.getScreenshot();
            }
        }
    }

}
