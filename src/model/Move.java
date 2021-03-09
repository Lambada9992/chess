package model;

public class Move {
    private Piece piece;
    private int from,to;
    private Move extraMove = null;
    private Piece killedPiece = null;

    public Move(Piece piece, int from, int to, Move extraMove) {
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.extraMove = extraMove;
    }

    public Move(Piece piece, int from, int to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    public Move(Piece piece, int from, int to, Piece killedPiece) {
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.killedPiece = killedPiece;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public Move getExtraMove() {
        return extraMove;
    }

    public Piece getKilledPiece() {
        return killedPiece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public void setExtraMove(Move extraMove) {
        this.extraMove = extraMove;
    }

    public void setKilledPiece(Piece killedPiece) {
        this.killedPiece = killedPiece;
    }
}
