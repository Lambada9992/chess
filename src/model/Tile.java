package model;

public class Tile {
    private Piece piece = null;

    public Piece getPiece(){
        return piece;
    }

    public void setPiece(Piece _piece){
        this.piece = _piece;
    }
}
