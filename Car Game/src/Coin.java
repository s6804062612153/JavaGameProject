import javax.swing.*;
import java.awt.*;

public class Coin extends JPanel {
    private final int speed;
    private boolean active = false;
    private final CarGUI gui;

    // Constructor
    public Coin(String fileName, int speed, CarGUI gui) {
        this.speed = speed;
        this.gui = gui;

        setLayout(new BorderLayout());
        setSize(120, 116);
        setOpaque(false);
        setDoubleBuffered(true);

        Image img = new ImageIcon("asset/" + fileName).getImage().getScaledInstance(120, 116, Image.SCALE_SMOOTH);
        JLabel coinLabel = new JLabel(new ImageIcon(img));
        coinLabel.setOpaque(false);
        add(coinLabel, BorderLayout.CENTER);

        reset();
    }

    // Spawns the coin
    public void spawn(int startX, int startY) {
        setLocation(startX, startY);
        active = true;
        setVisible(true);
    }

    // Resets coin state
    public void reset() {
        active = false;
        setVisible(false);
        setLocation(-500, -500);
    }

    // Updates coin position and gives 5000 points on collection
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

    // Returns active status
    public boolean isActive() {
        return active;
    }
}