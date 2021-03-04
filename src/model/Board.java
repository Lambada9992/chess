package model;

public class Board {
    public final int boardSize = 8;
    private Piece[] board = new Piece[boardSize*boardSize];

    public void putPiecesOnBoard(){
        //BLACK
        board[0] = new Piece(0, Piece.Type.CASTLE,Piece.Color.BLACK);
        board[1] = new Piece(1, Piece.Type.KNIGHT,Piece.Color.BLACK);
        board[2] = new Piece(2, Piece.Type.BISHOP,Piece.Color.BLACK);
        board[3] = new Piece(3, Piece.Type.QUEEN,Piece.Color.BLACK);
        board[4] = new Piece(4, Piece.Type.KING,Piece.Color.BLACK);
        board[5] = new Piece(5, Piece.Type.BISHOP,Piece.Color.BLACK);
        board[6] = new Piece(6, Piece.Type.KNIGHT,Piece.Color.BLACK);
        board[7] = new Piece(7, Piece.Type.CASTLE,Piece.Color.BLACK);
        for(int i=boardSize ; i<2*boardSize;i++){
            board[i] = new Piece(i,Piece.Type.PAWN,Piece.Color.BLACK);
        }

        //WHITE
        board[(boardSize*(boardSize-1))+0] = new Piece(0, Piece.Type.CASTLE,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+1] = new Piece(1, Piece.Type.KNIGHT,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+2] = new Piece(2, Piece.Type.BISHOP,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+3] = new Piece(3, Piece.Type.QUEEN,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+4] = new Piece(4, Piece.Type.KING,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+5] = new Piece(5, Piece.Type.BISHOP,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+6] = new Piece(6, Piece.Type.KNIGHT,Piece.Color.WHITE);
        board[(boardSize*(boardSize-1))+7] = new Piece(7, Piece.Type.CASTLE,Piece.Color.WHITE);
        for(int i=boardSize*(boardSize-2); i<boardSize*(boardSize-1);i++){
            board[i] = new Piece(i,Piece.Type.PAWN,Piece.Color.WHITE);
        }
    }

    //TODO to delete
    public void printTest(){
        for (int i=0; i<boardSize; i++){
            for (int j=0; j<boardSize; j++){
                if (board[(i*boardSize) + j]==null){
                    System.out.print("0");
                }else {
                    System.out.print("1");
                }
            }
            System.out.println();
        }
    }
}
