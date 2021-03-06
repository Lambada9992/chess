package model;

import java.util.HashSet;
import java.util.Set;

public class Piece {
    public enum Color{WHITE,BLACK}
    public enum Type{BISHOP,KING,KNIGHT,PAWN,QUEEN,ROOK}

    private int position;
    private Type type;
    private Color color;
    private boolean isDead = false;
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

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setIsDead(boolean dead) {
        isDead = dead;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public HashSet<Integer> getAvailableMoves(Piece piece){
        HashSet<Integer> result = new HashSet<>();




        return result;
    }

}
