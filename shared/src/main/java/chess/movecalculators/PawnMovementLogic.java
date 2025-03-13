package chess.movecalculators;

import chess.*;
import chess.ChessMove;
import chess.ChessGame;

import java.util.HashSet;

public class PawnMovementLogic {
    //helper for later rewrite of pawn
    private static void addMoveOrPromotion(HashSet<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow) {
        if (end.getRow() == promotionRow) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece piece = board.getPiece(position);
        //starting position
        int row = position.getRow();
        int col = position.getColumn();

        //left and righth diags
        int leftCol = col - 1;
        int rightCol = col + 1;
        ChessGame.TeamColor color = piece.getTeamColor();
        //based on color
        //movement
        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        //starting
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        //promo
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int forwardRow = row + direction;
        ChessPosition forwardPos = new ChessPosition(forwardRow, col);

        if (MoveCalculator.isInBounds(forwardRow, col) && board.getPiece(forwardPos) == null) {
            addMoveOrPromotion(moves, position, forwardPos, promotionRow);
        }
        //move double on first turn
        //start at 2 for pawn or 7 for black
        //use startRow
        if (row == startRow) {
            int doubleMoveRow = row + 2 * direction;
            ChessPosition doubleMovePos = new ChessPosition(doubleMoveRow, col);
            if (MoveCalculator.isInBounds(doubleMoveRow, col) && board.getPiece(doubleMovePos) == null && board.getPiece(forwardPos) == null) {
                moves.add(new ChessMove(position, doubleMovePos, null));
            }
        }
        //more for diags
        // Right diagonal
        ChessPosition capRight = new ChessPosition(forwardRow, rightCol);
        //check if in bounds (future move)
        if (MoveCalculator.isInBounds(capRight.getRow(), capRight.getColumn())) {
            //target then right
            ChessPiece targetPiece = board.getPiece(capRight);
            if (targetPiece != null && targetPiece.getTeamColor() != color) {
                addMoveOrPromotion(moves, position, capRight, promotionRow);
            }
        }

        // Left diagonal
        ChessPosition capLeft = new ChessPosition(forwardRow, leftCol);
        //check if in bounds (future move)
        if (MoveCalculator.isInBounds(capLeft.getRow(), capLeft.getColumn())) {
            //target then left.
            ChessPiece targetPiece = board.getPiece(capLeft);
            //not null and not same team
            if (targetPiece != null && targetPiece.getTeamColor() != color) {
                addMoveOrPromotion(moves, position, capLeft, promotionRow);
            }
        }
        return moves;
    }
}

