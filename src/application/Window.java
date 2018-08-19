package application;

import drawing.DrawingSheet;
import map.Map;

import javax.swing.*;
import java.awt.*;

class Window extends JFrame {

    private final int _SCREEN_WIDTH = 800;
    private final int _SCREEN_HEIGHT = 800;

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
        _map.update();
        _drawPanel.repaint();
    }

}
