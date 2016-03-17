import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.List;

/**
 * Alexander Artamonov (asartamonov@gmail.com) 2016.
 */
public class Solver {
    /**
     * Not all initial boards can lead to the goal board by a sequence of legal moves.
     * To detect such situations, use the fact that boards are divided into two
     * equivalence classes with respect to reachability:
     * (i) those that lead to the goal board and
     * (ii) those that lead to the goal board if we modify the initial board
     * by swapping any pair of blocks (the blank square is not a block).
     * <p>
     * Board public method twin() is for that purposes - to produce twin-board;
     * <p>
     * To apply the fact, run the A* algorithm on two puzzle instances—one with
     * the initial board and one with the initial board modified by
     * swapping a pair of blocks—in lockstep (alternating back and forth
     * between exploring search nodes in each of the two game trees).
     * Exactly one of the two will lead to the goal board.
     */
    private MinPQ<GameState> initMovesQueue = new MinPQ<>();
    private MinPQ<GameState> twinMovesQueue = new MinPQ<>();
    private List<Board> initSolutionBoards = new ArrayList<>();
    private List<Board> twinSolutionBoards = new ArrayList<>();

    private class GameState implements Comparable<GameState> {
        private GameState parentGameState;
        private Board gameBoard;
        private int moveNum;

        public GameState(int moveNum, GameState parentGameState, Board gameBoard) {
            this.moveNum = moveNum;
            this.parentGameState = parentGameState;
            this.gameBoard = gameBoard;
        }

        public int getPriority() {
            return gameBoard.manhattan() + moveNum;
        }

        @Override
        public int compareTo(GameState that) {
            if (that == null)
                throw new IllegalArgumentException();
            return this.getPriority() - that.getPriority();
        }
    }

    /**
     * Class Solver is to find a solution to the initial board (using the A* algorithm)
     * constructor should throw a java.lang.NullPointerException if passed a null argument.
     */
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException(
                    "\nWrong argument for Solver constructor\n");
        initMovesQueue.insert(new GameState(0, null, initial));
        twinMovesQueue.insert(new GameState(0, null, initial.twin()));

        while (!initMovesQueue.min().gameBoard.isGoal() && !twinMovesQueue.min().gameBoard.isGoal()) {
            GameState currentInitGameState = initMovesQueue.delMin();
            for (Board board : currentInitGameState.gameBoard.neighbors()) {
                if (!isAlreadyInPath(currentInitGameState, board))
                    initMovesQueue.insert(
                            new GameState(currentInitGameState.moveNum + 1, currentInitGameState, board));
            }

            GameState currentTwinGameState = twinMovesQueue.delMin();
            for (Board board : currentTwinGameState.gameBoard.neighbors()) {
                if (!isAlreadyInPath(currentTwinGameState, board))
                    twinMovesQueue.insert(
                            new GameState(currentTwinGameState.moveNum + 1, currentTwinGameState, board));
            }
        }
        GameState currentInitGameState = initMovesQueue.delMin();
        initSolutionBoards.add(currentInitGameState.gameBoard);
        while ((currentInitGameState = currentInitGameState.parentGameState) != null)
            initSolutionBoards.add(0, currentInitGameState.gameBoard);
    }

    private static boolean isAlreadyInPath(GameState gameState, Board board) {
        boolean isThere = false;
        GameState checkGameState = gameState;
        while (checkGameState.parentGameState != null) {
            if (checkGameState.parentGameState.gameBoard.equals(board))
                isThere = true;
            checkGameState = checkGameState.parentGameState;
        }
        return isThere;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return initSolutionBoards.get(initSolutionBoards.size() - 1).isGoal();
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (isSolvable())
            return initSolutionBoards.size() - 1;
        else return -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (isSolvable())
            return initSolutionBoards;
        else return null;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        int[][] blocksTestOne = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 0, 8},
        };
        Board testBoardOne = new Board(blocksTestOne);
        Solver testSolverOne = new Solver(testBoardOne);
        assert testSolverOne.isSolvable();
        assert testSolverOne.moves() == 1;
        for (Board b : testSolverOne.solution())
            System.out.println(b);
        int[][] blocksTestTwo = {
                {1, 5, 2},
                {4, 0, 3},
                {7, 8, 6},
        };
        Board testBoardTwo = new Board(blocksTestTwo);
        Solver testSolverTwo = new Solver(testBoardTwo);
        assert testSolverTwo.isSolvable();
        assert testSolverTwo.moves() == 4;
        for (Board b : testSolverTwo.solution())
            System.out.println(b);
    }
}
