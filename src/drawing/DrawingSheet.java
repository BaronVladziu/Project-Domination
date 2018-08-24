package drawing;

import map.Map;

import javax.swing.*;
import java.awt.*;

public class DrawingSheet extends JPanel {

    private final boolean _IS_SCREENSHOTING_ON;
    private final int _MAP_WIDTH;
    private final int _MAP_HEIGHT;
    private final PlayerColors _playerColors = new PlayerColors();
    private final Map _map;

    public DrawingSheet(int mapWidth, int mapHeight, final Map map, boolean isScreenshotingOn) {
        this._IS_SCREENSHOTING_ON = isScreenshotingOn;
        this._MAP_WIDTH = mapWidth;
        this._MAP_HEIGHT = mapHeight;
        this._map = map;
        super.setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        if (_IS_SCREENSHOTING_ON) {
//            g2d.setRenderingHints(new RenderingHints(
//                    RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON));
        }
        g2d.drawRect(0,0, _MAP_WIDTH, _MAP_HEIGHT);
        _map.draw(g2d, this._playerColors);
    }

}
