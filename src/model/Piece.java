package model;

import java.util.HashSet;

/**
 * A class that represent a single piece in the chess game
 */
public class Piece {
    /**
     * Colors of pieces in the game(
     * {@link #WHITE}
     * {@link #BLACK}
     * )
     */
    public enum Color{
        /**
         * White color
         */
        WHITE,
        /**
         * Black color
         */
        BLACK
    }

    /**
     * Types of the pieces in chess game
     */
    public enum Type{
        BISHOP,
        KING,
        KNIGHT,
        PAWN,
        QUEEN,
        ROOK
    }

    private int position;
    private Type type;
    private Color color;
    private boolean isDead = false;
    private int movesCounter = 0;
    private Board board;

    /**
     * Constructor of the piece
     * @param position Starting position of the piece
     * @param type Type of the piece BISHOP, KING, KNIGHT, PAWN, QUEEN, ROOK
     * @param color White or Black
     * @param board Reference to the board where the piece is placed
     */
    protected Piece(int position,Type type,Color color,Board board){
        this.position = position;
        this.type = type;
        this.color = color;
        this.board = board;
    }

    /**
     * Method returns the type of the piece
     * @return Type of the piece
     */
    public Type getPieceType(){
        return type;
    }

    /**
     * Method returns color of the piece
     * @return Color of the piece
     */
    public Color getPieceColor(){
        return color;
    }

    /**
     * Sets the position
     * @param position Position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return Position of the piece
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the death state of the piece
     * @param dead Status of death to be set
     */
    public void setIsDead(boolean dead) {
        this.isDead = dead;
    }

    /**
     * @return If the piece is dead or not.
     */
    public boolean getIsDead() {
        return isDead;
    }

    /**
     * @return number of moves done by this piece.
     */
    public int getMovesCounter() {
        return movesCounter;
    }

    /**
     * Sets a number of moves done by this piece.
     * @param movesCounter Number of moves
     */
    public void setMovesCounter(int movesCounter) {
        this.movesCounter = movesCounter;
    }

    /**
     * Method that returns a Hash set of positions to which the piece can move.
     * @param checkIllegalMoves Mark if the method should get rid of moves that make king vulnerable to attack
     * @return Set of moves.
     */
    public HashSet<Integer> getAvailableMoves(boolean checkIllegalMoves){
        HashSet<Integer> result = new HashSet<>();
            switch (this.type){
                case PAWN:
                    getPawnMoves(result);
                    break;
                case BISHOP:
                    getBishopMoves(result);
                    break;
                case KNIGHT:
                    getKnightMoves(result);
                    break;
                case KING:
                    getKingMoves(result);
                    break;
                case QUEEN:
                    getQueenMoves(result);
                    break;
                case ROOK:
                    getRookMoves(result);
                    break;
            }
            if(checkIllegalMoves) {
                HashSet<Integer> illegalMoves = new HashSet<>();
                for (Integer move : result) {
                    board.makeMove(this, move, false, false);
                    if (board.check(color)) {
                        illegalMoves.add(move);
                    }
                    board.undoMove();
                }
                HashSet<Integer> var = new HashSet<>();
                var.addAll(result);
                result.clear();

                for(Integer move : var){
                    if(!illegalMoves.contains(move)){
                        result.add(move);
                    }
                }
            }
        return result;
    }

