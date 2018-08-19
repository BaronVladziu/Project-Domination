package drawing;

import java.awt.*;
import java.util.ArrayList;

public class PlayerColors {

    private final static int _MAX_PLAYER_COLOR_ID = 44;
    private final static int _NUMBER_OF_COLOR_STATES = 4;
    private final static int _MIN_COLOR_SUM = 3;

    private Color[] _colors = new Color[_MAX_PLAYER_COLOR_ID];

    PlayerColors() {
        int actColorID = 0;
        for (int c1 = 0; c1 < _NUMBER_OF_COLOR_STATES; c1++) {
            for (int c2 = 0; c2 < _NUMBER_OF_COLOR_STATES; c2++) {
                for (int c3 = 0; c3 < _NUMBER_OF_COLOR_STATES; c3++) {
                    int colorSum = c1 + c2 + c3;
                    if (colorSum >= _MIN_COLOR_SUM && colorSum <= 3*(_NUMBER_OF_COLOR_STATES - 1) - _MIN_COLOR_SUM) {
                        float v1 = 1.f - (float)c1/(_NUMBER_OF_COLOR_STATES - 1);
                        float v2 = 1.f - (float)c2/(_NUMBER_OF_COLOR_STATES - 1);
                        float v3 = 1.f - (float)c3/(_NUMBER_OF_COLOR_STATES - 1);
                        this._colors[actColorID++] = new Color(v1, v2, v3);
                    }
                }
            }
        }
    }

    public Color getColor(int playerID) {
        if (playerID >= _MAX_PLAYER_COLOR_ID) {
            System.out.println("Warning: Exceeded max player color ID!");
            return Color.WHITE;
        } else if (playerID < 0) {
            return Color.WHITE;
        } else {
            return this._colors[playerID];
        }
    }

}
