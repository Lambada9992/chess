package model;

import java.util.ArrayList;

public class Board {
    public final int boardSize = 8;
    private Piece[] board = new Piece[boardSize*boardSize];

    public void putPiecesOnBoard(){
        //BLACK
        board[0] = new Piece(0, Piece.Type.ROOK,Piece.Color.BLACK);
        board[1] = new Piece(1, Piece.Type.KNIGHT,Piece.Color.BLACK);
        board[2] = new Piece(2, Piece.Type.BISHOP,Piece.Color.BLACK);
        board[3] = new Piece(3, Piece.Type.QUEEN,Piece.Color.BLACK);
        board[4] = new Piece(4, Piece.Type.KING,Piece.Color.BLACK);
        board[5] = new Piece(5, Piece.Type.BISHOP,Piece.Color.BLACK);
        board[6] = new Piece(6, Piece.Type.KNIGHT,Piece.Color.BLACK);
        board[7] = new Piece(7, Piece.Type.ROOK,Piece.Color.BLACK);
        for(int i=boardSize ; i<2*boardSize;i++){
            board[i] = new Piece(i,Piece.Type.PAWN,Piece.Color.BLACK);
        }

        //WHITE
        board[(boardSize*(boardSize-1))+0] = new Piece((boardSize*(boardSize-1))+0, Piece.Type.ROOK,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+1] = new Piece((boardSize*(boardSize-1))+1, Piece.Type.KNIGHT,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+2] = new Piece((boardSize*(boardSize-1))+2, Piece.Type.BISHOP,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+3] = new Piece((boardSize*(boardSize-1))+3, Piece.Type.QUEEN,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+4] = new Piece((boardSize*(boardSize-1))+4, Piece.Type.KING,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+5] = new Piece((boardSize*(boardSize-1))+5, Piece.Type.BISHOP,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+6] = new Piece((boardSize*(boardSize-1))+6, Piece.Type.KNIGHT,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+7] = new Piece((boardSize*(boardSize-1))+7, Piece.Type.ROOK,Piece.Color.WHITE);
        for(int i=boardSize*(boardSize-2); i<boardSize*(boardSize-1);i++){
            board[i] = new Piece(i,Piece.Type.PAWN,Piece.Color.WHITE);
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
}
