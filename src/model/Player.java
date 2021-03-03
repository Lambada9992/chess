package model;

public class Player {
    public enum g_color{
        WHITE,
        BLACK
    }
    private g_color color = null;

    public Player(){

    }

    public void setColor(g_color color){
        this.color = color;
    }

    public g_color getColor(){
        return color;
    }


}
