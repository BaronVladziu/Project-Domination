package drawing;

import map.Map;

import javax.swing.*;
import java.awt.*;

public class DrawingSheet extends JPanel {

    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;
    private final PlayerColors _playerColors = new PlayerColors();
    private final Map _map;

    public DrawingSheet(int mapWidth, int mapHeight, final Map map) {
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
        this._map = map;
        super.setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawRect(0,0, _MAP_WIDTH, _MAP_HEIGHT);
        _map.draw(g, this._playerColors);
    }

}
