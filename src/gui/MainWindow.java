package gui;

import model.Game;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            this.add(guiBoard);

            JButton undoButton = new JButton("Undo");
            undoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.getBoard().undoMove();
                    guiBoard.repaint();
                }
            });

            this.add(undoButton,"East");

            this.setVisible(true);
        }
    }


}
