package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

import static chess.movecalculators.MoveCalculator.calculateMoves;

public class QueenMovementLogic {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        int [][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        return calculateMoves(board, position, directions, true);
    }
}
