package chess;


import chess.MoveCalculators.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    //check if the game is over
    //inocorportate later
    private boolean endGame;
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        //white goes first
        //reset board
        board = new ChessBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get piece
        ChessPiece piece = board.getPiece(startPosition);

        //check if is empty
        if(piece == null){
            return new HashSet<>();
        }
        //check if its this pieces turn
        if(piece.getTeamColor() != teamTurn){
            return new HashSet<>();
        }
        //empty hashset for moves before checking if they are legal or not
        HashSet<ChessMove> allMoves = new HashSet<>();
        //go through piece types to run logic
        switch (piece.getPieceType()) {
            case ROOK:
                allMoves.addAll(RookMovementLogic.getMoves(board, startPosition));
                break;
            case BISHOP:
                allMoves.addAll(BishopMovementLogic.getMoves(board, startPosition));
                break;
            case KNIGHT:
                allMoves.addAll(KnightMovementLogic.getMoves(board, startPosition));
                break;
            case QUEEN:
                allMoves.addAll(QueenMovementLogic.getMoves(board, startPosition));
                break;
            case KING:
                allMoves.addAll(KingMovementLogic.getMoves(board, startPosition));
                break;
            case PAWN:
                allMoves.addAll(PawnMovementLogic.getMoves(board, startPosition));
                break;
        }

        //remove moves from allMoves that put the king in check
        // ChessBoard tempBoard = board.copy();

        //complete later

        return allMoves;
    }





    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if king is in check and there aren't valid moves the game is over
        return isInCheck(teamColor) && isInStalemate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //basically gonna check if there are ANY valid moves
        //if there arent any, then it should return true
        //if there are valid moves, it should return false
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    //did this on the other files
    //helpful for debugging?
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return endGame == chessGame.endGame && Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(endGame, board, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "endGame=" + endGame +
                ", board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
    }
}
