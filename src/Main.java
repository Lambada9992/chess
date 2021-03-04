import gui.MainWindow;
import model.Game;

import javax.swing.*;

/**
 * @author Marcin Bobiński (Lambada9992)
 */

public class Main {

    public static void main(String[] args) throws InterruptedException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                new MainWindow();
            }
        });

        Game game = new Game();
    }
}
