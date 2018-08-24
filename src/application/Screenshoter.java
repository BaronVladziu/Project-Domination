package application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Screenshoter {

    private final static int _MAX_NUMBER_OF_DIGITS_IN_SCREEN_NUMBER = 9;

    private final int _screenX;
    private final int _screenY;
    private final int _offsetX;
    private final int _offsetY;
    private int _screenshotID = 1;


    public Screenshoter(int screenX, int screenY) {
        this._screenX = screenX - 11;
        this._screenY = screenY - 45;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this._offsetX = screen.width / 2 + 66;
        this._offsetY = 63;
    }

    public void getScreenshot() {
        Rectangle rectangle = new Rectangle(this._offsetX, this._offsetY, this._screenX, this._screenY);
        try {
            Robot robot = new Robot();
            BufferedImage img = robot.createScreenCapture(rectangle);

            //Converting int to string with zeros at the front
            String number = "";
            int actDigit = _MAX_NUMBER_OF_DIGITS_IN_SCREEN_NUMBER;
            while (_screenshotID < Math.pow(10, actDigit)) {
                number += "0";
                actDigit--;
            }
            number += String.valueOf(_screenshotID++);

            ImageIO.write(img, "jpg", new File("screenshots/screenshot" + number + ".jpg"));
        } catch (Exception e) {
            System.out.print("Taking screenshot failed:");
            System.out.print(e.getMessage());
            System.out.print('\n');
        }
    }

}
