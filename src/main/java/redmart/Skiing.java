package redmart;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * http://geeks.redmart.com/2015/01/07/skiing-in-singapore-a-coding-diversion/
 */
public class Skiing {
    public static String longestPath(Path path) throws IOException {
        Matrix matrix = buildMatrix(path);
        int maxLength = 0;
        Map<String, Integer> memo = new HashMap<>();
        for (int row = 0; row < matrix.matrix.length; row++) {
            for (int col = 0; col < matrix.matrix[row].length; col++) {
//                System.out.println("row=" + row + ", col=" + col);
                int length = longestPath(matrix.matrix, matrix.maxRow, matrix.maxCol, row, col,
                    Integer.MAX_VALUE, new HashSet<>(), memo);
                maxLength = Math.max(maxLength, length);
            }
        }
        System.out.println("length=" + maxLength);
        return "";
    }

    private static int longestPath(int[][] matrix, int maxRow, int maxCol, int row, int col,
                                   int prevVal, Set<String> visited, Map<String, Integer> memo) {
        if (row < 0 || row >= maxRow || col < 0 || col >= maxCol) {
            return 0;
        }
        int val = matrix[row][col];
        if (prevVal <= val) {
            return 0;
        }
        String key = row + "|" + col;
        if (visited.contains(key)) {
            return 0;
        }
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        Set<String> newVisited = new HashSet<>(visited);
        newVisited.add(key);
        int maxLength = 0;
        // north
        int length = longestPath(matrix, maxRow, maxCol, row - 1, col, val, newVisited, memo);
        maxLength = Math.max(maxLength, length);
        // west
        length = longestPath(matrix, maxRow, maxCol, row, col - 1, val, newVisited, memo);
        maxLength = Math.max(maxLength, length);
        // south
        length = longestPath(matrix, maxRow, maxCol, row + 1, col, val, newVisited, memo);
        maxLength = Math.max(maxLength, length);
        // east
        length = longestPath(matrix, maxRow, maxCol, row, col + 1, val, newVisited, memo);
        maxLength = Math.max(maxLength, length);

        maxLength++;
        memo.put(key, maxLength);

        return maxLength;
    }

    private static class Matrix {
        private final int maxRow;
        private final int maxCol;
        private final int[][] matrix;

        public Matrix(int maxRow, int maxCol, int[][] matrix) {
            this.maxRow = maxRow;
            this.maxCol = maxCol;
            this.matrix = matrix;
        }
    }

    private static Matrix buildMatrix(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path);
        String firstLine = reader.readLine();
        String[] splitStr = firstLine.split("\\s+");
        int row = Integer.parseInt(splitStr[0]);
        int col = Integer.parseInt(splitStr[1]);
        int[][] matrix = new int[row][col];
        int i = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            String[] ss = line.split("\\s+");
            int j = 0;
            for (String s : ss) {
                matrix[i][j++] = Integer.parseInt(s);
            }
            i++;
        }
        return new Matrix(row, col, matrix);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(longestPath(Paths.get("map-small.txt")));
//        System.out.println(longestPath(Paths.get("map-large.txt")));
    }
}
