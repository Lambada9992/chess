package model;

import model.Player;

public class Piece {
    public enum Type{
        BISHOP,KING,KNIGHT,PAWN,QUEEN,ROCK
    }

    private int x,y;
    private Player owner;
    private int n_moves = 0;
    private Type type;

    public Piece(){//TODO to delete

    }

    protected Piece(int x,int y,Type type,Player owner){
        this.type = type;
        this.owner = owner;
    }
}
