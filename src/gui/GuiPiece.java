package gui;

import model.Piece;

import java.awt.image.BufferedImage;

/**
 * A class that represents GUI Piece
 * It holds a reference to a piece and its image
 */
public class GuiPiece {
    Piece piece;
    BufferedImage image;

    /**
     * Constructor
     * @param piece A piece which is represented by this instance
     */
    public GuiPiece(Piece piece) {
        this.piece = piece;
        this.image = ImageLoader.getInstance().getPieceImage(piece.getPieceType(),piece.getPieceColor());
    }

    /**
     * @return The reference to the piece
     */
    public Piece getPiece(){
        return piece;
    }

    /**
     * @return The image of the piece
     */
    public BufferedImage getPieceImage(){
        return image;
    }
}
