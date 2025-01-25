package chess.MoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

import static chess.MoveCalculators.MoveCalculator.calculateMoves;

public class RookMovementLogic {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        int [][] directions = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        return calculateMoves(board, position, directions, true);
    }
}
