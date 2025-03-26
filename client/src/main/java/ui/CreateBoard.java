package ui;

public class CreateBoard {

    public static void drawBoard(boolean whitePerspective) {
        String[][] board = new String[8][8];

        for (int row = 0; row < 8; row++) {
            for (int c = 0; c < 8; c++) {
                board[row][c] = EscapeSequences.EMPTY;
            }
        }
        board[0][0] = EscapeSequences.WHITE_ROOK;
        board[0][1] = EscapeSequences.WHITE_KNIGHT;
        board[0][2] = EscapeSequences.WHITE_BISHOP;
        board[0][3] = EscapeSequences.WHITE_QUEEN;
        board[0][4] = EscapeSequences.WHITE_KING;
        board[0][5] = EscapeSequences.WHITE_BISHOP;
        board[0][6] = EscapeSequences.WHITE_KNIGHT;
        board[0][7] = EscapeSequences.WHITE_ROOK;
        for (int c = 0; c < 8; c++) {
            board[1][c] = EscapeSequences.WHITE_PAWN;
        }

        board[7][0] = EscapeSequences.BLACK_ROOK;
        board[7][1] = EscapeSequences.BLACK_KNIGHT;
        board[7][2] = EscapeSequences.BLACK_BISHOP;
        board[7][3] = EscapeSequences.BLACK_QUEEN;
        board[7][4] = EscapeSequences.BLACK_KING;
        board[7][5] = EscapeSequences.BLACK_BISHOP;
        board[7][6] = EscapeSequences.BLACK_KNIGHT;
        board[7][7] = EscapeSequences.BLACK_ROOK;
        for (int c = 0; c < 8; c++) {
            board[6][c] = EscapeSequences.BLACK_PAWN;
        }
//use other file color stuff
        if (whitePerspective) {
            for (int r = 7; r >= 0; r--) {
                for (int c = 0; c < 8; c++) {
                    String bgColor = ((r + c) % 2 == 0) ?
                            EscapeSequences.SET_BG_COLOR_BLACK :
                            EscapeSequences.SET_BG_COLOR_WHITE;
                    System.out.print(bgColor + board[r][c] + EscapeSequences.RESET_BG_COLOR);
                }
                System.out.println();
            }
        } else {
            for (int r = 0; r < 8; r++) {
                for (int c = 7; c >= 0; c--) {
                    String bgColor = ((r + c) % 2 == 0) ?
                            EscapeSequences.SET_BG_COLOR_BLACK :
                            EscapeSequences.SET_BG_COLOR_WHITE;
                    System.out.print(bgColor + board[r][c] + EscapeSequences.RESET_BG_COLOR);
                }
                System.out.println();
            }
        }
    }

}
