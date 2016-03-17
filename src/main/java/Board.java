import java.util.ArrayList;
import java.util.Arrays;

/**
 * Alexander Artamonov (asartamonov@gmail.com) 2016.
 */
public class Board {
    private final int[][] blocks;
    private final int size;
    private int iEmp;
    private int jEmp;

    /**
     * Construct a board from an N-by-N array of blocks
     * (where blocks[i][j] = block in row i, column j)
     */
    public Board(int[][] blocks) {
        if (blocks == null)
            throw new IllegalArgumentException("\nCan't build board from null\n");
        for (int i = 0; i < blocks.length; i++) {
            int emptyCells = 0;
            if (blocks.length != blocks[i].length)
                throw new IllegalArgumentException("\nCan't build not squared board\n");
            for (int j = 0; j < blocks.length; j++) {
                int testing = blocks[i][j];
                if (testing > Math.pow(blocks.length, 2) - 1)
                    throw new IllegalArgumentException("\nValue in cell is too large\n");
                if (testing == 0 && emptyCells++ > 0)
                    throw new IllegalArgumentException("\nMore than one empty cell\n");
            }
        }
        this.blocks = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < this.blocks.length; i++)
            this.blocks[i] = Arrays.copyOf(blocks[i], blocks[i].length);
        size = blocks.length;
    }


    public int dimension() {
        return size;
    }

    /**
     * Hamming priority function.
     * The number of blocks in the wrong position, plus the number of moves
     * made so far to get to the search node. Intuitively, a search node with a small
     * number of blocks in the wrong position is close to the goal, and we prefer a search
     * node that have been reached using a small number of moves.
     *
     * @return number of blocks out of place
     */
    public int hamming() {
        int wrongPositionCounter = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (blocks[i][j] != 0 && blocks[i][j] != i * size + j + 1)
                    wrongPositionCounter++;
        return wrongPositionCounter;
    }

    /**
     * Manhattan priority function. The sum of the Manhattan distances
     * (sum of the vertical and horizontal distance) from the blocks to
     * their goal positions, plus the number of moves made so far to get to the search node.
     *
     * @return sum of Manhattan distances between blocks and goal
     */
    public int manhattan() {
        int manhattanDistances = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                int value = blocks[i][j];
                if (value == 0) continue;
                int iGoal = (value - 1) / size;
                int jGoal = (value - iGoal * size) - 1;
                manhattanDistances += Math.abs(i - iGoal) + Math.abs(j - jGoal);
            }
        return manhattanDistances;
    }

    /**
     * is this board the goal board?
     *
     * @return true if and only if all blocks on their places
     */
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    /**
     * a board that is obtained by exchanging any pair of blocks
     *
     * @return new Board, twin to this.Board
     */
    public Board twin() {
        int[][] newBlocks = Arrays.copyOf(this.blocks, this.blocks.length);
        for (int i = 0; i < this.blocks.length; i++)
            newBlocks[i] = Arrays.copyOf(this.blocks[i], this.blocks[i].length);
        int i = 0, j = 0;
        while (newBlocks[i][j] == 0 || newBlocks[i][j + 1] == 0) {
            i++;
            //if (++i == size)
            //    throw new IllegalArgumentException("More than one empty space in board");
        }
        newBlocks = exchange(newBlocks, i, j, i, j + 1);
        return new Board(newBlocks);
    }

    public boolean equals(Object y) {
        if (y == null)
            return false;
        if (y.getClass() == String.class)
            return this.toString().equals(y);
        if (!(y.getClass().isInstance(this)))
            throw new IllegalArgumentException();
        Board that = (Board) y;
        if (this.size != that.size)
            return false;
        return Arrays.deepEquals(this.blocks, that.blocks);
    }

    /**
     * Method generates array of all neighboring boards
     */
    private enum Positions {
        TOPLEFTCORNER, TOPROW, TOPRIGHTCORNER,
        RIGHTROW,
        BOTTOMRIGHTCORNER, BOTTOMROW, BOTTOMLEFTCORNER,
        LEFTROW,
        MIDDLE
    }

    private Positions getPositionByCorrdinates(int i, int j) {
        if (i == 0) { //top row
            if (j == 0)
                return Positions.TOPLEFTCORNER;
            else if (j == size - 1)
                return Positions.TOPRIGHTCORNER;
            else if (j > 0 && j < size - 1)
                return Positions.TOPROW;
        } else if (i == size - 1) { //bottom row
            if (j == 0)
                return Positions.BOTTOMLEFTCORNER;
            else if (j == size - 1)
                return Positions.BOTTOMRIGHTCORNER;
            else if (j > 0 && j < size - 1)
                return Positions.BOTTOMROW;
        } else if (j == 0 && i > 0 && i < size - 1)
            return Positions.LEFTROW;
        else if (j == size - 1 && i > 0 && i < size - 1)
            return Positions.RIGHTROW;
        return Positions.MIDDLE;
    }

    public Iterable<Board> neighbors() {
        ArrayList<Board> neighboringBoards = new ArrayList<>();
        /**
         * Find zero
         * descriminate zero position as: topleft, topright, top row etc.
         * perform all possible moves according to zero position and add it to array to return
         */
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (blocks[i][j] != 0)
                    continue;
                iEmp = i;
                jEmp = j;
            }
        Positions emptyPosition = getPositionByCorrdinates(iEmp, jEmp);
        switch (emptyPosition) {
            case TOPLEFTCORNER:
                neighboringBoards.add(new Board(righterToLeft()));
                neighboringBoards.add(new Board(downerUp()));
                neighboringBoards.trimToSize();
                break;
            case TOPROW:
                neighboringBoards.add(new Board(lefterToRight()));
                neighboringBoards.add(new Board(downerUp()));
                neighboringBoards.add(new Board(righterToLeft()));
                neighboringBoards.trimToSize();
                break;
            case TOPRIGHTCORNER:
                neighboringBoards.add(new Board(lefterToRight()));
                neighboringBoards.add(new Board(downerUp()));
                neighboringBoards.trimToSize();
                break;
            case RIGHTROW:
                neighboringBoards.add(new Board(upperDown()));
                neighboringBoards.add(new Board(lefterToRight()));
                neighboringBoards.add(new Board(downerUp()));
                neighboringBoards.trimToSize();
                break;
            case BOTTOMRIGHTCORNER:
                neighboringBoards.add(new Board(upperDown()));
                neighboringBoards.add(new Board(lefterToRight()));
                neighboringBoards.trimToSize();
                break;
            case BOTTOMROW:
                neighboringBoards.add(new Board(upperDown()));
                neighboringBoards.add(new Board(lefterToRight()));
                neighboringBoards.add(new Board(righterToLeft()));
                neighboringBoards.trimToSize();
                break;
            case BOTTOMLEFTCORNER:
                neighboringBoards.add(new Board(righterToLeft()));
                neighboringBoards.add(new Board(upperDown()));
                neighboringBoards.trimToSize();
                break;
            case LEFTROW:
                neighboringBoards.add(new Board(upperDown()));
                neighboringBoards.add(new Board(righterToLeft()));
                neighboringBoards.add(new Board(downerUp()));
                neighboringBoards.trimToSize();
                break;
            case MIDDLE:
                neighboringBoards.add(new Board(upperDown()));
                neighboringBoards.add(new Board(righterToLeft()));
                neighboringBoards.add(new Board(downerUp()));
                neighboringBoards.add(new Board(lefterToRight()));
                neighboringBoards.trimToSize();
                break;
        }
        return neighboringBoards;
    }

    private int[][] lefterToRight() {
        int[][] nBoard = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < nBoard.length; i++)
            nBoard[i] = Arrays.copyOf(blocks[i], blocks[i].length);
        nBoard = exchange(nBoard, iEmp, jEmp, iEmp, jEmp - 1);
        return nBoard;
    }

    private int[][] righterToLeft() {
        int[][] nBoard = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < nBoard.length; i++)
            nBoard[i] = Arrays.copyOf(blocks[i], blocks[i].length);
        nBoard = exchange(nBoard, iEmp, jEmp, iEmp, jEmp + 1);
        return nBoard;
    }

    private int[][] downerUp() {
        int[][] nBoard = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < nBoard.length; i++)
            nBoard[i] = Arrays.copyOf(blocks[i], blocks[i].length);
        nBoard = exchange(nBoard, iEmp, jEmp, iEmp + 1, jEmp);
        return nBoard;
    }

    private int[][] upperDown() {
        int[][] nBoard = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < nBoard.length; i++)
            nBoard[i] = Arrays.copyOf(blocks[i], blocks[i].length);
        nBoard = exchange(nBoard, iEmp, jEmp, iEmp - 1, jEmp);
        return nBoard;
    }

    private int[][] exchange(int[][] a, int i, int j, int k, int l) {
        int[][] nBoard = Arrays.copyOf(a, a.length);
        for (int row = 0; row < nBoard.length; row++)
            nBoard[row] = Arrays.copyOf(a[row], a[row].length);
        int tmp = nBoard[i][j];
        nBoard[i][j] = nBoard[k][l];
        nBoard[k][l] = tmp;
        return nBoard;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(size);
        s.append("\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                s.append(String.format("%2d ", this.blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Few module tests.
     *
     * @param args
     */
    public static void main(String[] args) {
        int[][] blocksTestOne;
        blocksTestOne = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 0, 8},
        };
        Board testOne = new Board(blocksTestOne);
        blocksTestOne[0][0] = 6;
        assert testOne.size == 3;
        assert testOne.manhattan() == 1;
        assert testOne.hamming() == 1;
        int[][] blocksTestOneTwin = {
                {2, 1, 3},
                {4, 5, 6},
                {7, 0, 8},
        };
        assert (testOne.twin().equals(new Board(blocksTestOneTwin)));
        //neighbors check (one by one)
        ArrayList<Board> testOneNeighbors = (ArrayList<Board>) testOne.neighbors();
        assert testOneNeighbors.size() == 3;
        assert testOneNeighbors.get(0).equals(new Board(new int[][]{
                {1, 2, 3},
                {4, 0, 6},
                {7, 5, 8},
        })); //upperdown
        assert testOneNeighbors.get(1).equals(new Board(new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {0, 7, 8},
        })); //lefttoright
        assert testOneNeighbors.get(2).equals(new Board(new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0},
        })); // righttoleft
        int[][] blocksTestOneTwinTw = {
                {1, 3, 2},
                {4, 5, 6},
                {7, 0, 8},
        };
        //equality and non-equality check
        assert new Board(blocksTestOneTwin).equals(new Board(blocksTestOneTwinTw)) == false;
        assert new Board(blocksTestOneTwin).equals(new Board(blocksTestOneTwin)) == true;
    }
}
