package gui;

import model.Piece;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {
    private BufferedImage piecesImage = null;
    private BufferedImage logoImage = null;

    private static final ImageLoader instane = new ImageLoader();

    private ImageLoader() {}

    public void setPiecesImage(String path) throws Exception{
        try {
            piecesImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new Exception("Failed to load pieces image!!!");
        }
    }
    public void setLogoImage(String path) throws Exception {
        try {
            logoImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new Exception("Failed to load logo image!!!");
        }
    }

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
        BufferedImage result = piecesImage.getSubimage(x*piecesImage.getWidth()/6,y*piecesImage.getHeight()/2,
                piecesImage.getWidth()/6,piecesImage.getHeight()/2);


        return result;


    }

    public BufferedImage getLogoImage(){
        return logoImage;
    }

    public static ImageLoader getInstance(){
        return instane;
    }
}
