package gui;

import model.Game;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private Game game;

    private MainMenuPanel mmp = new MainMenuPanel();
    private GamePanel gp;

    public MainWindow (String title,Game game){
        super(title);
        this.game = game;
        this.gp = new GamePanel(game);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setLocationRelativeTo(null);
        this.setSize(500,500);

        this.add(new GamePanel(game));

        this.setVisible(true);
    }



    private class MainMenuPanel extends JPanel{


    }

    private class GamePanel extends JPanel{
        private Game game;
        private GuiBoard guiBoard;

        GamePanel(Game game){
            this.game = game;
            guiBoard = new GuiBoard(game);
            this.setLayout(new BorderLayout());
            //this.setBounds(0,0,300,300);
            this.add(guiBoard);
            this.setVisible(true);
        }
    }


}
