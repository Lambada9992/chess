package gui;

import model.Piece;

import java.awt.image.BufferedImage;

public class GuiPiece {
    Piece piece;
    BufferedImage image;

    public GuiPiece(Piece piece) {
        this.piece = piece;
        this.image = ImageLoader.getInstance().getPieceImage(piece.getPieceType(),piece.getPieceColor());
    }

    public Piece getPiece(){
        return piece;
    }

    public BufferedImage getPieceImage(){
        return image;
    }
}
