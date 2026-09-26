import javax.swing.*;
import java.awt.*;

public abstract class GameObject extends JPanel {
    protected int speed;
    protected boolean active = false;
    protected CarGUI gui;

    public GameObject(String fileName, int width, int height, int speed, CarGUI gui) {
        this.speed = speed;
        this.gui = gui;
        setLayout(new BorderLayout());
        setSize(width, height);
        setOpaque(false);
        setDoubleBuffered(true);

        Image img = new ImageIcon("asset/" + fileName).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setOpaque(false);
        add(label, BorderLayout.CENTER);

        reset();
    }

    public void spawn(int startX, int startY) {
        setLocation(startX, startY);
        active = true;
        setVisible(true);
    }

    public void reset() {
        active = false;
        setVisible(false);
        setLocation(-500, -500);
    }

    public boolean isActive() {
        return active;
    }

    public abstract void updatePosition();
}