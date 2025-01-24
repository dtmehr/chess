package chess.MoveCalculators;

import java.util.HashSet;

import chess.*;

import static chess.MoveCalculators.MoveCalculator.calculateMoves;


public class BishopMovementLogic {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        int [][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return calculateMoves(board, position, directions);
    }

}
