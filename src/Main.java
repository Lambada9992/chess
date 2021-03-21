import gui.ImageLoader;
import gui.MainWindow;
import model.Game;

import javax.swing.*;

/**
 * The Chess game
 *
 * @author Marcin Bobiński (Lambada9992)
 * @version 1.0
 */
public class Main {
    private static MainWindow mainWindow;
    private static Game game;

    public static void main(String[] args){
        try {
            ImageLoader.getInstance().setPiecesImage("Images/ChessPiecesArray.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ImageLoader.getInstance().setLogoImage("Images/Logo.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        game = new Game();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //JFrame.setDefaultLookAndFeelDecorated(true);
                mainWindow = new MainWindow("Chess by Marcin Bobiński",game);
            }
        });


    }
}
