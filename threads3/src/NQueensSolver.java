import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class NQueensSolver {
    private int boardSize;
    private List<int[]> solutions;

    public NQueensSolver(int boardSize) {
        this.boardSize = boardSize;
        this.solutions = Collections.synchronizedList(new ArrayList<>());
    }

    public long solve(int numberOfThreads) {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < boardSize; i++) {
            int[] initial = new int[boardSize];
            initial[0] = i;
            executor.execute(new WorkerThread(initial, 1));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private class WorkerThread implements Runnable {
        private int[] state;
        private int row;

        WorkerThread(int[] state, int row) {
            this.state = state.clone();
            this.row = row;
        }

        @Override
        public void run() {
            if (row == boardSize) {
                synchronized (solutions) {
                    solutions.add(state.clone());
                }
            } else {
                for (int i = 0; i < boardSize; i++) {
                    if (isValid(state, row, i)) {
                        state[row] = i;
                        new WorkerThread(state, row + 1).run();
                    }
                }
            }
        }

        private boolean isValid(int[] state, int row, int col) {
            for (int i = 0; i < row; i++) {
                if (state[i] == col || Math.abs(state[i] - col) == row - i) {
                    return false;
                }
            }
            return true;
        }
    }

    public List<int[]> getSolutions() {
        return solutions;
    }

    public static void main(String[] args) {
        int n[] = {4, 8, 10}; // Default board size
        int[] threadCounts = {1, 2, 4, 8, 16}; // Different numbers of threads to test



        System.out.println("N-Queens Problem Solver");

        for(int i : n){
            System.out.println("Board size: " + i);
            for (int threads : threadCounts) {
                NQueensSolver solver = new NQueensSolver(i);
                long timeTaken = solver.solve(threads);
                System.out.println("Threads: " + threads + " | Time taken: " + timeTaken + " ms | Solutions found: " + solver.getSolutions().size());
            }

        }

    }
}
