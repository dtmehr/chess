package chess.movecalculators;

import chess.*;
import chess.ChessMove;
import chess.ChessGame;

import java.util.HashSet;

public class PawnMovementLogic {
    //helper for later rewrite of pawn
    private static void addMoveOrPromotion(HashSet<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow)
    {
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
        HashSet<ChessMove> totalMoves = new HashSet<>();
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
            addMoveOrPromotion(totalMoves, position, forwardPos, promotionRow);
        }
        //move double on first turn
        //start at 2 for pawn or 7 for black
        //use startRow
        if (row == startRow) {
            int doubleMoveRow = row + 2 * direction;
            ChessPosition doubleMovePos = new ChessPosition(doubleMoveRow, col);
            if (MoveCalculator.isInBounds(doubleMoveRow, col) && board.getPiece(doubleMovePos) == null && board.getPiece(forwardPos) == null ) {
                totalMoves.add(new ChessMove(position, doubleMovePos, null));
            }
        }


//        //WHITE
//        ChessGame.TeamColor color = piece.getTeamColor();
//        if (color == ChessGame.TeamColor.WHITE) {
//            if (row == 2) {
//                ChessPosition newPosition = new ChessPosition(row + 1, col);
//                if (board.getPiece(newPosition) == null) {
//                    totalMoves.add(new ChessMove(position, newPosition, null));
//                    newPosition = new ChessPosition(row + 2, col);
//                    if (board.getPiece(newPosition) == null) {
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//                }
//            } else {
//                int newRow = row + 1;
//                ChessPosition newPosition = new ChessPosition(newRow, col);
//                if (newRow == 8 && board.getPiece(newPosition) == null) {
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                } else {
//                    if (MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn()) && board.getPiece(newPosition) == null) {
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//
//                    //white
//
//                }
//            }
//        }
//
//        //BLACK
//        if (color == ChessGame.TeamColor.BLACK) {
//            if (row == 7) {
//                ChessPosition newPosition = new ChessPosition(row - 1, col);
//                if (board.getPiece(newPosition) == null) {
//                    totalMoves.add(new ChessMove(position, newPosition, null));
//                    newPosition = new ChessPosition(row - 2, col);
//                    if (board.getPiece(newPosition) == null) {
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//                }
//            }else {
//                int newRow = row - 1;
//                ChessPosition newPosition = new ChessPosition(newRow, col);
//                if (newRow == 1 && board.getPiece(newPosition) == null) {
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
//                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                } else {
//                    if (MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn()) && board.getPiece(newPosition) == null) {
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//
//                    //white
//
//                }
//            }
//
//        }
//
//
//        //white capture logic
//        //right diag
//        if(color == ChessGame.TeamColor.WHITE){
//            int newRow = row + 1;
//            int newCol = col + 1;
//            ChessPosition newPosition = new ChessPosition(newRow, newCol);
//            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
//                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.WHITE){
//                    if(newRow == 8){
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                    }else{
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//                }
//            }
//
//        }
//
//        //white capture
//        //left diag
//        if(color == ChessGame.TeamColor.WHITE){
//            int newRow = row + 1;
//            int newCol = col - 1;
//            ChessPosition newPosition = new ChessPosition(newRow, newCol);
//            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
//                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.WHITE){
//                    if(newRow == 8){
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                    }else{
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//                }
//            }
//
//        }
//
//        //black
//        //black right
//        if(color == ChessGame.TeamColor.BLACK){
//            int newRow = row - 1;
//            int newCol = col + 1;
//            ChessPosition newPosition = new ChessPosition(newRow, newCol);
//            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
//                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.BLACK){
//                    if(newRow == 1){
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                    }else{
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//                }
//            }
//
//        }
//
//        //black
//        //black left
//        if(color == ChessGame.TeamColor.BLACK){
//            int newRow = row - 1;
//            int newCol = col - 1;
//            ChessPosition newPosition = new ChessPosition(newRow, newCol);
//            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
//                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.BLACK){
//                    if(newRow == 1){
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
//                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                    }else{
//                        totalMoves.add(new ChessMove(position, newPosition, null));
//                    }
//                }
//            }
//
//        }
//
//        return totalMoves;
//    }
}


