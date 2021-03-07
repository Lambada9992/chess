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
    private Board board;

    protected Piece(int position,Type type,Color color,Board board){
        this.position = position;
        this.type = type;
        this.color = color;
        this.board = board;
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

    public int getN_moves() {
        return n_moves;
    }

    public void setN_moves(int n_moves) {
        this.n_moves = n_moves;
    }

    public HashSet<Integer> getAvailableMoves(){
        HashSet<Integer> result = new HashSet<>();
            switch (this.type){
                case PAWN:
                    getPawnMoves(result,this);
                    break;
                case BISHOP:
                    break;
                case KNIGHT:
                    break;
                case KING:
                    break;
                case QUEEN:
                    break;
                case ROOK:
                    break;
            }
        return result;
    }

    private void getPawnMoves(HashSet<Integer> moves,Piece piece){
        int movedirection = getPieceColor() == Color.WHITE? Board.UP : Board.DOWN;

        //1 tile + 2 tile Up/Down
        if(getPosition()+movedirection>=0 && getPosition()+movedirection<Board.boardSize*Board.boardSize){
            if(board.getPiece(getPosition()+movedirection)==null){
                moves.add(getPosition()+movedirection);
                if(n_moves == 0){
                    if(getPosition()+2*movedirection>=0 ||
                            getPosition()+2*movedirection<Board.boardSize*Board.boardSize){
                        if(board.getPiece(getPosition()+2*movedirection)==null){
                            moves.add(getPosition()+2*movedirection);
                        }
                    }
                }
            }
        }

        //kill another piece
        if(getPosition()+movedirection>=0 && getPosition()+movedirection<Board.boardSize*Board.boardSize){
            int positionInNextRow = getPosition()+movedirection;
            //left
            if(getPosition()%Board.boardSize!=0){
                if(board.getPiece(positionInNextRow+Board.LEFT)!=null){
                    if(board.getPiece(positionInNextRow+Board.LEFT).getPieceColor()!=getPieceColor()){
                        moves.add(positionInNextRow+Board.LEFT);
                    }
                }
            }
            //right
            if(Board.boardSize - (getPosition()%Board.boardSize)!=0){
                if(board.getPiece(positionInNextRow+Board.RIGHT)!=null){
                    if(board.getPiece(positionInNextRow+Board.RIGHT).getPieceColor()!=getPieceColor()){
                        moves.add(positionInNextRow+Board.RIGHT);
                    }
                }
            }
        }

    }

    private void getBishopMoves(HashSet<Integer> moves,Piece piece){

    }
    private void getKnightMoves(HashSet<Integer> moves,Piece piece){

    }
    private void getKingMoves(HashSet<Integer> moves,Piece piece){

    }private void getQueenMoves(HashSet<Integer> moves,Piece piece){

    }
    private void getRookMoves(HashSet<Integer> moves,Piece piece){

    }


}
