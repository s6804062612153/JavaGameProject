import javax.swing.SwingUtilities;

public class Driver {
    // Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TitleGUI::new);
    }
}