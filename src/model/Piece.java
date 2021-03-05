package model;

public class Piece {
    public enum Color{WHITE,BLACK}
    public enum Type{BISHOP,KING,KNIGHT,PAWN,QUEEN,ROOK}

    private int position;
    private Type type;
    private Color color;
    private int n_moves = 0;

    protected Piece(int position,Type type,Color color){
        this.position = position;
        this.type = type;
        this.color = color;
    }

    public Type getPieceType(){
        return type;
    }

    public Color getPieceColor(){
        return color;
    }

    public int getPosition() {
        return position;
    }
}
