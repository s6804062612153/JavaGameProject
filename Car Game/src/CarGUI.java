import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TrackPanel extends JPanel {
    private final Image trackImage;
    private int trackY = 0;
    private final int scrollSpeed = 10;

    // Constructor
    public TrackPanel(Image image) {
        this.trackImage = image;
        setOpaque(false);
        setDoubleBuffered(true);
    }

    // Scrolls background image
    public void updateScroll() {
        trackY = (trackY + scrollSpeed) % 1080;
        repaint();
    }

    // Draws scrolling track
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (trackImage != null) {
            g.drawImage(trackImage, 0, trackY, 800, 1080, null);
            g.drawImage(trackImage, 0, trackY - 1080, 800, 1080, null);
        }
    }
}

class ProgressGauge extends JPanel {
    private int currentScore = 0;
    private final int maxScore = 100000;

    // Constructor
    public ProgressGauge() {
        setOpaque(false);
        setDoubleBuffered(true);
        setSize(400, 60);
    }

    // Sets gauge score value and repaints
    public void setScore(int score) {
        this.currentScore = Math.min(Math.max(score, 0), maxScore);
        repaint();
    }

    // Draws progress bar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int barX = 20;
            int barY = 20;
            int barWidth = 320;
            int barHeight = 18;
            int circleRadius = 15;

            g2d.setColor(Color.BLACK);
            g2d.fillRect(barX, barY, barWidth, barHeight);

            double progress = (double) currentScore / maxScore;
            int fillWidth = (int) (progress * barWidth);

            g2d.setColor(new Color(254, 224, 126));
            g2d.fillRect(barX, barY, fillWidth, barHeight);

            g2d.setColor(new Color(40, 40, 40));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(barX, barY, barWidth, barHeight);

            int circleX = barX + fillWidth;
            int circleY = barY + (barHeight / 2);

            g2d.setColor(new Color(255, 226, 120));
            g2d.fillOval(circleX - circleRadius, circleY - circleRadius,
                    circleRadius * 2, circleRadius * 2);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.drawOval(circleX - circleRadius, circleY - circleRadius,
                    circleRadius * 2, circleRadius * 2);
        } finally {
            g2d.dispose();
        }
    }
}

public class CarGUI {
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final int CAR_WIDTH = 110;
    private static final int CAR_HEIGHT = 220;
    private static final int MOVE_STEP = 10;
    private static final int GAME_TICK_MS = 16;

    JFrame fr;
    JPanel carPanel;
    CarHandler hnd;
    JLabel background;
    TrackPanel trackLabel;
    JLayeredPane layeredPane;
    int speed;

    private Timer gameLoopTimer;
    private long tickCount = 0;

    private int targetX;
    private int targetY;

    private boolean isGameOver = false;
    private boolean isWin = false;
    private boolean policeReadyToSpawn = false;
    private boolean readyForFinishLine = false;
    private boolean finishLineSpawned = false;

    private int score = 0;
    private JLabel scoreLabel;
    private ProgressGauge progressGauge;

    private JLabel finishLineLabel;
    private int finishLineY = -171;
    private int finishLineX;

    private final List<ObstacleCar> obstaclePool = new ArrayList<>();
    private static final int POOL_SIZE = 6;
    private PoliceCar policeCar;

    private final List<Coin> coinPool = new ArrayList<>();
    private static final int COIN_POOL_SIZE = 4;

    private final List<Hole> holePool = new ArrayList<>();
    private static final int HOLE_POOL_SIZE = 3;

    private JLabel smokeLabel;
    private JLabel gameOverLabel;
    private JLabel winLabel;
    private JButton restartBtn;
    private Random random = new Random();

    private int selectedLevel = 1;
    private Clip bgmClip;

    // Constructor for CarGUI
    public CarGUI() {
        this(null, 1);
    }

    // Constructor for CarGUI
    public CarGUI(JFrame existingFrame) {
        this(existingFrame, 1);
    }

