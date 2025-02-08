package chess;


import chess.MoveCalculators.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;


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
        board.resetBoard();
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
        if(piece == null){
            return new HashSet<>();
        }

        //all moves before doing filtering
        Collection<ChessMove> unfilteredMoves = piece.pieceMoves(board, startPosition);
//        if(piece.getTeamColor() != teamTurn){
//            return unfilteredMoves;
//        }
        //now filter all moves
        Collection<ChessMove> moves = new HashSet<>();


        for(ChessMove move : unfilteredMoves){
            //use the copy method made earlier
            ChessBoard copiedBoard = board.copyBoard();
            ChessPiece toMove = copiedBoard.getPiece(move.getStartPosition());
            copiedBoard.addPiece(move.getStartPosition(), null);
            copiedBoard.addPiece(move.getEndPosition(), toMove);

            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(copiedBoard);
            tempGame.setTeamTurn(piece.getTeamColor());

            //make sure king not in check in order to add move
            if(!tempGame.isInCheck(piece.getTeamColor())){
                moves.add(move);
            }

        }

        //do later, got stuck
        return moves;
    }





    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //get starting position
        //throw exep if there is no piece there
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPos);
        if(piece == null){
            throw new InvalidMoveException("No piece at start position");
        }

        //check teamcolor

        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("wrong team");
        }

        //apply validMoves
        //as of rn this is not done yet, don't worry about it
        //check that the move is possible before doing anything
        Collection<ChessMove> possibleMoves = validMoves(startPos);
        if(!possibleMoves.contains(move)){
            throw new InvalidMoveException("move not valid");
        }

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);
        ChessPiece.PieceType promoType = move.getPromotionPiece();
        if(promoType != null){
            piece = new ChessPiece(piece.getTeamColor(), promoType);
            board.addPiece(endPos, piece);
        }
        //do the move
        //startpos needs to lose its start and add its end

        //SWITCH TEAM TURN
        if(teamTurn == WHITE){
            teamTurn = BLACK;
        }else{
            teamTurn = WHITE;
        }

        //CHECK IF ENDGAME (here or somewhere else)?
        if(isInCheckmate(teamTurn) || isInStalemate(teamTurn)){
            endGame = true;
        }


    }
    //got stuck, made this to help
    //remove and optimize later
    //there was an issue in valid moves line 78. not sure why but that logic statement ruined the whole thing ad
    //caused a ton of issues with testing. this function makes it test outside of the validMoves function so it works.
    private Collection<ChessMove> fakeValidMoves(ChessPosition startPosition){
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return new HashSet<>();
        }
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //go through each piece on the board to find the king
        //set null king pos to be filled later
        ChessPosition king = null;
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPosition square = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(square);
                if (piece != null && piece.getPieceType() == KING && piece.getTeamColor() == teamColor) {
                    king = square;
                }
            }
        }
        //check if opponent can attack king rn
        //find op team color
        TeamColor opponent = (teamColor == TeamColor.WHITE) ? BLACK : TeamColor.WHITE;
        //same start to loop as before
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPosition square = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(square);
                //this might be helpful for edge case scenarios?
                if(piece == null){
                    continue;
                }
                //create opMoves
                //if any of them end where king is, king is in check
                if(piece.getTeamColor() == opponent){
                    Collection<ChessMove> opMoves = fakeValidMoves(square);
                    for(ChessMove move : opMoves){
                        if(move.getEndPosition().equals(king)){
                            return true;
                        }
                    }
                }
            }
        }
        //if no move ends where king is, king is not in check
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if king is in check and there aren't valid moves the game is over
        if(!isInCheck(teamColor)){
            return false;
        }
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPosition square = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(square);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(square).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
        //if in check, no stalemate
        if(isInCheck(teamColor)){
            return false;
        }
        //this is almost the same as the isincheck
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPosition square = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(square);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(square);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
            return true;

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
