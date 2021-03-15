package gui;

import com.sun.org.apache.xpath.internal.operations.Mod;
import model.Game;
import model.Piece;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    private JPanel mainPanel = new JPanel();
    private CardLayout cl = new CardLayout();

    private MainMenuPanel mmp = new MainMenuPanel();
    private MultiPlayerPanel mpp = new MultiPlayerPanel();
    private GamePanel gp;

    private Game game;

    public MainWindow (String title,Game game){
        super(title);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.this.game.setMode(Game.Mode.NONE);
                System.exit(0);
            }
        });
        this.setSize(500,500);
        this.setLocationByPlatform(true);
        this.add(mainPanel);
        mainPanel.setLayout(cl);

        this.game = game;
        this.gp = new GamePanel(game);

        mainPanel.add(mmp,"mainMenu");
        mainPanel.add(mpp,"multiPlayer");
        mainPanel.add(gp,"game");

        this.setVisible(true);
    }

    private class MainMenuPanel extends JPanel{

        private Font font = new Font("TimesRoman", Font.PLAIN, 30);
        private JLabel title = new JLabel("Chess",JLabel.CENTER);
        private JButton lanButton = new JButton("Lan");
        private JButton multiPlayerButton = new JButton("multiPlayer");
        private JButton exitButton = new JButton("Exit");

        public MainMenuPanel() {
            setLayout(new GridLayout(4,1,40,40));

            title.setFont(font);
            title.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {

                }
            });

            lanButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.setMode(Game.Mode.LAN);
                    MainWindow.this.gp.getGuiBoard().updateGuiPieces();
                    //MainWindow.this.gp.getGuiBoard().repaint();
                    MainWindow.this.cl.show(MainWindow.this.mainPanel,"game");

                }
            });

            multiPlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.setMode(Game.Mode.NONE);
                    MainWindow.this.cl.show(MainWindow.this.mainPanel,"multiPlayer");
                }
            });

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.setMode(Game.Mode.NONE);
                    System.exit(0);
                }
            });

            add(title);
            add(lanButton);
            add(multiPlayerButton);
            add(exitButton);

            CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(20,20,20,20),null);
            this.setBorder(cb);
        }
    }

    private class MultiPlayerPanel extends JPanel{
        private JLabel statusLabel = new JLabel();
        private JTextField ipField = new JTextField("127.0.0.1");
        private JButton hostButton = new JButton("Host");
        private JButton joinButton = new JButton("Join");
        private JButton mainMenuButton = new JButton("Main Menu");


        public MultiPlayerPanel() {
            this.setLayout(new GridLayout(5,1,30,30));

            hostButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainWindow.this.game.setMode(Game.Mode.JOIN);
                    MainWindow.this.game.startServer();
                }
            });

            joinButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainWindow.this.game.setMode(Game.Mode.JOIN);
                    try {
                        MainWindow.this.game.connect(ipField.getText());
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(new JFrame(), "Podano błędne IP", "IP error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.game.setMode(Game.Mode.NONE);
                    }
                }
            });

            mainMenuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainWindow.this.game.setMode(Game.Mode.NONE);
                    MainWindow.this.cl.show(MainWindow.this.mainPanel,"mainMenu");
                }
            });

            add(statusLabel);
            add(ipField);
            add(joinButton);
            add(hostButton);
            add(mainMenuButton);
        }
    }

    private class GamePanel extends JPanel{
        private Game game;
        private GuiBoard guiBoard;

        GamePanel(Game game){
            this.game = game;
            guiBoard = new GuiBoard(game);
            this.setLayout(new BorderLayout());
            this.add(guiBoard);


            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new GridLayout(2,1,30,30));

//            JButton undoButton = new JButton("Undo");
//            undoButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    game.getBoard().undoMove();
//                    guiBoard.repaint();
//                }
//            });
//            rightPanel.add(undoButton);

            JButton mainMenuButton = new JButton("MainMenu");
            mainMenuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainWindow.this.game.setMode(Game.Mode.NONE);
                    MainWindow.this.cl.show(MainWindow.this.mainPanel,"mainMenu");
                }
            });
            rightPanel.add(mainMenuButton);


            this.add(rightPanel,"East");

            this.setVisible(true);
        }

        public GuiBoard getGuiBoard() {
            return guiBoard;
        }
    }


}
