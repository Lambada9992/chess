package gui;

import model.Game;
import model.Piece;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class MainWindow extends JFrame {
    private JPanel mainPanel = new JPanel();
    private CardLayout cl = new CardLayout();

    static public final Color backgroundColor = new Color(49, 46, 43);
    static public final Color buttonColor = new Color(65, 62, 60);
    static public final Color textButtonColor = new Color(255,255,255);

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
                MainWindow.this.game.beforeClosing();
                System.exit(0);
            }
        });
        this.setSize(700,500);
        this.setLocationByPlatform(true);
        this.add(mainPanel);
        mainPanel.setLayout(cl);

        this.game = game;
        game.setDisplayGameObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                MainWindow.this.gp.getGuiBoard().updateGuiPieces();
                MainWindow.this.gp.updateLabelsAndButtons();
                MainWindow.this.game.setState(Game.State.RUNNING);
                MainWindow.this.cl.show(MainWindow.this.mainPanel,"game");
            }
        });
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
            setBackground(MainWindow.backgroundColor);
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
                    MainWindow.this.gp.updateLabelsAndButtons();
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


            title.setForeground(MainWindow.textButtonColor);
            lanButton.setBackground(MainWindow.buttonColor);
            lanButton.setForeground(MainWindow.textButtonColor);
            multiPlayerButton.setBackground(MainWindow.buttonColor);
            multiPlayerButton.setForeground(MainWindow.textButtonColor);
            exitButton.setBackground(MainWindow.buttonColor);
            exitButton.setForeground(MainWindow.textButtonColor);

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
            setBackground(MainWindow.backgroundColor);
            this.setLayout(new GridLayout(6,1,30,30));

            ipField.setBackground(MainWindow.buttonColor);
            ipField.setForeground(MainWindow.textButtonColor);
            hostButton.setBackground(MainWindow.buttonColor);
            hostButton.setForeground(MainWindow.textButtonColor);
            joinButton.setBackground(MainWindow.buttonColor);
            joinButton.setForeground(MainWindow.textButtonColor);
            mainMenuButton.setBackground(MainWindow.buttonColor);
            mainMenuButton.setForeground(MainWindow.textButtonColor);

            hostButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainWindow.this.game.setMode(Game.Mode.HOST);
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

            //preferred color
            ButtonGroup preferredColorGroup = new ButtonGroup();
            JRadioButton noneButton = new JRadioButton("None",true);
            noneButton.setBackground(MainWindow.backgroundColor);
            noneButton.setForeground(MainWindow.textButtonColor);
            JRadioButton whiteButton = new JRadioButton("White");
            whiteButton.setBackground(MainWindow.backgroundColor);
            whiteButton.setForeground(MainWindow.textButtonColor);
            JRadioButton blackButton = new JRadioButton("Black");
            blackButton.setBackground(MainWindow.backgroundColor);
            blackButton.setForeground(MainWindow.textButtonColor);

            preferredColorGroup.add(noneButton);
            preferredColorGroup.add(whiteButton);
            preferredColorGroup.add(blackButton);

            JPanel preferredColorPanel = new JPanel();
            preferredColorPanel.setBackground(MainWindow.backgroundColor);
            preferredColorPanel.setLayout(new FlowLayout());
            preferredColorPanel.add(noneButton);
            preferredColorPanel.add(whiteButton);
            preferredColorPanel.add(blackButton);

            noneButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.setPreferredColor(null);
                }
            });
            whiteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.setPreferredColor(Piece.Color.WHITE);
                }
            });
            blackButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.setPreferredColor(Piece.Color.BLACK);
                }
            });


            add(statusLabel);
            add(ipField);
            add(joinButton);
            add(hostButton);
            add(preferredColorPanel);
            add(mainMenuButton);
        }
    }

    private class GamePanel extends JPanel{
        private Game game;
        private GuiBoard guiBoard;
        private JLabel whoseMove;
        private JLabel yourMove;
        private JButton restartButton;
        private JButton surrenderButton;

        GamePanel(Game game){
            setBackground(MainWindow.backgroundColor);
            this.game = game;
            guiBoard = new GuiBoard(game);
            game.setUpdateBoardObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    updateLabelsAndButtons();
                    guiBoard.repaint();
                }
            });
            this.setLayout(new BorderLayout());
            this.add(guiBoard);


            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout(10,10));
            rightPanel.setBackground(MainWindow.backgroundColor);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridLayout(2,1));
            infoPanel.setBackground(MainWindow.backgroundColor);
            rightPanel.add(infoPanel,BorderLayout.NORTH);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new GridLayout(3,1));
            buttonsPanel.setBackground(MainWindow.backgroundColor);
            rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

            //info panel
            Font font1 = new Font(Font.SERIF, Font.PLAIN,  20);
            whoseMove = new JLabel("",JLabel.CENTER);
            whoseMove.setFont(font1);
            whoseMove.setForeground(Color.WHITE);
            yourMove = new JLabel("YOUR MOVE!!!",JLabel.CENTER);
            yourMove.setVisible(false);
            yourMove.setFont(font1);
            infoPanel.add(whoseMove);
            infoPanel.add(yourMove);


            //buttons panel
            restartButton = new JButton("Restart");
            restartButton.setForeground(MainWindow.textButtonColor);
            restartButton.setBackground(MainWindow.buttonColor);
            restartButton.setVisible(false);
            surrenderButton = new JButton("Surrender");
            surrenderButton.setForeground(MainWindow.textButtonColor);
            surrenderButton.setBackground(MainWindow.buttonColor);
            surrenderButton.setVisible(false);
            JButton mainMenuButton = new JButton("MainMenu");
            mainMenuButton.setForeground(MainWindow.textButtonColor);
            mainMenuButton.setBackground(MainWindow.buttonColor);

            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.restart();
                    restartButton.setVisible(false);
                }
            });
            surrenderButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int result = JOptionPane.showConfirmDialog(MainWindow.this,
                            "Sure? You want to Surrender?", "Surrender",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if(result == JOptionPane.YES_OPTION)game.surrender();
                }
            });
            mainMenuButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MainWindow.this.game.setMode(Game.Mode.NONE);
                    MainWindow.this.cl.show(MainWindow.this.mainPanel,"mainMenu");
                }
            });
            buttonsPanel.add(restartButton);
            buttonsPanel.add(surrenderButton);
            buttonsPanel.add(mainMenuButton);

            this.add(rightPanel,BorderLayout.EAST);
        }

        public void updateLabelsAndButtons(){
            if(game.getCurrentMove()==Piece.Color.WHITE){
                whoseMove.setText("TURN: WHITE");
            }else{
                whoseMove.setText("TURN: BLACK");
            }
            if(game.getCurrentMove()==game.getPlayerColor()){
                yourMove.setVisible(true);
            }else {
                yourMove.setVisible(false);
            }

            if(game.getState() == Game.State.ENDED){
                Piece.Color color = game.getWinnerColor();
                if(color!=null) {
                    String text;
                    text = color==Piece.Color.WHITE? "WHITE":"BLACK";
                    text+=" WON!!!";
                    JLabel label = new JLabel(text,JLabel.CENTER);
                    JOptionPane.showMessageDialog(MainWindow.this,label,"END OF THE GAME",JOptionPane.PLAIN_MESSAGE);
                }
                if(game.getMode() == Game.Mode.LAN || game.getMode() == Game.Mode.HOST){
                    restartButton.setVisible(true);
                }
            }else {
                restartButton.setVisible(false);
            }


            if(game.getMode()== Game.Mode.HOST || game.getMode()== Game.Mode.JOIN){
                surrenderButton.setVisible(true);
            }else {
                surrenderButton.setVisible(false);
            }
        }


        public GuiBoard getGuiBoard() {
            return guiBoard;
        }
    }


}
