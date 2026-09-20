import javax.swing.*;
import java.awt.*;

public class Hole extends JPanel {
    private final int speed;
    private boolean active = false;
    private final CarGUI gui;

    // Constructor
    public Hole(String fileName, int speed, CarGUI gui) {
        this.speed = speed;
        this.gui = gui;

        setLayout(new BorderLayout());
        setSize(110, 110);
        setOpaque(false);
        setDoubleBuffered(true);

        Image img = new ImageIcon("asset/" + fileName).getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
        JLabel holeLabel = new JLabel(new ImageIcon(img));
        holeLabel.setOpaque(false);
        add(holeLabel, BorderLayout.CENTER);

        reset();
    }

    // Spawns hole
    public void spawn(int startX, int startY) {
        setLocation(startX, startY);
        active = true;
        setVisible(true);
    }

    // Resets hole state
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

    // Returns active status
    public boolean isActive() {
        return active;
    }
}