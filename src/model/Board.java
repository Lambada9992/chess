package model;

import java.util.ArrayList;

public class Board {
    public static final int boardSize = 8;

    public static final int UP = -boardSize;
    public static final int DOWN = boardSize;
    public static final int RIGHT = 1;
    public static final int LEFT = -1;

    private Piece[] board = new Piece[boardSize*boardSize];


    public void putPiecesOnBoard(){
        //BLACK
        board[0] = new Piece(0, Piece.Type.ROOK,Piece.Color.BLACK,this);
        board[1] = new Piece(1, Piece.Type.KNIGHT,Piece.Color.BLACK,this);
        board[2] = new Piece(2, Piece.Type.BISHOP,Piece.Color.BLACK,this);
        board[3] = new Piece(3, Piece.Type.QUEEN,Piece.Color.BLACK,this);
        board[4] = new Piece(4, Piece.Type.KING,Piece.Color.BLACK,this);
        board[5] = new Piece(5, Piece.Type.BISHOP,Piece.Color.BLACK,this);
        board[6] = new Piece(6, Piece.Type.KNIGHT,Piece.Color.BLACK,this);
        board[7] = new Piece(7, Piece.Type.ROOK,Piece.Color.BLACK,this);
        for(int i=boardSize ; i<2*boardSize;i++){
            board[i] = new Piece(i,Piece.Type.PAWN,Piece.Color.BLACK,this);
        }

        //WHITE
        board[(boardSize*(boardSize-1))+0] = new Piece((boardSize*(boardSize-1))+0, Piece.Type.ROOK,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+1] = new Piece((boardSize*(boardSize-1))+1, Piece.Type.KNIGHT,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+2] = new Piece((boardSize*(boardSize-1))+2, Piece.Type.BISHOP,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+3] = new Piece((boardSize*(boardSize-1))+3, Piece.Type.QUEEN,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+4] = new Piece((boardSize*(boardSize-1))+4, Piece.Type.KING,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+5] = new Piece((boardSize*(boardSize-1))+5, Piece.Type.BISHOP,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+6] = new Piece((boardSize*(boardSize-1))+6, Piece.Type.KNIGHT,Piece.Color.WHITE,this);
        board[(boardSize*(boardSize-1))+7] = new Piece((boardSize*(boardSize-1))+7, Piece.Type.ROOK,Piece.Color.WHITE,this);
        for(int i=boardSize*(boardSize-2); i<boardSize*(boardSize-1);i++){
            board[i] = new Piece(i,Piece.Type.PAWN,Piece.Color.WHITE,this);
        }
    }

    public void printBoardConsole(){
        for (int i=0; i<boardSize; i++){
            for (int j=0; j<boardSize; j++){
                if (board[(i*boardSize) + j]==null){
                    System.out.print("- ");
                }else {
                    switch (board[(i*boardSize) + j].getPieceType()){
                        case KING:
                            System.out.print("K ");
                            break;
                        case QUEEN:
                            System.out.print("Q ");
                            break;
                        case PAWN:
                            System.out.print("p ");
                            break;
                        case ROOK:
                            System.out.print("r");
                            break;
                        case BISHOP:
                            System.out.print("b ");
                            break;
                        case KNIGHT:
                            System.out.println("k ");

                    }
                }
            }
            System.out.println();
        }
    }

    public ArrayList<Piece> getPieces(){
        ArrayList<Piece> result = new ArrayList<Piece>();
        for(int i=0;i<board.length; i++){
            if (board[i]!=null){
                result.add(board[i]);
            }
        }
        return result;
    }

    public void movePiece(Piece piece,int toPosition){

        if(!piece.getAvailableMoves().contains(toPosition)) return;

        if(board[toPosition]!=null){
            if (board[toPosition].getPieceColor()==piece.getPieceColor()){
                return;
            }else board[toPosition].setIsDead(true);
        }
        board[piece.getPosition()] = null;

        //castling
        if(piece.getPieceType() == Piece.Type.KING){
            if(toPosition - piece.getPosition() == 2*LEFT){
                board[toPosition+RIGHT] = board[piece.getPosition()+4*LEFT];
                board[piece.getPosition()+4*LEFT] = null;
                board[toPosition+RIGHT].setPosition(toPosition+RIGHT);
            }else if(toPosition - piece.getPosition() == 2*RIGHT){
                board[toPosition+LEFT] = board[piece.getPosition()+3*RIGHT];
                board[piece.getPosition()+3*RIGHT] = null;
                board[toPosition+LEFT].setPosition(toPosition+LEFT);
            }
        }

        board[toPosition] = piece;
        piece.setPosition(toPosition);
        piece.setN_moves(piece.getN_moves()+1);
    }

    public Piece getPiece(int index){
        return board[index];
    }
}
