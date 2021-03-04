package model;

public class Piece {
    public enum Color{
        WHITE,
        BLACK
    }
    public enum Type{
        BISHOP,KING,KNIGHT,PAWN,QUEEN,ROCK,CASTLE
    }

    private int position;
    private Type type;
    private Color color;
    private int n_moves = 0;

    public Piece(){//TODO to delete

    }

    protected Piece(int position,Type type,Color color){
        this.position = position;
        this.type = type;
        this.color = color;
    }
}
