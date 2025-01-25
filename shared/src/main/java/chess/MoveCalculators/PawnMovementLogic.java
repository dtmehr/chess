package chess.MoveCalculators;

import chess.*;
import chess.ChessMove;
import chess.ChessGame;

import java.util.HashSet;

public class PawnMovementLogic {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> totalMoves = new HashSet<>();
        //starting position
        int row = position.getRow();
        int col = position.getColumn();

        ChessPiece piece = board.getPiece(position);

        //WHITE
        ChessGame.TeamColor color = piece.getTeamColor();
        if (color == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                ChessPosition newPosition = new ChessPosition(row + 1, col);
                if (board.getPiece(newPosition) == null) {
                    totalMoves.add(new ChessMove(position, newPosition, null));
                    newPosition = new ChessPosition(row + 2, col);
                    if (board.getPiece(newPosition) == null) {
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }
                }
            } else {
                int newRow = row + 1;
                ChessPosition newPosition = new ChessPosition(newRow, col);
                if (newRow == 8 && board.getPiece(newPosition) == null) {
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                } else {
                    if (MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn()) && board.getPiece(newPosition) == null) {
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }

                    //white

                }
            }
        }

        //BLACK
        if (color == ChessGame.TeamColor.BLACK) {
            if (row == 7) {
                ChessPosition newPosition = new ChessPosition(row - 1, col);
                if (board.getPiece(newPosition) == null) {
                    totalMoves.add(new ChessMove(position, newPosition, null));
                    newPosition = new ChessPosition(row - 2, col);
                    if (board.getPiece(newPosition) == null) {
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }else {
                int newRow = row - 1;
                ChessPosition newPosition = new ChessPosition(newRow, col);
                if (newRow == 1 && board.getPiece(newPosition) == null) {
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                    totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                } else {
                    if (MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn()) && board.getPiece(newPosition) == null) {
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }

                    //white

                }
            }

        }


        //white capture logic
        //right diag
        if(color == ChessGame.TeamColor.WHITE){
            int newRow = row + 1;
            int newCol = col + 1;
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.WHITE){
                    if(newRow == 8){
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    }else{
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }

        }

        //white capture
        //left diag
        if(color == ChessGame.TeamColor.WHITE){
            int newRow = row + 1;
            int newCol = col - 1;
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.WHITE){
                    if(newRow == 8){
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    }else{
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }

        }

        //black
        //black right
        if(color == ChessGame.TeamColor.BLACK){
            int newRow = row - 1;
            int newCol = col + 1;
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.BLACK){
                    if(newRow == 1){
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    }else{
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }

        }

        //black
        //black left
        if(color == ChessGame.TeamColor.BLACK){
            int newRow = row - 1;
            int newCol = col - 1;
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if(MoveCalculator.isInBounds(newPosition.getRow(), newPosition.getColumn())){
                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != ChessGame.TeamColor.BLACK){
                    if(newRow == 1){
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                        totalMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    }else{
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }

        }

        return totalMoves;
    }
}


