import javax.swing.*;

public class applaucher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new weatherappgui();

            }
        });
    }
}