    // Constructor for CarGUI
    public CarGUI(JFrame existingFrame, int level) {
        this.selectedLevel = level;
        if (existingFrame != null) {
            this.fr = existingFrame;
            this.fr.setTitle("Race to Parksoi - Level " + level);
        }
        
        if (selectedLevel == 2) {
            speed = MOVE_STEP + 3;
        } else {
            speed = MOVE_STEP;
        }

        hnd = new CarHandler(this);
        initGUI();
    }

    // Initializes gameplay UI
    private void initGUI() {
        if (fr == null) {
            fr = new JFrame("Race to Parksoi");
            fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fr.setResizable(false);
            fr.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            fr.setLocationRelativeTo(null);
        }

        layeredPane = fr.getLayeredPane();
        layeredPane.removeAll();
        layeredPane.setOpaque(true);
        layeredPane.setBackground(Color.BLACK);
        layeredPane.setDoubleBuffered(true);

        ImageIcon bgIcon = loadScaledIcon("BG.png", WINDOW_WIDTH, WINDOW_HEIGHT, Image.SCALE_SMOOTH);
        background = new JLabel(bgIcon);
        background.setOpaque(true);
        background.setBackground(Color.BLACK);
        background.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        layeredPane.add(background, Integer.valueOf(1));

        Image trackImg = new ImageIcon("asset/TRACK.jpg").getImage();
        trackLabel = new TrackPanel(trackImg);
        trackLabel.setBounds(560, 0, 800, 1080);
        layeredPane.add(trackLabel, Integer.valueOf(2));

        carPanel = new JPanel(new BorderLayout());
        carPanel.setSize(CAR_WIDTH, CAR_HEIGHT);
        carPanel.setOpaque(false);
        carPanel.setDoubleBuffered(true);

        ImageIcon playerIcon = loadScaledIcon("CARS 2.png", CAR_WIDTH, CAR_HEIGHT, Image.SCALE_SMOOTH);
        JLabel carLabel = new JLabel(playerIcon);
        carLabel.setOpaque(false);
        carPanel.add(carLabel, BorderLayout.CENTER);

        int startX = 905;
        int startY = 800;
        carPanel.setLocation(startX, startY);
        targetX = startX;
        targetY = startY;

        layeredPane.add(carPanel, Integer.valueOf(3));

        progressGauge = new ProgressGauge();
        progressGauge.setLocation(60, 50);
        layeredPane.add(progressGauge, Integer.valueOf(5));

        Font customFont;
        try {
            File fontFile = new File("asset/SedgwickAveDisplay-Regular.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile)
                    .deriveFont(Font.BOLD, 48f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            customFont = new Font("Serif", Font.BOLD, 48);
        }

        scoreLabel = new JLabel("SCORE: 0");
        scoreLabel.setFont(customFont);
        scoreLabel.setForeground(Color.decode("#980000"));
        scoreLabel.setBounds(1450, 60, 400, 80);
        layeredPane.add(scoreLabel, Integer.valueOf(5));

        finishLineX = trackLabel.getX() + (trackLabel.getWidth() - 670) / 2;
        initFinishLine();

        policeCar = new PoliceCar("POLICE_CAR.png", 8, this);
        layeredPane.add(policeCar, Integer.valueOf(3));

        initObstaclePool();
        initCoinPool();
        if (selectedLevel == 2) {
            initHolePool();
        }

        playBGM("bgm.wav");

        initMasterGameLoop();

        hnd.installKeyBindings(layeredPane);

        fr.revalidate();
        fr.repaint();
        fr.setVisible(true);
        SwingUtilities.invokeLater(() -> layeredPane.requestFocusInWindow());
    }

    // Plays background music on continuous loop
    private void playBGM(String fileName) {
        try {
            stopBGM();
            File soundFile = new File("asset/" + fileName);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(audioIn);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stops background music
    private void stopBGM() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) {
                bgmClip.stop();
            }
            bgmClip.close();
            bgmClip = null;
        }
    }

    // Initializes hole pool
    private void initHolePool() {
        for (int i = 0; i < HOLE_POOL_SIZE; i++) {
            Hole hole = new Hole("HOLE.png", 10, this);
            holePool.add(hole);
            layeredPane.add(hole, Integer.valueOf(3));
        }
    }

    // Retrieves inactive hole
    private Hole getAvailableHole() {
        for (Hole h : holePool) {
            if (!h.isActive()) return h;
        }
        return null;
    }

    // Loads image icon from asset directory
    private ImageIcon loadScaledIcon(String fileName, int width, int height, int hints) {
        ImageIcon icon = new ImageIcon("asset/" + fileName);
        Image image = icon.getImage().getScaledInstance(width, height, hints);
        return new ImageIcon(image);
    }

    // Initializes finish line
    private void initFinishLine() {
        ImageIcon finishIcon = loadScaledIcon("FINISH.png", 670, 171, Image.SCALE_SMOOTH);
        finishLineLabel = new JLabel(finishIcon);
        finishLineLabel.setBounds(finishLineX, -171, 670, 171);
        finishLineLabel.setVisible(false);
        layeredPane.add(finishLineLabel, Integer.valueOf(3));
    }

    // Returns X coordinates
    private int[] getLaneX() {
        return new int[]{
                trackLabel.getX() + 68,
                trackLabel.getX() + 68 + 138,
                trackLabel.getX() + 68 + (138 * 2),
                trackLabel.getX() + 68 + (138 * 3)
        };
    }

    // Initializes obstacle car
    private void initObstaclePool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            ObstacleCar obs = new ObstacleCar("ENEMY_CAR.png", 8, this);
            obstaclePool.add(obs);
            layeredPane.add(obs, Integer.valueOf(3));
        }
    }

    // Initializes coin pool
    private void initCoinPool() {
        for (int i = 0; i < COIN_POOL_SIZE; i++) {
            Coin coin = new Coin("COIN.png", 8, this);
            coinPool.add(coin);
            layeredPane.add(coin, Integer.valueOf(3));
        }
    }

    // Retrieves inactive obstacle car from pool
    private ObstacleCar getAvailableObstacle() {
        for (ObstacleCar obs : obstaclePool) {
            if (!obs.isActive()) return obs;
        }
        return null;
    }

    // Retrieves inactive coin from pool
    private Coin getAvailableCoin() {
        for (Coin c : coinPool) {
            if (!c.isActive()) return c;
        }
        return null;
    }

    // Initializes main game loop
    private void initMasterGameLoop() {
        gameLoopTimer = new Timer(GAME_TICK_MS, e -> {
            if (isGameOver || isWin) return;

            tickCount++;

            trackLabel.updateScroll();

            updatePlayerPosition();

            for (ObstacleCar obs : obstaclePool) {
                obs.updatePosition();
            }
            policeCar.updatePosition();

            for (Coin c : coinPool) {
                c.updatePosition();
            }

            if (selectedLevel == 2) {
                for (Hole h : holePool) {
                    h.updatePosition();
                }
            }

            if (tickCount % 6 == 0) {
                addScore(54);
            }

            if (tickCount % 75 == 0 && !readyForFinishLine && !finishLineSpawned) {
                handleObstacleSpawning();
            }

            if (tickCount % 90 == 0 && !readyForFinishLine && !finishLineSpawned) {
                trySpawnCoin();
            }

            if (selectedLevel == 2 && tickCount % 120 == 0 && !readyForFinishLine && !finishLineSpawned) {
                trySpawnHole();
            }

            if (policeReadyToSpawn && !policeCar.isActive()) {
                trySpawnPolice();
            }

            if (readyForFinishLine && !finishLineSpawned) {
                if (isFinishLineAreaClear()) {
                    finishLineSpawned = true;
                    finishLineLabel.setVisible(true);
                }
            }

            if (finishLineSpawned) {
                finishLineY += 3;
                finishLineLabel.setLocation(finishLineX, finishLineY);

                Rectangle carBounds = carPanel.getBounds();
                Rectangle finishBounds = finishLineLabel.getBounds();
                if (carBounds.intersects(finishBounds)) {
                    winGame();
                }
            }
        });
        gameLoopTimer.start();
    }

    // Spawns hole in safe lane for level 2
    private void trySpawnHole() {
        Hole availableHole = getAvailableHole();
        if (availableHole == null) return;

        int[] lanesX = getLaneX();
        List<Integer> safeLanes = new ArrayList<>();

        for (int laneX : lanesX) {
            if (isSafeToSpawnHole(laneX)) {
                safeLanes.add(laneX);
            }
        }

        if (!safeLanes.isEmpty()) {
            int randomLaneX = safeLanes.get(random.nextInt(safeLanes.size()));
            int holeX = randomLaneX + (CAR_WIDTH - 110) / 2;
            availableHole.spawn(holeX, -110);
        }
    }

    // Checks if lane is safe for spawning hole
    private boolean isSafeToSpawnHole(int laneX) {
        int holeX = laneX + (CAR_WIDTH - 110) / 2;

        for (ObstacleCar obs : obstaclePool) {
            if (obs.isActive() && obs.getX() == laneX && obs.getY() >= -CAR_HEIGHT && obs.getY() <= 350) {
                return false;
            }
        }

        for (Coin c : coinPool) {
            if (c.isActive() && Math.abs(c.getX() - holeX) < 60 && c.getY() >= -120 && c.getY() <= 350) {
                return false;
            }
        }

        for (Hole h : holePool) {
            if (h.isActive() && h.getX() == holeX && h.getY() >= -110 && h.getY() <= 350) {
                return false;
            }
        }

        return true;
    }

    // Spawns coin in safe lane
    private void trySpawnCoin() {
        Coin availableCoin = getAvailableCoin();
        if (availableCoin == null) return;

        int[] lanesX = getLaneX();
        List<Integer> safeLanes = new ArrayList<>();

        for (int laneX : lanesX) {
            if (isSafeToSpawnCoin(laneX)) {
                safeLanes.add(laneX);
            }
        }

        if (!safeLanes.isEmpty()) {
            int randomLaneX = safeLanes.get(random.nextInt(safeLanes.size()));
            int coinX = randomLaneX + (CAR_WIDTH - 120) / 2;
            availableCoin.spawn(coinX, -116);
        }
    }

    // Checks if lane is safe for spawning coin
    private boolean isSafeToSpawnCoin(int laneX) {
        for (ObstacleCar obs : obstaclePool) {
            if (obs.isActive() && obs.getX() == laneX && obs.getY() >= -CAR_HEIGHT && obs.getY() <= 350) {
                return false;
            }
        }

        if (policeCar.isActive() && policeCar.getX() == laneX && policeCar.getY() >= -CAR_HEIGHT && policeCar.getY() <= 350) {
            return false;
        }

        return true;
    }

    // Spawns obstacle car in safe lane
    private void handleObstacleSpawning() {
        int[] lanesX = getLaneX();
        List<Integer> safeLanes = new ArrayList<>();
        for (int laneX : lanesX) {
            if (isSafeToSpawn(laneX, lanesX)) {
                safeLanes.add(laneX);
            }
        }

        if (!safeLanes.isEmpty()) {
            ObstacleCar availableCar = getAvailableObstacle();
            if (availableCar != null) {
                int randomLane = safeLanes.get(random.nextInt(safeLanes.size()));
                availableCar.spawn(randomLane, -CAR_HEIGHT);
            }
        }
    }

    // Spawns police car in available lane
    private void trySpawnPolice() {
        int[] lanesX = getLaneX();
        List<Integer> clearLanes = new ArrayList<>();

        for (int laneX : lanesX) {
            if (isPoliceLaneClear(laneX)) {
                clearLanes.add(laneX);
            }
        }

        if (!clearLanes.isEmpty()) {
            int randomLane = clearLanes.get(random.nextInt(clearLanes.size()));
            policeCar.spawn(randomLane);
            policeReadyToSpawn = false;
        }
    }

    // Checks if lane is clear for police car
    private boolean isPoliceLaneClear(int laneX) {
        for (ObstacleCar obs : obstaclePool) {
            if (obs.isActive() && obs.getX() == laneX) {
                return false;
            }
        }
        return true;
    }

    // Increments game score up to 100000 and triggers police spawn every 10000 pts
    public void addScore(int points) {
        if (isGameOver || isWin) return;

        int oldScore = score;
        score = Math.min(score + points, 100000);
        scoreLabel.setText("SCORE: " + score);
        progressGauge.setScore(score);

        if (score > 0 && (score / 10000) > (oldScore / 10000)) {
            if (!policeCar.isActive()) {
                policeReadyToSpawn = true;
            }
        }

        if (score >= 100000 && !readyForFinishLine && !finishLineSpawned) {
            readyForFinishLine = true;
        }
    }

    // Checks if top lane area is clear for finish line
    private boolean isFinishLineAreaClear() {
        for (ObstacleCar obs : obstaclePool) {
            if (obs.isActive() && obs.getY() >= -CAR_HEIGHT && obs.getY() <= 250) {
                return false;
            }
        }
        if (policeCar.isActive() && policeCar.getY() >= -CAR_HEIGHT && policeCar.getY() <= 250) {
            return false;
        }
        return true;
    }

    // Smoothly moves player car
    private void updatePlayerPosition() {
        int currentX = carPanel.getX();
        int currentY = carPanel.getY();
        int step = speed;

        int nextX = currentX;
        if (Math.abs(currentX - targetX) <= step) {
            nextX = targetX;
        } else if (currentX < targetX) {
            nextX = currentX + step;
        } else {
            nextX = currentX - step;
        }

        int nextY = currentY;
        if (Math.abs(currentY - targetY) <= step) {
            nextY = targetY;
        } else if (currentY < targetY) {
            nextY = currentY + step;
        } else {
            nextY = currentY - step;
        }

        if (nextX != currentX || nextY != currentY) {
            carPanel.setLocation(nextX, nextY);
        }
    }

    // Checks if target lane is safe for spawning obstacle
    private boolean isSafeToSpawn(int targetLaneX, int[] allLanes) {
        int minVerticalGap = 320;

        for (ObstacleCar obs : obstaclePool) {
            if (obs.isActive()
                    && obs.getX() == targetLaneX
                    && obs.getY() < minVerticalGap) {
                return false;
            }
        }

        int blockedLanes = 0;
        for (int laneX : allLanes) {
            if (laneX == targetLaneX) {
                blockedLanes++;
                continue;
            }
            for (ObstacleCar obs : obstaclePool) {
                if (obs.isActive()
                        && obs.getX() == laneX
                        && obs.getY() >= -CAR_HEIGHT
                        && obs.getY() <= 250) {
                    blockedLanes++;
                    break;
                }
            }
        }

        if (blockedLanes >= 3) return false;

        int playerX = carPanel.getX();
        int leftEdgeLane = allLanes[0];
        int rightEdgeLane = allLanes[allLanes.length - 1];

        if (Math.abs(playerX - leftEdgeLane) < 50) {
            if (targetLaneX == allLanes[1] && isLaneBlockedNearTop(leftEdgeLane)) {
                return false;
            }
            if (targetLaneX == leftEdgeLane && isLaneBlockedNearTop(allLanes[1])) {
                return false;
            }
        }

        if (Math.abs(playerX - rightEdgeLane) < 50) {
            if (targetLaneX == allLanes[2] && isLaneBlockedNearTop(rightEdgeLane)) {
                return false;
            }
            if (targetLaneX == rightEdgeLane && isLaneBlockedNearTop(allLanes[2])) {
                return false;
            }
        }

        return true;
    }

    // Checks if top area of lane is blocked
    private boolean isLaneBlockedNearTop(int laneX) {
        for (ObstacleCar obs : obstaclePool) {
            if (obs.isActive()
                    && obs.getX() == laneX
                    && obs.getY() >= -CAR_HEIGHT
                    && obs.getY() <= 400) {
                return true;
            }
        }
        return false;
    }

    // Triggers win state
    public void winGame() {
        if (isWin || isGameOver) return;
        isWin = true;

        if (gameLoopTimer != null) gameLoopTimer.stop();
        stopBGM();

        ImageIcon winIcon = loadScaledIcon("Win.png", 825, 289, Image.SCALE_SMOOTH);
        winLabel = new JLabel(winIcon);
        winLabel.setBounds(547, 120, 825, 289);
        layeredPane.add(winLabel, Integer.valueOf(5));

        ImageIcon returnIcon = loadScaledIcon("ReturnToMain.png", 550, 173, Image.SCALE_SMOOTH);
        restartBtn = new JButton(returnIcon);
        restartBtn.setBounds(685, 450, 550, 173);
        restartBtn.setOpaque(false);
        restartBtn.setContentAreaFilled(false);
        restartBtn.setBorderPainted(false);
        restartBtn.setFocusPainted(false);
        restartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        restartBtn.addActionListener(e -> {
            stopBGM();
            new TitleGUI(fr);
        });

        layeredPane.add(restartBtn, Integer.valueOf(5));
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // Triggers game over state
    public void gameOver(int smokeX, int smokeY) {
        if (isGameOver || isWin) return;
        isGameOver = true;

        if (gameLoopTimer != null) gameLoopTimer.stop();
        stopBGM();

        ImageIcon smokeIcon = new ImageIcon("asset/EXPLOSION.gif");
        smokeLabel = new JLabel(smokeIcon);
        smokeLabel.setBounds(smokeX, smokeY, 140, 133);
        layeredPane.add(smokeLabel, Integer.valueOf(4));

        gameOverLabel = new JLabel(new ImageIcon("asset/GameOver.png"));
        gameOverLabel.setBounds(445, 100, 1030, 330);
        layeredPane.add(gameOverLabel, Integer.valueOf(5));

        createRestartButton();
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // Creates restart button
    private void createRestartButton() {
        Image scaledRestart = new ImageIcon("asset/Restart.png")
                .getImage().getScaledInstance(455, 142, Image.SCALE_SMOOTH);

        restartBtn = new JButton(new ImageIcon(scaledRestart));
        restartBtn.setBounds(732, 470, 455, 142);
        restartBtn.setOpaque(false);
        restartBtn.setContentAreaFilled(false);
        restartBtn.setBorderPainted(false);
        restartBtn.setFocusPainted(false);
        restartBtn.addActionListener(e -> restartGame());

        layeredPane.add(restartBtn, Integer.valueOf(5));
    }

    // Resets game state
    public void restartGame() {
        removeIfPresent(smokeLabel);
        removeIfPresent(gameOverLabel);
        removeIfPresent(winLabel);
        removeIfPresent(restartBtn);

        smokeLabel = null;
        gameOverLabel = null;
        winLabel = null;
        restartBtn = null;

        score = 0;
        tickCount = 0;
        scoreLabel.setText("SCORE: 0");
        progressGauge.setScore(0);

        for (ObstacleCar obs : obstaclePool) {
            obs.reset();
        }
        for (Coin c : coinPool) {
            c.reset();
        }
        if (selectedLevel == 2) {
            for (Hole h : holePool) {
                h.reset();
            }
        }
        policeCar.reset();

        readyForFinishLine = false;
        finishLineSpawned = false;
        finishLineY = -171;
        finishLineLabel.setLocation(finishLineX, finishLineY);
        finishLineLabel.setVisible(false);

        int startX = 905;
        int startY = 800;
        carPanel.setLocation(startX, startY);
        targetX = startX;
        targetY = startY;

        isGameOver = false;
        isWin = false;
        policeReadyToSpawn = false;

        playBGM("bgm.wav");
        gameLoopTimer.start();

        layeredPane.revalidate();
        layeredPane.repaint();
        SwingUtilities.invokeLater(() -> layeredPane.requestFocusInWindow());
    }

    // Removes UI
    private void removeIfPresent(Component component) {
        if (component != null) {
            layeredPane.remove(component);
        }
    }

    // Sets target movement position
    public void moveTo(int newTargetX, int newTargetY) {
        if (isGameOver || isWin) return;
        targetX = newTargetX;
        targetY = newTargetY;
    }

    // Returns whether game is over or won
    public boolean isGameOver() {
        return isGameOver || isWin;
    }

    // Returns player target X position
    public int getTargetX() {
        return targetX;
    }

    // Returns player target Y position
    public int getTargetY() {
        return targetY;
    }
}