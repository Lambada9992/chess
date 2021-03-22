package gui;

import model.Game;
import model.Piece;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

/**
 * A class that represent window of the app
 */
public class MainWindow extends JFrame {
    private JPanel mainPanel = new JPanel();
    private CardLayout cl = new CardLayout();

    static public final Color backgroundColor = new Color(49, 46, 43);
    static public final Color buttonColor = new Color(65, 62, 60);
    static public final Color textColor = new Color(255,255,255);

    private MainMenuPanel mmp = new MainMenuPanel();
    private MultiPlayerPanel mpp;
    private GamePanel gp;

    private Game game;

    /**
     * Constructor
     * @param title Title of the window
     * @param game Reference to the game
     */
    public MainWindow (String title,Game game){
        super(title);
        this.setIconImage(ImageLoader.getInstance().getLogoImage());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.this.game.setMode(Game.Mode.NONE);
                MainWindow.this.game.beforeClosing();
                System.exit(0);
            }
        });
        this.setSize(650,500);
        this.setMinimumSize(new Dimension(520,400));
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
        this.mpp = new MultiPlayerPanel(game);

        mainPanel.add(mmp,"mainMenu");
        mainPanel.add(mpp,"multiPlayer");
        mainPanel.add(gp,"game");

        this.setVisible(true);
    }

    /**
     * A class that represents the Main menu panel(view)
     */
    private class MainMenuPanel extends JPanel{

        private Font font = new Font("TimesRoman", Font.PLAIN, 30);
        private JLabel title = new JLabel("Chess",JLabel.CENTER);
        private JButton lanButton = new JButton("Lan");
        private JButton multiPlayerButton = new JButton("multiPlayer");
        private JButton exitButton = new JButton("Exit");

        /**
         * Constructor
         */
        public MainMenuPanel() {
            setBackground(MainWindow.backgroundColor);
            setLayout(new BorderLayout(50,50));

            title.setFont(font);

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
                    MainWindow.this.mpp.update();
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


            title.setForeground(MainWindow.textColor);
            lanButton.setBackground(MainWindow.buttonColor);
            lanButton.setForeground(MainWindow.textColor);
            multiPlayerButton.setBackground(MainWindow.buttonColor);
            multiPlayerButton.setForeground(MainWindow.textColor);
            exitButton.setBackground(MainWindow.buttonColor);
            exitButton.setForeground(MainWindow.textColor);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setBackground(MainWindow.backgroundColor);
            buttonsPanel.setLayout(new GridLayout(3,3,0,50));
            buttonsPanel.setBorder(new CompoundBorder(
                    BorderFactory.createEmptyBorder(20,150,20,150),null));

            add(title,BorderLayout.NORTH);
            buttonsPanel.add(lanButton);
            buttonsPanel.add(multiPlayerButton);
            buttonsPanel.add(exitButton);
            add(buttonsPanel,BorderLayout.CENTER);

            CompoundBorder cb = new CompoundBorder(
                    BorderFactory.createEmptyBorder(50,20,20,20),null);
            this.setBorder(cb);
        }
    }

    /**
     * A class that represents the Multiplayer menu panel(view)
     */
    private class MultiPlayerPanel extends JPanel{
        private JLabel serverStatus = new JLabel("Server: OFF",JLabel.CENTER);
        private JLabel connectingStatus = new JLabel("Connecting...",JLabel.CENTER);
        private JTextField ipField = new JTextField("127.0.0.1",JTextField.CENTER);
        private JButton hostButton = new JButton("Host");
        private JButton joinButton = new JButton("Join");
        private JButton mainMenuButton = new JButton("Main Menu");


        /**
         * Constructor
         * @param game Reference to the game
         */
        public MultiPlayerPanel(Game game) {
            game.setConnectionObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    MultiPlayerPanel.this.update();
                }
            });

            //butons listeners
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

            //gui components
            ipField.setBackground(MainWindow.buttonColor);
            ipField.setForeground(MainWindow.textColor);
            hostButton.setBackground(MainWindow.buttonColor);
            hostButton.setForeground(MainWindow.textColor);
            joinButton.setBackground(MainWindow.buttonColor);
            joinButton.setForeground(MainWindow.textColor);
            mainMenuButton.setBackground(MainWindow.buttonColor);
            mainMenuButton.setForeground(MainWindow.textColor);
            serverStatus.setForeground(MainWindow.textColor);
            connectingStatus.setForeground(MainWindow.textColor);


            //preferred color
            ButtonGroup preferredColorGroup = new ButtonGroup();
            JRadioButton noneButton = new JRadioButton("None",true);
            noneButton.setBackground(MainWindow.backgroundColor);
            noneButton.setForeground(MainWindow.textColor);
            JRadioButton whiteButton = new JRadioButton("White");
            whiteButton.setBackground(MainWindow.backgroundColor);
            whiteButton.setForeground(MainWindow.textColor);
            JRadioButton blackButton = new JRadioButton("Black");
            blackButton.setBackground(MainWindow.backgroundColor);
            blackButton.setForeground(MainWindow.textColor);
            preferredColorGroup.add(noneButton);
            preferredColorGroup.add(whiteButton);
            preferredColorGroup.add(blackButton);

            JPanel preferredColorPanel = new JPanel();
            preferredColorPanel.setBackground(MainWindow.backgroundColor);
            preferredColorPanel.setLayout(new FlowLayout());
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
            preferredColorPanel.add(noneButton);
            preferredColorPanel.add(whiteButton);
            preferredColorPanel.add(blackButton);


            //adding everything to panel
            setBackground(MainWindow.backgroundColor);
            this.setLayout(new GridBagLayout());
            setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(60,20,20,20),null));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.ipadx = 40;
            gbc.ipady = 10;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(serverStatus,gbc);

            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(hostButton,gbc);

            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(connectingStatus,gbc);

            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(ipField,gbc);

            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(joinButton,gbc);

            JLabel preferredColorLabel = new JLabel("Preferred Color:",JLabel.CENTER);
            preferredColorLabel.setForeground(MainWindow.textColor);
            gbc.weighty = 0.5;
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.PAGE_END;
            add(preferredColorLabel,gbc);


            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(preferredColorPanel,gbc);


            gbc.gridy = 5;
            gbc.weighty = 0.5;
            add(mainMenuButton,gbc);

        }

        /**
         * Updates the labels on the panel
         */
        public void update(){
            if (game.getIsServerOn()){
                serverStatus.setText("Server: ON ");
            }else {
                serverStatus.setText("Server: OFF");
            }

            if (game.isConnecting()){
                connectingStatus.setForeground(MainWindow.textColor);
            }else {
                connectingStatus.setForeground(MainWindow.backgroundColor);
            }
        }
    }

    /**
     * A class that represents the Game panel(view)
     */
    private class GamePanel extends JPanel{
        private Game game;
        private GuiBoard guiBoard;
        private JLabel whoseMove;
        private JLabel yourMove;
        private JButton restartButton;
        private JButton surrenderButton;

        /**
         * Constructor
         * @param game Reference to the game
         */
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
            this.setLayout(new BorderLayout(30,0));
            this.setBorder(new CompoundBorder(
                    BorderFactory.createEmptyBorder(20,20,20,20),null));
            this.add(guiBoard);


            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout(10,10));
            rightPanel.setBackground(MainWindow.backgroundColor);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridLayout(2,1));
            infoPanel.setBackground(MainWindow.backgroundColor);
            rightPanel.add(infoPanel,BorderLayout.NORTH);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new GridLayout(3,1,0,20));
            buttonsPanel.setBackground(MainWindow.backgroundColor);
            rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

            //info panel
            Font font1 = new Font(Font.SERIF, Font.PLAIN,  20);
            whoseMove = new JLabel("",JLabel.CENTER);
            whoseMove.setFont(font1);
            whoseMove.setForeground(MainWindow.textColor);
            yourMove = new JLabel("YOUR MOVE!!!",JLabel.CENTER);
            yourMove.setVisible(false);
            yourMove.setFont(font1);
            yourMove.setForeground(MainWindow.textColor);
            infoPanel.add(whoseMove);
            infoPanel.add(yourMove);


            //buttons panel
            restartButton = new JButton("Restart");
            restartButton.setForeground(MainWindow.textColor);
            restartButton.setBackground(MainWindow.buttonColor);
            restartButton.setVisible(false);
            surrenderButton = new JButton("Surrender");
            surrenderButton.setForeground(MainWindow.textColor);
            surrenderButton.setBackground(MainWindow.buttonColor);
            surrenderButton.setVisible(false);
            JButton mainMenuButton = new JButton("MainMenu");
            mainMenuButton.setForeground(MainWindow.textColor);
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

        /**
         * Updates Gui(labels, displayed board, buttons)
         */
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
                game.resetWinner();
                if(color!=null) {
                    String text;
                    text = color==Piece.Color.WHITE? "WHITE":"BLACK";
                    text+=" WON!!!";
                    JLabel label = new JLabel(text,JLabel.CENTER);
                    JOptionPane.showMessageDialog(MainWindow.this,label,"END OF THE GAME",
                            JOptionPane.PLAIN_MESSAGE);
                }
                if(game.getMode() == Game.Mode.LAN || game.getMode() == Game.Mode.HOST){
                    restartButton.setVisible(true);
                }
            }else {
                restartButton.setVisible(false);
            }

            if(game.getMode() == Game.Mode.HOST || game.getMode() == Game.Mode.JOIN){
                surrenderButton.setVisible(true);
            }else {
                surrenderButton.setVisible(false);
            }
        }

        /**
         * @return The gui board
         */
        public GuiBoard getGuiBoard() {
            return guiBoard;
        }
    }

}
