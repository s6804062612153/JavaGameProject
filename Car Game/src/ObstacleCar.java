import java.awt.*;

public class ObstacleCar extends GameObject {

    // Constructor
    public ObstacleCar(String fileName, int speed, CarGUI gui) {
        super(fileName, 110, 220, speed, gui);
    }

    // Updates position
    @Override
    public void updatePosition() {
        if (!active || gui.isGameOver()) return;

        int nextY = getY() + speed;
        setLocation(getX(), nextY);

        Rectangle playerBounds = gui.carPanel.getBounds();
        Rectangle obstacleBounds = getBounds();

        if (playerBounds.intersects(obstacleBounds)) {
            Rectangle intersection = playerBounds.intersection(obstacleBounds);
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