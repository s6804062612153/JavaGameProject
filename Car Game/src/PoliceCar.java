import java.awt.*;

public class PoliceCar extends GameObject {

    // Constructor
    public PoliceCar(String fileName, int speed, CarGUI gui) {
        super(fileName, 110, 220, speed, gui);
    }

    // Spawns police car at bottom
    public void spawn(int startX) {
        super.spawn(startX, 1080);
    }

    // Updates position
    @Override
    public void updatePosition() {
        if (!active || gui.isGameOver()) return;

        int nextY = getY() - speed;
        setLocation(getX(), nextY);

        Rectangle playerBounds = gui.carPanel.getBounds();
        Rectangle policeBounds = getBounds();

        if (playerBounds.intersects(policeBounds)) {
            Rectangle intersection = playerBounds.intersection(policeBounds);
            int centerX = (int) intersection.getCenterX();
            int centerY = (int) intersection.getCenterY();
            gui.gameOver(centerX - 70, centerY - 66);
            return;
        }

        if (nextY < -220) {
            reset();
        }
    }
}