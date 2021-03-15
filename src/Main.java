import gui.ImageLoader;
import gui.MainWindow;
import model.Game;

import javax.swing.*;

/**
 * @author Marcin Bobiński (Lambada9992)
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
