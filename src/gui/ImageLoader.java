package gui;

import model.Piece;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {
    private BufferedImage piecesImage = null;
    private BufferedImage logoImage = null;

    private static final ImageLoader instance = new ImageLoader();

    /**
     * Constructor
     */
    private ImageLoader() {}

    /**
     * Sets the Pieces image from the file
     * @param path Path to the image file
     * @throws Exception Load image fail
     */
    public void setPiecesImage(String path) throws Exception{
        try {
            try {
                piecesImage = ImageIO.read(getClass().getResourceAsStream(path));
            } catch (IllegalArgumentException e){
                piecesImage = ImageIO.read(new File(path));
            }

        } catch (IOException e) {
            throw new Exception("Failed to load pieces image!!!");
        }
    }

    /**
     * Sets the Icon Logo from the file
     * @param path Path to the image file
     * @throws Exception Load image fail
     */
    public void setLogoImage(String path) throws Exception {
        try {
            try {
                logoImage = ImageIO.read(getClass().getResourceAsStream(path));
            } catch (IllegalArgumentException e){
                logoImage = ImageIO.read(new File(path));
            }
        } catch (IOException e) {
            throw new Exception("Failed to load logo image!!!");
        }
    }

    /**
     * Returns the image of the chosen piece
     * @param type Type of the piece
     * @param color Color of the piece
     * @return Image
     */
    public BufferedImage getPieceImage(Piece.Type type,Piece.Color color){
        int x=5,y = color == Piece.Color.BLACK ? 0 : 1;
        switch (type){
            case QUEEN:
                x = 0; break;
            case KING:
                x = 1; break;
            case ROOK:
                x = 2; break;
            case KNIGHT:
                x = 3; break;
            case BISHOP:
                x = 4; break;
            case PAWN:
                x = 5; break;
        }
        if(piecesImage==null) return null;
        BufferedImage result = piecesImage.getSubimage(x*piecesImage.getWidth()/6,y*piecesImage.getHeight()/2,
                piecesImage.getWidth()/6,piecesImage.getHeight()/2);

        return result;
    }

    /**
     * @return The logo icon image
     */
    public BufferedImage getLogoImage(){
        return logoImage;
    }

    /**
     * @return The instance of the class
     */
    public static ImageLoader getInstance(){
        return instance;
    }
}
