package gui;

import model.Game;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GuiBoard extends JPanel {
    private Game game;
    private ArrayList<GuiPiece> guiPieces = null;

    public GuiBoard(Game game) {
        this.setPreferredSize(new Dimension(300,300));
        this.game = game;
        updateGuiPieces();
    }

    public void updateGuiPieces(){
        if(game==null) System.out.println("game");
        if(game.getBoard()==null) System.out.println("board");
        if(game.getBoard().getPieces()==null) System.out.println("pieces");

        ArrayList<Piece> pieces = game.getBoard().getPieces();
        guiPieces = new ArrayList<>();
        for (Piece piece:pieces) {
            guiPieces.add(new GuiPiece(piece));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics g2d = (Graphics2D) g;

        //board painting
        g2d.setColor(new Color(220,255,220));
        g2d.fillRect(0,0,getWidth(),getHeight());
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                if((i+j)%2!=0){
                    g2d.setColor(new Color(100,140,20));
                    g2d.fillRect(j*getWidth()/8,i*getHeight()/8,getWidth()/8,getHeight()/8);
                }
            }
        }

        //Piece Painting
        for (GuiPiece guiPiece:guiPieces) {
            int x = guiPiece.getPiece().getPosition()%game.getBoard().boardSize;
            int y = (guiPiece.getPiece().getPosition()-x)/game.getBoard().boardSize;
            x*=getWidth()/8;
            y*=getHeight()/8;
            //g2d.drawImage(guiPiece.getPieceImage(),0,0,null);
            g2d.drawImage(guiPiece.getPieceImage(),x,y,getWidth()/8,getHeight()/8,null);
        }


    }
}
