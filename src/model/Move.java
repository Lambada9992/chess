package model;

/**
 * A class that stores a move
 */
public class Move {
    private Piece piece;
    private int from,to;
    /**
     * Used for castling
     */
    private Move extraMove = null;
    /**
     * Used when piece kill another piece
     */
    private Piece killedPiece = null;

    /**
     * Constructor
     * @param piece Piece is being moved
     * @param from From position on the board
     * @param to To position on the board
     */
    public Move(Piece piece, int from, int to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    /**
     * @return The piece
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * @return From position
     */
    public int getFrom() {
        return from;
    }

    /**
     * @return To position
     */
    public int getTo() {
        return to;
    }

    /**
     * @return The extra move(used for castling)
     */
    public Move getExtraMove() {
        return extraMove;
    }

    /**
     * @return Piece killed by this move
     */
    public Piece getKilledPiece() {
        return killedPiece;
    }

    /**
     * Sets the extra move(castling)
     * @param extraMove Move
     */
    public void setExtraMove(Move extraMove) {
        this.extraMove = extraMove;
    }

    /**
     * Sets the killed piece
     * @param killedPiece Piece
     */
    public void setKilledPiece(Piece killedPiece) {
        this.killedPiece = killedPiece;
    }
}
