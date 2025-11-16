import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class RubiksCube {

    public char[][] RubiksCube = new char[9][12];

    /**
     * default constructor
     * Creates a Rubik's Cube in an initial state:
     * OOO
     * OOO
     * OOO
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     * RRR
     * RRR
     * RRR
     */
    public RubiksCube() {
        // TODO implement me
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 12; j++) {
                if (j > 2 && j < 6 && i < 3) RubiksCube[i][j] = 'O';
                else if (j < 3 && i > 2 && i < 6) RubiksCube[i][j] = 'G';
                else if (j > 2 && j < 6 && i > 2 && i < 6) RubiksCube[i][j] = 'W';
                else if (j > 5 && j < 9 && i > 2 && i < 6) RubiksCube[i][j] = 'B';
                else if (j > 8 && i > 2 && i < 6) RubiksCube[i][j] = 'Y';
                else if (j > 2 && j < 6 && i > 5) RubiksCube[i][j] = 'R';
                else RubiksCube[i][j] = ' ';
            }
        }
    }

    /**
     * @param fileName
     * @throws IOException
     * @throws CubeFormatException Creates a Rubik's Cube from the description in fileName
     */
    public RubiksCube(String fileName) throws IOException, CubeFormatException {
        // TODO implement me
        RubiksCube = new char[9][12];
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < 9) {
                // validate line length
                if ((row < 3 || row >= 6) && line.length() != 6) {
                    throw new CubeFormatException("Expected 6 chars in row " + row);
                }
                if (row >= 3 && row < 6 && line.length() != 12) {
                    throw new CubeFormatException("Expected 12 chars in row " + row);
                }
                // copy characters into 2D array
                for (int col = 0; col < line.length(); col++) {
                    RubiksCube[row][col] = line.charAt(col);
                }
                row++;
            }
            // check actually read 9 rows
            if (row != 9) {
                throw new CubeFormatException("Expected 9 rows in the file");
            }
        }
    }

    // --- small helpers ---
    private int idx(int r, int c) {
        return r * 12 + c; // Index calc of 2d array 
    } // r:0..8, c:0..11

    private void cycle4(int a, int b, int c, int d) {
        char[][] g = RubiksCube;
        int ra = a / 12, ca = a % 12, rb = b / 12, cb = b % 12, rc = c / 12, cc = c % 12, rd = d / 12, cd = d % 12;
        char t = g[ra][ca];
        g[ra][ca] = g[rd][cd];
        g[rd][cd] = g[rc][cc];
        g[rc][cc] = g[rb][cb];
        g[rb][cb] = t;
    }

    private void cycleBy(int[] P, int k) {             // rotate P forward by k
        k %= P.length;
        if (k == 0) return;
        for (int t = 0; t < k; t++) {
            char last = RubiksCube[P[P.length - 1] / 12][P[P.length - 1] % 12];
            for (int i = P.length - 1; i > 0; i--) {
                int rFrom = P[i - 1] / 12, cFrom = P[i - 1] % 12;
                int rTo = P[i] / 12, cTo = P[i] % 12;
                RubiksCube[rTo][cTo] = RubiksCube[rFrom][cFrom];
            }
            RubiksCube[P[0] / 12][P[0] % 12] = last;
        }
    }

    // rotate a 3x3 face at (r0,c0) clockwise using 4-cycles (compact & in-place)
    private void rotateFaceCW(int r0, int c0) {
        // corners
        cycle4(idx(r0, c0), idx(r0, c0 + 2), idx(r0 + 2, c0 + 2), idx(r0 + 2, c0));
        // edges
        cycle4(idx(r0, c0 + 1), idx(r0 + 1, c0 + 2), idx(r0 + 2, c0 + 1), idx(r0 + 1, c0));
    }

    private void moveF() {
        rotateFaceCW(3, 3);
        int[] ring = {
                idx(2, 3), idx(2, 4), idx(2, 5),        // U bottom (L->R)
                idx(3, 6), idx(4, 6), idx(5, 6),        // R left   (T->B)
                idx(6, 5), idx(6, 4), idx(6, 3),        // D top    (R->L)  <- reversed to match orientation
                idx(5, 2), idx(4, 2), idx(3, 2)         // L right  (B->T)
        };
        cycleBy(ring, 3);
    }

    private void moveB() {
        rotateFaceCW(3, 9);
        int[] ring = {
                idx(0, 5), idx(0, 4), idx(0, 3),        // U top    (R->L)  <- reversed
                idx(3, 8), idx(4, 8), idx(5, 8),        // R right  (T->B)
                idx(8, 3), idx(8, 4), idx(8, 5),        // D bottom (L->R)
                idx(5, 0), idx(4, 0), idx(3, 0)         // L left   (B->T)  <- reversed
        };
        cycleBy(ring, 3);
    }

    private void moveL() {
        rotateFaceCW(3, 0);
        int[] ring = {
                idx(0, 3), idx(1, 3), idx(2, 3),        // U left   (T->B)
                idx(3, 3), idx(4, 3), idx(5, 3),        // F left   (T->B)
                idx(8, 3), idx(7, 3), idx(6, 3),        // D left   (B->T)  <- reversed
                idx(5, 11), idx(4, 11), idx(3, 11)      // B right  (B->T)  <- reversed (B is "upside down")
        };
        cycleBy(ring, 3);
    }

    private void moveR() {
        rotateFaceCW(3, 6);
        int[] ring = {
                idx(0, 5), idx(1, 5), idx(2, 5),        // U right  (T->B)
                idx(5, 9), idx(4, 9), idx(3, 9),        // B left   (B->T)  <- reversed
                idx(6, 5), idx(7, 5), idx(8, 5),        // D right  (T->B)
                idx(3, 5), idx(4, 5), idx(5, 5)         // F right  (T->B)
        };
        cycleBy(ring, 3);
    }

    private void moveU() {
        rotateFaceCW(0, 3);
        int[] ring = {
                idx(3, 0), idx(3, 1), idx(3, 2),        // L top (L->R)
                idx(3, 3), idx(3, 4), idx(3, 5),        // F top
                idx(3, 6), idx(3, 7), idx(3, 8),        // R top
                idx(3, 9), idx(3, 10), idx(3, 11)       // B top
        };
        cycleBy(ring, 3); // L->F->R->B
    }

    private void moveD() {
        rotateFaceCW(6, 3);
        int[] ring = {
                idx(5, 3), idx(5, 4), idx(5, 5),        // F bottom
                idx(5, 6), idx(5, 7), idx(5, 8),        // R bottom
                idx(5, 9), idx(5, 10), idx(5, 11),      // B bottom
                idx(5, 0), idx(5, 1), idx(5, 2)         // L bottom
        };
        cycleBy(ring, 3); // F->R->B->L
    }

    /**
     * @param moves Applies the sequence of moves on the Rubik's Cube
     */
    public void applyMoves(String moves) {
        for (int i = 0; i < moves.length(); i++) {
            switch (moves.charAt(i)) {
                case 'F':
                    moveF();
                    break;
                case 'B':
                    moveB();
                    break;
                case 'L':
                    moveL();
                    break;
                case 'R':
                    moveR();
                    break;
                case 'U':
                    moveU();
                    break;
                case 'D':
                    moveD();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * returns true if the current state of the Cube is solved,
     * i.e., it is in this state:
     * OOO
     * OOO
     * OOO
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     * RRR
     * RRR
     * RRR
     */
    public boolean isSolved() {
        // TODO implement me
        String solvedState =
                "   OOO\n" +
                "   OOO\n" +
                "   OOO\n" +
                "GGGWWWBBBYYY\n" +
                "GGGWWWBBBYYY\n" +
                "GGGWWWBBBYYY\n" +
                "   RRR\n" +
                "   RRR\n" +
                "   RRR\n";

        return this.toString().equals(solvedState);
    }


    @Override
    public String toString() {
        // TODO implement me
        StringBuilder str = new StringBuilder();
        int char_amount = 0;
        for (int i = 0; i < 9; i++) {
            if (i < 3 || i > 5) {
                char_amount = 6;
            } else {
                char_amount = 12;
            }
            for (int k = 0; k < char_amount; k++) {
                str.append(RubiksCube[i][k]);
            }
            str.append('\n');
        }
        return str.toString();
    }

    /**
     *
     * @param moves
     * @return the order of the sequence of moves
     */
    public static int order(String moves) {
        if (moves == null || moves.isEmpty()) {
            return 1; // Empty sequence has order 1
        }

        // Create a cube in the initial state
        RubiksCube cube = new RubiksCube();

        // Store the initial state for comparison
        String initialState = cube.toString();

        int count = 0;
        do {
            cube.applyMoves(moves);
            count++;

            // Safety check to prevent infinite loops
            if (count > 10000) {
                return -1; // Return -1 to indicate order is too large or infinite
            }

        } while (!cube.toString().equals(initialState));

        return count;
    }
}