    /**
     * Returns positions to which Pawn piece can move
     * @param moves Set to be filled with positions that piece can move.
     */
    private void getPawnMoves(HashSet<Integer> moves){
        int movedirection = getPieceColor() == Color.WHITE? Board.UP : Board.DOWN;

        //1 tile + 2 tile Up/Down
        if(getPosition()+movedirection>=0 && getPosition()+movedirection<Board.boardSize*Board.boardSize){
            if(board.getPiece(getPosition()+movedirection)==null){
                moves.add(getPosition()+movedirection);
                if(movesCounter == 0){
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
            if(Board.boardSize - (getPosition()%Board.boardSize)!=1){
                if(board.getPiece(positionInNextRow+Board.RIGHT)!=null){
                    if(board.getPiece(positionInNextRow+Board.RIGHT).getPieceColor()!=getPieceColor()){
                        moves.add(positionInNextRow+Board.RIGHT);
                    }
                }
            }
        }

    }

    /**
     * Returns positions to which Bishop piece can move
     * @param moves Set to be filled with positions that piece can move.
     */
    private void getBishopMoves(HashSet<Integer> moves){
        int var;
        //UP RIGHT
        var = getPosition();
        while (var+Board.UP>=0 && var%Board.boardSize!=Board.boardSize-1){
            Piece pieceOnNextTile = board.getPiece(var+Board.UP+Board.RIGHT);
            if (pieceOnNextTile==null){
                moves.add(var+Board.UP+Board.RIGHT);
                var += Board.UP + Board.RIGHT;
                continue;
            }else {
                if (pieceOnNextTile.getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.UP+Board.RIGHT);
                    var += Board.UP + Board.RIGHT;
                    break;
                }else {
                    break;
                }
            }
        }
        //UP LEFT
        var = getPosition();
        while (var+Board.UP>=0 && var%Board.boardSize!=0){
            Piece pieceOnNextTile = board.getPiece(var+Board.UP+Board.LEFT);
            if (pieceOnNextTile==null){
                moves.add(var+Board.UP+Board.LEFT);
                var += Board.UP + Board.LEFT;
                continue;
            }else {
                if (pieceOnNextTile.getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.UP+Board.LEFT);
                    var += Board.UP + Board.LEFT;
                    break;
                }else {
                    break;
                }
            }
        }
        //DOWN RIGHT
        var = getPosition();
        while (var+Board.DOWN<Board.boardSize*Board.boardSize && var%Board.boardSize!=Board.boardSize-1){
            Piece pieceOnNextTile = board.getPiece(var+Board.DOWN+Board.RIGHT);
            if (pieceOnNextTile==null){
                moves.add(var+Board.DOWN+Board.RIGHT);
                var += Board.DOWN + Board.RIGHT;
                continue;
            }else {
                if (pieceOnNextTile.getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.DOWN+Board.RIGHT);
                    var += Board.DOWN + Board.RIGHT;
                    break;
                }else {
                    break;
                }
            }
        }
        //DOWN LEFT
        var = getPosition();
        while (var+Board.DOWN<Board.boardSize*Board.boardSize && var%Board.boardSize!=0){
            Piece pieceOnNextTile = board.getPiece(var+Board.DOWN+Board.LEFT);
            if (pieceOnNextTile==null){
                moves.add(var+Board.DOWN+Board.LEFT);
                var += Board.DOWN + Board.LEFT;
                continue;
            }else {
                if (pieceOnNextTile.getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.DOWN+Board.LEFT);
                    var += Board.DOWN + Board.LEFT;
                    break;
                }else {
                    break;
                }
            }
        }
    }

    /**
     * Returns positions to which Knight piece can move
     * @param moves Set to be filled with positions that piece can move.
     */
    private void getKnightMoves(HashSet<Integer> moves){
        //UP
        if(getPosition()+2*Board.UP>=0){
            //LEFT
            if(getPosition()%Board.boardSize!=0){
                if(board.getPiece(getPosition()+2*Board.UP+Board.LEFT)==null){
                    moves.add(getPosition()+2*Board.UP+Board.LEFT);
                }else {
                    if(board.getPiece(getPosition()+2*Board.UP+Board.LEFT).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.UP+Board.LEFT);
                    }
                }
            }
            //RIGHT
            if(getPosition()%Board.boardSize!=Board.boardSize-1){
                if(board.getPiece(getPosition()+2*Board.UP+Board.RIGHT)==null){
                    moves.add(getPosition()+2*Board.UP+Board.RIGHT);
                }else {
                    if(board.getPiece(getPosition()+2*Board.UP+Board.RIGHT).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.UP+Board.RIGHT);
                    }
                }
            }
        }
        //DOWN
        if(getPosition()+2*Board.DOWN<Board.boardSize*Board.boardSize){
            //LEFT
            if(getPosition()%Board.boardSize!=0){
                if(board.getPiece(getPosition()+2*Board.DOWN+Board.LEFT)==null){
                    moves.add(getPosition()+2*Board.DOWN+Board.LEFT);
                }else {
                    if(board.getPiece(getPosition()+2*Board.DOWN+Board.LEFT).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.DOWN+Board.LEFT);
                    }
                }
            }
            //RIGHT
            if(getPosition()%Board.boardSize!=Board.boardSize-1){
                if(board.getPiece(getPosition()+2*Board.DOWN+Board.RIGHT)==null){
                    moves.add(getPosition()+2*Board.DOWN+Board.RIGHT);
                }else {
                    if(board.getPiece(getPosition()+2*Board.DOWN+Board.RIGHT).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.DOWN+Board.RIGHT);
                    }
                }
            }
        }
        //LEFT
        if(getPosition()%Board.boardSize>1){
            //UP
            if(getPosition()+Board.UP>=0){
                if(board.getPiece(getPosition()+2*Board.LEFT+Board.UP)==null){
                    moves.add(getPosition()+2*Board.LEFT+Board.UP);
                }else{
                    if(board.getPiece(getPosition()+2*Board.LEFT+Board.UP).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.LEFT+Board.UP);
                    }
                }
            }
            //DOWN
            if(getPosition()+Board.DOWN<Board.boardSize*Board.boardSize){
                if(board.getPiece(getPosition()+2*Board.LEFT+Board.DOWN)==null){
                    moves.add(getPosition()+2*Board.LEFT+Board.DOWN);
                }else{
                    if(board.getPiece(getPosition()+2*Board.LEFT+Board.DOWN).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.LEFT+Board.DOWN);
                    }
                }
            }
        }

        //RIGHT
        if(getPosition()%Board.boardSize<Board.boardSize-2){
            //UP
            if(getPosition()+Board.UP>=0){
                if(board.getPiece(getPosition()+2*Board.RIGHT+Board.UP)==null){
                    moves.add(getPosition()+2*Board.RIGHT+Board.UP);
                }else{
                    if(board.getPiece(getPosition()+2*Board.RIGHT+Board.UP).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.RIGHT+Board.UP);
                    }
                }
            }
            //DOWN
            if(getPosition()+Board.DOWN<Board.boardSize*Board.boardSize){
                if(board.getPiece(getPosition()+2*Board.RIGHT+Board.DOWN)==null){
                    moves.add(getPosition()+2*Board.RIGHT+Board.DOWN);
                }else{
                    if(board.getPiece(getPosition()+2*Board.RIGHT+Board.DOWN).getPieceColor()!=getPieceColor()){
                        moves.add(getPosition()+2*Board.RIGHT+Board.DOWN);
                    }
                }
            }
        }

    }

    /**
     * Returns positions to which King piece can move
     * @param moves Set to be filled with positions that piece can move.
     */
    private void getKingMoves(HashSet<Integer> moves){
        //UP
        if(getPosition()+Board.UP>=0){
            if(board.getPiece(getPosition()+Board.UP)==null){
                moves.add(getPosition()+Board.UP);
            }else if(board.getPiece(getPosition()+Board.UP).getPieceColor()!=getPieceColor()){
                moves.add(getPosition()+Board.UP);
            }
            //LEFT
            if(getPosition()%Board.boardSize>0){
                if(board.getPiece(getPosition()+Board.UP+Board.LEFT)==null){
                    moves.add(getPosition()+Board.UP+Board.LEFT);
                } else if (board.getPiece(getPosition()+Board.UP+Board.LEFT).getPieceColor()!=getPieceColor()){
                    moves.add(getPosition()+Board.UP+Board.LEFT);
                }
            }
            //RIGHT
            if(getPosition()%Board.boardSize<Board.boardSize-1){
                if(board.getPiece(getPosition()+Board.UP+Board.RIGHT)==null){
                    moves.add(getPosition()+Board.UP+Board.RIGHT);
                } else if (board.getPiece(getPosition()+Board.UP+Board.RIGHT).getPieceColor()!=getPieceColor()){
                    moves.add(getPosition()+Board.UP+Board.RIGHT);
                }
            }
        }
        //DOWN
        if(getPosition()+Board.DOWN<Board.boardSize*Board.boardSize){
            if(board.getPiece(getPosition()+Board.DOWN)==null){
                moves.add(getPosition()+Board.DOWN);
            }else if(board.getPiece(getPosition()+Board.DOWN).getPieceColor()!=getPieceColor()){
                moves.add(getPosition()+Board.DOWN);
            }
            //LEFT
            if(getPosition()%Board.boardSize>0){
                if(board.getPiece(getPosition()+Board.DOWN+Board.LEFT)==null){
                    moves.add(getPosition()+Board.DOWN+Board.LEFT);
                } else if (board.getPiece(getPosition()+Board.DOWN+Board.LEFT).getPieceColor()!=getPieceColor()){
                    moves.add(getPosition()+Board.DOWN+Board.LEFT);
                }
            }
            //RIGHT
            if(getPosition()%Board.boardSize<Board.boardSize-1){
                if(board.getPiece(getPosition()+Board.DOWN+Board.RIGHT)==null){
                    moves.add(getPosition()+Board.DOWN+Board.RIGHT);
                } else if (board.getPiece(getPosition()+Board.DOWN+Board.RIGHT).getPieceColor()!=getPieceColor()){
                    moves.add(getPosition()+Board.DOWN+Board.RIGHT);
                }
            }
        }
        //LEFT
        if(getPosition()%Board.boardSize>0){
            if(board.getPiece(getPosition()+Board.LEFT)==null){
                moves.add(getPosition()+Board.LEFT);
            } else if (board.getPiece(getPosition()+Board.LEFT).getPieceColor()!=getPieceColor()){
                moves.add(getPosition()+Board.LEFT);
            }
        }
        //RIGHT
        if(getPosition()%Board.boardSize<Board.boardSize-1){
            if(board.getPiece(getPosition()+Board.RIGHT)==null){
                moves.add(getPosition()+Board.RIGHT);
            } else if (board.getPiece(getPosition()+Board.RIGHT).getPieceColor()!=getPieceColor()){
                moves.add(getPosition()+Board.RIGHT);
            }
        }
        //CASTLING
        if(getMovesCounter()==0){
            if(getPosition()%Board.boardSize==4){
                //LEFT
                if(board.getPiece(getPosition()+Board.LEFT)==null &&
                        board.getPiece(getPosition()+2*Board.LEFT)==null &&
                        board.getPiece(getPosition()+3*Board.LEFT)==null &&
                        board.getPiece(getPosition()+4*Board.LEFT)!=null
                ){
                    if(board.getPiece(getPosition()+4*Board.LEFT).getMovesCounter()==0 &&
                            board.getPiece(getPosition()+4*Board.LEFT).getPieceType()==Type.ROOK &&
                            board.getPiece(getPosition()+4*Board.LEFT).getPieceColor()==getPieceColor()
                    ) {
                        moves.add(getPosition() + 2 * Board.LEFT);
                    }
                }
                //RIGHT
                if(board.getPiece(getPosition()+Board.RIGHT)==null &&
                        board.getPiece(getPosition()+2*Board.RIGHT)==null &&
                        board.getPiece(getPosition()+3*Board.RIGHT)!=null
                ){
                    if(board.getPiece(getPosition()+3*Board.RIGHT).getMovesCounter()==0 &&
                            board.getPiece(getPosition()+3*Board.RIGHT).getPieceType()==Type.ROOK &&
                            board.getPiece(getPosition()+3*Board.RIGHT).getPieceColor()==getPieceColor()
                    ) {
                        moves.add(getPosition() + 2 * Board.RIGHT);
                    }
                }
            }
        }
    }

    /**
     * Returns positions to which Queen piece can move
     * @param moves Set to be filled with positions that piece can move.
     */
    private void getQueenMoves(HashSet<Integer> moves){
        getRookMoves(moves);
        getBishopMoves(moves);
    }

    /**
     * Returns positions to which Rook piece can move
     * @param moves Set to be filled with positions that piece can move.
     */
    private void getRookMoves(HashSet<Integer> moves){
        int var;
        //UP
        var = getPosition();
        while (var+Board.UP>=0){
            if(board.getPiece(var+Board.UP)==null){
                moves.add(var+Board.UP);
                var+=Board.UP;
            }else{
                if(board.getPiece(var+Board.UP).getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.UP);
                    var+=Board.UP;
                    break;
                }else {
                    break;
                }
            }
        }
        //DOWN
        var = getPosition();
        while (var+Board.DOWN<Board.boardSize*Board.boardSize){
            if(board.getPiece(var+Board.DOWN)==null){
                moves.add(var+Board.DOWN);
                var+=Board.DOWN;
            }else{
                if(board.getPiece(var+Board.DOWN).getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.DOWN);
                    var+=Board.DOWN;
                    break;
                }else {
                    break;
                }
            }
        }
        //RIGHT
        var = getPosition();
        while (var%Board.boardSize!=Board.boardSize-1){
            if(board.getPiece(var+Board.RIGHT)==null){
                moves.add(var+Board.RIGHT);
                var+=Board.RIGHT;
            }else{
                if(board.getPiece(var+Board.RIGHT).getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.RIGHT);
                    var+=Board.RIGHT;
                    break;
                }else {
                    break;
                }
            }
        }
        //LEFT
        var = getPosition();
        while (var%Board.boardSize!=0){
            if(board.getPiece(var+Board.LEFT)==null){
                moves.add(var+Board.LEFT);
                var+=Board.LEFT;
            }else{
                if(board.getPiece(var+Board.LEFT).getPieceColor()!=getPieceColor()){
                    moves.add(var+Board.LEFT);
                    var+=Board.LEFT;
                    break;
                }else {
                    break;
                }
            }
        }
    }

}
