import java.awt.*;

public class Coin extends GameObject {

    // Constructor
    public Coin(String fileName, int speed, CarGUI gui) {
        super(fileName, 120, 116, speed, gui);
    }

    // Updates coin position and gives 5000 points
    @Override
    public void updatePosition() {
        if (!active || gui.isGameOver()) return;

        int nextY = getY() + speed;
        setLocation(getX(), nextY);

        Rectangle playerBounds = gui.carPanel.getBounds();
        Rectangle coinBounds = getBounds();

        if (playerBounds.intersects(coinBounds)) {
            gui.addScore(5000);
            reset();
            return;
        }

        if (nextY > 1080) {
            reset();
        }
    }
}