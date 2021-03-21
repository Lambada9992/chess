package gui;

import model.Board;
import model.Game;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

public class GuiBoard extends JPanel {
    private Game game;
    private ArrayList<GuiPiece> guiPieces = new ArrayList<>();
    private boolean reverseMode = false;

    private Color tileColor1 = new Color(237, 237, 209);
    private Color tileColor2 = new Color(118, 149, 86);
    private Color tipColor = new Color(180,180,180,180);

    private GuiPiece chosenPiece = null;
    private Point cursorPosition = new Point(0,0);

    public GuiBoard(Game game) {
        this.setPreferredSize(new Dimension(300,300));
        this.game = game;

        this.addMouseListener(new ClickListener());
        this.addMouseMotionListener(new DragListener());
    }

    public void updateGuiPieces(){
        ArrayList<Piece> pieces = game.getBoard().getPieces();
        guiPieces.clear();
        for (Piece piece:pieces) {
            this.guiPieces.add(new GuiPiece(piece));
        }
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        reverseMode = game.getPlayerColor()!=Piece.Color.BLACK? false : true;

        //board painting
        g.setColor(tileColor1);
        g.fillRect(0,0,getWidth(),getHeight());
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++) {

                if(!reverseMode) {
                    if ((i + j) % 2 != 0) {
                        g.setColor(tileColor2);
                        g.fillRect(j * getWidth() / 8, i * getHeight() / 8, getWidth() / 8, getHeight() / 8);
                    }
                }else {
                    if ((i + j) % 2 == 0) {
                        g.setColor(tileColor2);
                        g.fillRect(j * getWidth() / 8, i * getHeight() / 8, getWidth() / 8, getHeight() / 8);
                    }
                }

            }
        }

        //Chosen piece Tips
        if(chosenPiece!=null){
            g.setColor(tipColor);
            HashSet<Integer> tips = chosenPiece.getPiece().getAvailableMoves(true);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            for(int tip : tips){
                int tipReversed = (game.getBoard().boardSize * game.getBoard().boardSize)-1 -tip;

                int x = reverseMode?tipReversed:tip;
                x = x%game.getBoard().boardSize;
                int y = reverseMode?tipReversed:tip;
                y -= x;
                y /= game.getBoard().boardSize;

                x*=getWidth()/8;
                y*=getHeight()/8;

                if(game.getBoard().getPiece(tip)==null){
                    g.fillOval(x+getWidth()/32,y+getHeight()/32,getWidth()/16,getHeight()/16);
                }else {
                    g2d.drawOval(x, y, getWidth() / 8, getHeight() / 8);
                }
            }
        }

        //Piece Painting
        for (GuiPiece guiPiece:guiPieces) {
            if(guiPiece==chosenPiece) continue;
            if(guiPiece.getPiece().getIsDead())continue;

            int position = guiPiece.getPiece().getPosition();

            position = reverseMode?((game.getBoard().boardSize*game.getBoard().boardSize)-1)-position:position;

            int x = position%game.getBoard().boardSize;
            int y = (position-x)/game.getBoard().boardSize;
            x*=getWidth()/8;
            y*=getHeight()/8;
            g.drawImage(guiPiece.getPieceImage(),x,y,getWidth()/8,getHeight()/8,null);
        }

        //chosen piece painting
        if(chosenPiece!=null){
            g.drawImage(chosenPiece.getPieceImage(),
                    cursorPosition.x-getWidth()/16, cursorPosition.y-getHeight()/16,
                    getWidth()/8,getHeight()/8,null);
        }

    }

    private class ClickListener extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            x /= GuiBoard.this.getWidth()/8;
            y /= GuiBoard.this.getHeight()/8;

            x = reverseMode?game.getBoard().boardSize-1-x:x;
            y = reverseMode?game.getBoard().boardSize-1-y:y;


            for(GuiPiece guiPiece : GuiBoard.this.guiPieces){
                if (guiPiece.getPiece().getPosition() == y*8 + x && guiPiece.getPiece().getIsDead()==false) {
                    GuiBoard.this.chosenPiece = guiPiece;
                    cursorPosition = e.getPoint();
                    break;
                }
            }
            GuiBoard.this.repaint();

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(chosenPiece==null) return;
            int x = e.getX()/(GuiBoard.this.getWidth()/8);
            int y = e.getY()/(GuiBoard.this.getHeight()/8);

            x = reverseMode?game.getBoard().boardSize-1-x:x;
            y = reverseMode?game.getBoard().boardSize-1-y:y;

            if(chosenPiece.getPiece().getPosition()!= y*8 +x) {
                GuiBoard.this.game.move(chosenPiece.getPiece(), y * 8 + x,true);
            }
            chosenPiece = null;
            GuiBoard.this.repaint();
        }
    }

    private class DragListener extends MouseMotionAdapter{
        @Override
        public void mouseDragged(MouseEvent e) {
            if (chosenPiece!=null) {
                cursorPosition = e.getPoint();
                GuiBoard.this.repaint();
            }
        }
    }

}
