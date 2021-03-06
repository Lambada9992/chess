package model;

import java.util.*;

/**
 * Class that represents the board of the chess game
 */
public class Board {

    public static final int boardSize = 8;
    public static final int UP = -boardSize;
    public static final int DOWN = boardSize;
    public static final int RIGHT = 1;
    public static final int LEFT = -1;

    private Piece[] board = new Piece[boardSize*boardSize];
    private LinkedList<Move> movesHistory = new LinkedList<>();

    /**
     * Delete all pieces from the board and put new Pieces in default starting positions
     */
    public void putPiecesOnBoard(){
        clearBoard();
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

    /**
     * Delete all pieces from the board
     */
    public void clearBoard(){
        for(int i = 0;i<board.length;i++){
            board[i] = null;
        }
        movesHistory.clear();
    }

    /**
     * Prints the board in the console
     * @deprecated Used only for debug purposes
     */
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

    /**
     * Moves selected piece to the selected position
     * @param piece Piece to be moved
     * @param toPosition Position where the piece should be moved
     * @return If the move was made successfully
     */
    public boolean makeMove(Piece piece, int toPosition){
        return makeMove(piece,toPosition,false,true);
    }

    /**
     * Moves selected piece to the selected position
     * @param piece Piece to be moved
     * @param toPosition Position where the piece should be moved
     * @param undoMode Marks if a move is a try of undoing the move that is already made
     * @param checkAvailableMoves Marks if the method should check if king is vulnerable to attack after this move
     * @return If the move was made successfully
     */
    public boolean makeMove(Piece piece, int toPosition,boolean undoMode,boolean checkAvailableMoves){

        if(checkAvailableMoves){
            if(!piece.getAvailableMoves(true).contains(toPosition)) return false;
        }

        Move move = new Move(piece,piece.getPosition(),toPosition);

        if(board[toPosition]!=null){
            if (board[toPosition].getPieceColor()==piece.getPieceColor()){
                return false;
            }else {
                board[toPosition].setIsDead(true);
                move.setKilledPiece(board[toPosition]);
            }
        }
        board[piece.getPosition()] = null;

        //castling
        if(!undoMode) {
            if (piece.getPieceType() == Piece.Type.KING) {
                if (toPosition - piece.getPosition() == 2 * LEFT) {
                    move.setExtraMove(new Move(board[piece.getPosition() + 4 * LEFT], piece.getPosition() + 4 * LEFT, toPosition + RIGHT));
                    board[toPosition + RIGHT] = board[piece.getPosition() + 4 * LEFT];
                    board[piece.getPosition() + 4 * LEFT] = null;
                    board[toPosition + RIGHT].setPosition(toPosition + RIGHT);
                    board[toPosition + RIGHT].setMovesCounter(board[toPosition + RIGHT].getMovesCounter() + 1);
                } else if (toPosition - piece.getPosition() == 2 * RIGHT) {
                    move.setExtraMove(new Move(board[piece.getPosition() + 3 * RIGHT], piece.getPosition() + 3 * RIGHT, toPosition + LEFT));
                    board[toPosition + LEFT] = board[piece.getPosition() + 3 * RIGHT];
                    board[piece.getPosition() + 3 * RIGHT] = null;
                    board[toPosition + LEFT].setPosition(toPosition + LEFT);
                    board[toPosition + LEFT].setMovesCounter(board[toPosition + LEFT].getMovesCounter() + 1);
                }
            }
        }

        board[toPosition] = piece;
        piece.setPosition(toPosition);
        piece.setMovesCounter(piece.getMovesCounter()+1);
        if(!undoMode)movesHistory.add(move);

        return true;
    }

    /**
     * Undo the last move made, if there is no movement in the history it's do nothing
     */
    public void undoMove(){
        if(movesHistory.size()!=0){
            Move move = movesHistory.peekLast();
            movesHistory.removeLast();
            makeMove(move.getPiece(),move.getFrom(),true,false);
            move.getPiece().setMovesCounter(move.getPiece().getMovesCounter()-2);

            if(move.getKilledPiece()!=null){
                board[move.getTo()] = move.getKilledPiece();
                move.getKilledPiece().setIsDead(false);
            }

            if(move.getExtraMove()!=null){
                movesHistory.add(move.getExtraMove());
                undoMove();
            }
        }

    }

    /**
     * Is it check?
     * Returns information if the chosen king(by color) can be killed by any other piece on the board
     *
     * @param color Color of the king that is being checked
     * @return If if the chosen king is vulnerable to attack from another piece
     */
    public boolean check(Piece.Color color){
        Piece king = null;

        HashSet<Integer> allCoveredTiles = new HashSet<>();

        for(Piece var : board){
            if(var!=null){
                //findKing
                if(var.getPieceType()== Piece.Type.KING && var.getPieceColor()==color){
                    king = var;
                }
                //get all covered places
                if(var.getPieceColor()!=color){
                    allCoveredTiles.addAll(var.getAvailableMoves(false));
                }
            }
        }
        if (king==null) return true;
        return allCoveredTiles.contains(king.getPosition())? true : false ;
    }

    /**
     * Is it CheckMate?
     * Returns information if pieces in any chosen color can do any move if not it's CheckMate
     * @param color color
     * @return If is checkMate or not
     */
    public boolean checkMate(Piece.Color color){
        for(Piece var: board){
            if(var==null) continue;
            if(var.getPieceColor()==color){
                if(var.getAvailableMoves(true).size()!=0){
                    return false;
                }
            }else {
                continue;
            }
        }
        return true;
    }

    /**
     * Returns the list of all pieces on the board
     * @return list of pieces on the board
     */
    public ArrayList<Piece> getPieces(){
        ArrayList<Piece> result = new ArrayList<Piece>();
        for(int i=0;i<board.length; i++){
            if (board[i]!=null){
                result.add(board[i]);
            }
        }
        return result;
    }

    /**
     * Returns the piece from the board on the chosen position
     * @param position position on the board
     * @return piece
     */
    public Piece getPiece(int position){
        return board[position];
    }
}
