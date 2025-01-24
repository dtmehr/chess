package chess.MoveCalculators;


import chess.*;

import java.util.HashSet;

interface MoveCalculator {
    //check that move is in range. basically just holds the valid number ranges
    static boolean isInBounds(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    // meant to create a hashset of possible moves
    //check for in bounds
    //check for opponent pieces in path
    //check for own pieces in path
    //used in every piece logic
    static HashSet<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, int[][] directions){
            // start with the current position of the piece
            int currentRow = position.getRow();
            int currentColumn = position.getColumn();
            // create empty hashset to store the possible move values
            HashSet<ChessMove> totalMoves = new HashSet<>();

            //for loop
            for (int[] direction : directions) {
                int row = currentRow;
                int col = currentColumn;

                while (true) {
                    row += direction[0];
                    col += direction[1];
                    //ensure moves are in bounds (call function created earlier)
                    if (!MoveCalculator.isInBounds(row, col)) {
                        break;
                    }
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(newPosition);
                    // when there is no piece in the spot, add it to the hashset
                    if (piece == null) {
                        totalMoves.add(new ChessMove(position, newPosition, null));
                    } else {
                        ChessPiece currentPiece = board.getPiece(position);
                        //a piece is there and is on the opposite team.
                        //this is the capture logic
                        if (currentPiece != null && !piece.getTeamColor().equals(currentPiece.getTeamColor())) {
                            totalMoves.add(new ChessMove(position, newPosition, null));
                        }
                        break;
                    }
                }
            }
        // this is a hashset of the total possible moves
        return totalMoves;
    }

}
