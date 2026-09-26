import java.awt.*;

public class Hole extends GameObject {

    // Constructor
    public Hole(String fileName, int speed, CarGUI gui) {
        super(fileName, 110, 110, speed, gui);
    }

    // Updates position
    @Override
    public void updatePosition() {
        if (!active || gui.isGameOver()) return;

        int nextY = getY() + speed;
        setLocation(getX(), nextY);

        Rectangle playerBounds = gui.carPanel.getBounds();
        Rectangle holeBounds = getBounds();

        if (playerBounds.intersects(holeBounds)) {
            Rectangle intersection = playerBounds.intersection(holeBounds);
            int centerX = (int) intersection.getCenterX();
            int centerY = (int) intersection.getCenterY();
            gui.gameOver(centerX - 70, centerY - 66);
            return;
        }

        if (nextY > 1080) {
            reset();
        }
    }
}