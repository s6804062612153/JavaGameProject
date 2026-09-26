import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CarHandler {
    private final CarGUI C;

    // Constructor
    public CarHandler(CarGUI ref) {
        this.C = ref;
    }

    // Installs WASD and Arrow key bindings
    public void installKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        // WASD Key Bindings
        bind(inputMap, actionMap, "pressed A", KeyEvent.VK_A, "moveLeft");
        bind(inputMap, actionMap, "pressed D", KeyEvent.VK_D, "moveRight");
        bind(inputMap, actionMap, "pressed W", KeyEvent.VK_W, "moveUp");
        bind(inputMap, actionMap, "pressed S", KeyEvent.VK_S, "moveDown");

        // Arrow Key Bindings
        bind(inputMap, actionMap, "pressed LEFT", KeyEvent.VK_LEFT, "moveLeft");
        bind(inputMap, actionMap, "pressed RIGHT", KeyEvent.VK_RIGHT, "moveRight");
        bind(inputMap, actionMap, "pressed UP", KeyEvent.VK_UP, "moveUp");
        bind(inputMap, actionMap, "pressed DOWN", KeyEvent.VK_DOWN, "moveDown");
    }

    // Binds key action
    private void bind(InputMap inputMap, ActionMap actionMap,
                      String keyStroke, int keyCode, String actionName) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyStroke);
        inputMap.put(stroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move(keyCode);
            }
        });
    }

    // Calculates and sets position
    private void move(int key) {
        if (C.isGameOver()) return;

        int minLeftX = C.trackLabel.getX() + 68;
        int maxRightX = C.trackLabel.getX()
                + C.trackLabel.getWidth()
                - C.carPanel.getWidth()
                - 68;

        int minY = C.trackLabel.getY();
        int maxY = C.trackLabel.getY()
                + C.trackLabel.getHeight()
                - C.carPanel.getHeight();

        int nextTargetX = C.getTargetX();
        int nextTargetY = C.getTargetY();

        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            nextTargetX = Math.max(minLeftX, nextTargetX - 138);
        } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            nextTargetX = Math.min(maxRightX, nextTargetX + 138);
        } else if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
            nextTargetY = Math.max(minY, nextTargetY - 138);
        } else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
            nextTargetY = Math.min(maxY, nextTargetY + 138);
        }

        C.moveTo(nextTargetX, nextTargetY);
    }
}