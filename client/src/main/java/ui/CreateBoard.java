package ui;

public class CreateBoard {

    public static void drawBoard(boolean whitePerspective) {
        String[][] board = new String[8][8];
        emptyBoard(board);
        pieces(board);
        printHeaders(board, whitePerspective);
    }

    //nothing in it
    private static void emptyBoard(String[][] board) {
        //same for loop as always
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                //set to empty from file
                board[row][col] = EscapeSequences.EMPTY;
            }
        }
    }

    //
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


    private static void printHeaders(String[][] board, boolean whitePerspective) {
        char[] columns = whitePerspective
                ? new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}
                : new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

        // top
        System.out.print("  ");
        for (char col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();

        // order
        int start, end, step;
        if (whitePerspective) {
            start = 7; end = -1; step = -1;
        } else {
            start = 0; end = 8; step = 1;
        }

        // Print each row with row numbers on the left and right.
        for (int row = start; row != end; row += step) {
            int rowLabel = whitePerspective ? row + 1 : 8 - row;
            System.out.print(rowLabel + " ");

            if (whitePerspective) {
                for (int column = 0; column < 8; column++) {
                    String bgColor = ((row + column) % 2 == 0)
                            ? EscapeSequences.SET_BG_COLOR_BLACK
                            : EscapeSequences.SET_BG_COLOR_WHITE;
                    System.out.print(bgColor + board[row][column] + EscapeSequences.RESET_BG_COLOR);
                }
            } else {
                for (int c = 7; c >= 0; c--) {
                    String bgColor = ((row + c) % 2 == 0)
                            ? EscapeSequences.SET_BG_COLOR_BLACK
                            : EscapeSequences.SET_BG_COLOR_WHITE;
                    System.out.print(bgColor + board[row][c] + EscapeSequences.RESET_BG_COLOR);
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
}
