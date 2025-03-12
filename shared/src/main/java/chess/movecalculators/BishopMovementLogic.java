package chess.movecalculators;

import java.util.HashSet;

import chess.*;

import static chess.movecalculators.MoveCalculator.calculateMoves;


public class BishopMovementLogic {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        int [][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return calculateMoves(board, position, directions, true);
    }

}
