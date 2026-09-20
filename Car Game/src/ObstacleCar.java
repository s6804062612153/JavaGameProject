import javax.swing.*;
import java.awt.*;

public class ObstacleCar extends JPanel {
    private final int speed;
    private boolean active = false;
    private final CarGUI gui;

    // Constructor
    public ObstacleCar(String fileName, int speed, CarGUI gui) {
        this.speed = speed;
        this.gui = gui;

        setLayout(new BorderLayout());
        setSize(110, 220);
        setOpaque(false);
        setDoubleBuffered(true);

        Image scaledImage = new ImageIcon("asset/" + fileName).getImage().getScaledInstance(110, 220, Image.SCALE_SMOOTH);
        JLabel carLabel = new JLabel(new ImageIcon(scaledImage));
        carLabel.setOpaque(false);
        add(carLabel, BorderLayout.CENTER);

        reset();
    }

    // Spawns obstacle car
    public void spawn(int startX, int startY) {
        setLocation(startX, startY);
        active = true;
        setVisible(true);
    }

    // Resets obstacle car
    public void reset() {
        active = false;
        setVisible(false);
        setLocation(-500, -500);
    }

    // Updates position
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

    // Returns active status
    public boolean isActive() {
        return active;
    }
}