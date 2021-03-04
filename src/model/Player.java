package model;

public class Player {

    private Piece.Color color = null;

    public Player(){

    }

    public void setColor(Piece.Color color){
        this.color = color;
    }

    public Piece.Color getColor(){
        return color;
    }


}
