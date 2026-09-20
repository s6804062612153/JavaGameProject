import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TitleGUI {
    private JFrame fr;
    private JLayeredPane layeredPane;

    // Default constructor
    public TitleGUI() {
        this(null);
    }

    // Constructor
    public TitleGUI(JFrame existingFrame) {
        if (existingFrame != null) {
            this.fr = existingFrame;
            this.fr.setTitle("Race to Parksoi - Main Menu");
        } else {
            this.fr = new JFrame("Race to Parksoi - Main Menu");
            this.fr.setSize(1920, 1080);
            this.fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.fr.setResizable(false);
            this.fr.setLocationRelativeTo(null);
        }
        initGUI();
    }

    // Initializes title screen components
    private void initGUI() {
        layeredPane = fr.getLayeredPane();
        layeredPane.removeAll();
        layeredPane.setOpaque(true);
        layeredPane.setBackground(Color.BLACK);

        ImageIcon bgIcon = loadScaledIcon("TitleBG.png", 1920, 1080);
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 1920, 1080);
        layeredPane.add(background, Integer.valueOf(1));

        JLabel titleLabel = new JLabel("RACING TO PARKSOI !", SwingConstants.CENTER);
        titleLabel.setFont(loadCustomFont(130f));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(0, 90, 1920, 160);
        layeredPane.add(titleLabel, Integer.valueOf(2));

        ImageIcon startIcon = loadScaledIcon("StartBtn.png", 805, 267);
        JButton startBtn = new JButton(startIcon);

        startBtn.setBounds(557, 410, 805, 267);
        startBtn.setOpaque(false);
        startBtn.setContentAreaFilled(false);
        startBtn.setBorderPainted(false);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        startBtn.addActionListener(e -> {
            new LevelSelectGUI(fr);
        });

        layeredPane.add(startBtn, Integer.valueOf(2));

        fr.revalidate();
        fr.repaint();
        fr.setVisible(true);
    }

    // Loads image icon from asset directory
    private ImageIcon loadScaledIcon(String fileName, int width, int height) {
        ImageIcon icon = new ImageIcon("asset/" + fileName);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // Loads custom font from asset directory
    private Font loadCustomFont(float size) {
        try {
            File fontFile = new File("asset/SedgwickAveDisplay-Regular.ttf");
            return Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.BOLD, size);
        } catch (Exception e) {
            return new Font("Impact", Font.BOLD, (int) size);
        }
    }
}