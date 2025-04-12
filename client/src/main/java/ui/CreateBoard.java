package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.List;

public class CreateBoard {

    private static final String HIGHLIGHT_BG = "\u001b[48;5;229m";
    private static final String RESET_ALL = "\u001b[0m";


    public static void drawBoard(boolean whitePerspective, GameData gameData) {
        String[][] board = new String[8][8];
        emptyBoard(board);

        if (gameData == null || gameData.getChessGame() == null) {
            pieces(board);
        } else {
            fillBoardFromGameData(board, gameData);
        }
        boolean[][] highlightCells = new boolean[8][8];
        printHeaders(board, highlightCells, whitePerspective);
    }

    public static void drawBoard(boolean whitePerspective, GameData gameData, List<ChessMove> highlightMoves) {
        String[][] board = new String[8][8];
        emptyBoard(board);

        if (gameData == null || gameData.getChessGame() == null) {
            pieces(board);
        } else {
            fillBoardFromGameData(board, gameData);
        }

        boolean[][] highlightCells = new boolean[8][8];
        if (highlightMoves != null) {
            for (ChessMove move : highlightMoves) {
                int endRow = move.getEndPosition().getRow() - 1;
                int endCol = move.getEndPosition().getColumn() - 1;
                highlightCells[endRow][endCol] = true;
            }
        }

        printHeaders(board, highlightCells, whitePerspective);
    }


    private static void emptyBoard(String[][] board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = EscapeSequences.EMPTY;
            }
        }
    }

    private static void pieces(String[][] board) {
        // white
        board[0][0] = EscapeSequences.WHITE_ROOK;
        board[0][1] = EscapeSequences.WHITE_KNIGHT;
        board[0][2] = EscapeSequences.WHITE_BISHOP;
        board[0][3] = EscapeSequences.WHITE_QUEEN;
        board[0][4] = EscapeSequences.WHITE_KING;
        board[0][5] = EscapeSequences.WHITE_BISHOP;
        board[0][6] = EscapeSequences.WHITE_KNIGHT;
        board[0][7] = EscapeSequences.WHITE_ROOK;
        for (int col = 0; col < 8; col++) {
            board[1][col] = EscapeSequences.WHITE_PAWN;
        }
        // black
        board[7][0] = EscapeSequences.BLACK_ROOK;
        board[7][1] = EscapeSequences.BLACK_KNIGHT;
        board[7][2] = EscapeSequences.BLACK_BISHOP;
        board[7][3] = EscapeSequences.BLACK_QUEEN;
        board[7][4] = EscapeSequences.BLACK_KING;
        board[7][5] = EscapeSequences.BLACK_BISHOP;
        board[7][6] = EscapeSequences.BLACK_KNIGHT;
        board[7][7] = EscapeSequences.BLACK_ROOK;
        for (int col = 0; col < 8; col++) {
            board[6][col] = EscapeSequences.BLACK_PAWN;
        }
    }


    private static void fillBoardFromGameData(String[][] board, GameData gameData) {
        ChessGame chess = gameData.getChessGame();
        ChessBoard cb = chess.getBoard();

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = cb.getPiece(new ChessPosition(r, c));
                if (piece != null) {
                    board[r - 1][c - 1] = icon(piece);
                }
            }
        }
    }

    private static void printSquare(String cell, boolean highlight, int row, int col) {
        if (highlight) {
            // yellow background
            System.out.print(HIGHLIGHT_BG + cell + RESET_ALL);
        } else {
            // checkerboard
            boolean isBlackSquare = ((row + col) % 2 == 0);
            String bgColor = isBlackSquare
                    ? EscapeSequences.SET_BG_COLOR_BLACK
                    : EscapeSequences.SET_BG_COLOR_WHITE;
            System.out.print(bgColor + cell + EscapeSequences.RESET_BG_COLOR);
        }
    }

    private static void printHeaders(String[][] board, boolean[][] highlightCells, boolean whitePerspective) {
        char[] columns = whitePerspective
                ? new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}
                : new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        // order
        int start, end, step;
        if (whitePerspective) {
            start = 7; end = -1; step = -1;
        } else {
            start = 0; end = 8; step = 1;
        }

        // top headers
        System.out.print("  ");
        for (char col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();

        // go row by row with numbers
        for (int row = start; row != end; row += step) {
            int rowLabel = row + 1;
            System.out.print(rowLabel + " ");
            if (whitePerspective) {
                for (int col = 0; col < 8; col++) {
                    printSquare(board[row][col], highlightCells[row][col], row, col);
                }
            } else {
                for (int col = 7; col >= 0; col--) {
                    printSquare(board[row][col], highlightCells[row][col], row, col);
                }
            }
            System.out.println(" " + rowLabel);
        }

        // bottom letters
        System.out.print("  ");
        for (char col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
    }


    private static String icon(ChessPiece piece) {
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);

        return switch (type) {
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            default ->
                    isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}
