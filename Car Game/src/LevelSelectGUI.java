import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LevelSelectGUI {
    private JFrame fr;
    private JLayeredPane layeredPane;

    // Constructor for LevelSelectGUI
    public LevelSelectGUI(JFrame existingFrame) {
        if (existingFrame != null) {
            this.fr = existingFrame;
            this.fr.setTitle("Race to Parksoi - Select Level");
        } else {
            this.fr = new JFrame("Race to Parksoi - Select Level");
            this.fr.setSize(1920, 1080);
            this.fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.fr.setResizable(false);
            this.fr.setLocationRelativeTo(null);
        }
        initGUI();
    }

    // Initializes level selection components
    private void initGUI() {
        layeredPane = fr.getLayeredPane();
        layeredPane.removeAll();
        layeredPane.setOpaque(true);
        layeredPane.setBackground(Color.BLACK);

        ImageIcon bgIcon = loadScaledIcon("TitleBG.png", 1920, 1080);
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 1920, 1080);
        layeredPane.add(background, Integer.valueOf(1));

        JLabel headerLabel = new JLabel("SELECT LEVEL", SwingConstants.CENTER);
        headerLabel.setFont(loadCustomFont(80f));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBounds(0, 120, 1920, 120);
        layeredPane.add(headerLabel, Integer.valueOf(2));

        int btnY = 390;
        int btn1X = 597;
        int btn2X = 1010;

        JButton lvl1Btn = new JButton(loadScaledIcon("Level1Btn.png", 313, 300));
        lvl1Btn.setBounds(btn1X, btnY, 313, 300);
        setupButtonStyle(lvl1Btn);
        lvl1Btn.addActionListener(e -> new CarGUI(fr, 1));

        JButton lvl2Btn = new JButton(loadScaledIcon("Level2Btn.png", 313, 300));
        lvl2Btn.setBounds(btn2X, btnY, 313, 300);
        setupButtonStyle(lvl2Btn);
        lvl2Btn.addActionListener(e -> new CarGUI(fr, 2));

        layeredPane.add(lvl1Btn, Integer.valueOf(2));
        layeredPane.add(lvl2Btn, Integer.valueOf(2));

        fr.revalidate();
        fr.repaint();
        fr.setVisible(true);
    }

    // Sets transparent button styling
    private void setupButtonStyle(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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