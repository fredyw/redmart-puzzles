package redmart;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * http://geeks.redmart.com/2015/01/07/skiing-in-singapore-a-coding-diversion/
 */
public class Skiing {
    public static String longestPath(Path path) throws IOException {
        Matrix matrix = buildMatrix(path);
        List<Integer> result = new ArrayList<>();
        Map<String, List<Integer>> memo = new HashMap<>();
        for (int row = 0; row < matrix.matrix.length; row++) {
            for (int col = 0; col < matrix.matrix[row].length; col++) {
                List<Integer> list = longestPath(matrix.matrix, matrix.maxRow, matrix.maxCol,
                    row, col, Integer.MAX_VALUE, new HashSet<>(), memo);
                if (list.size() > 0) {
                    if (result.size() < list.size()) {
                        result = list;
                    } else if (result.size() == list.size()) {
                        int drop1 = result.get(result.size() - 1) - result.get(0);
                        int drop2 = list.get(list.size() - 1) - list.get(0);
                        if (drop1 < drop2) {
                            result = list;
                        }
                    }
                }
            }
        }
        int length = result.size();
        int drop = result.get(result.size() - 1) - result.get(0);
        Collections.reverse(result);
        String pathResult = result.stream()
            .map(i -> Integer.toString(i))
            .collect(Collectors.joining("-"));
        System.out.println("result: " + pathResult);
        return length + "" + drop;
    }

    private static List<Integer> longestPath(int[][] matrix, int maxRow, int maxCol,
                                             int row, int col, int prevVal,
                                             Set<String> visited,
                                             Map<String, List<Integer>> memo) {
        if (row < 0 || row >= maxRow || col < 0 || col >= maxCol) {
            return new ArrayList<>();
        }
        int val = matrix[row][col];
        if (prevVal <= val) {
            return new ArrayList<>();
        }
        String key = row + "|" + col;
        if (visited.contains(key)) {
            return new ArrayList<>();
        }
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        Set<String> newVisited = new HashSet<>(visited);
        newVisited.add(key);

        // north
        List<Integer> north = longestPath(matrix, maxRow, maxCol, row - 1, col, val, newVisited, memo);
        // west
        List<Integer> west = longestPath(matrix, maxRow, maxCol, row, col - 1, val, newVisited, memo);
        // south
        List<Integer> south = longestPath(matrix, maxRow, maxCol, row + 1, col, val, newVisited, memo);
        // east
        List<Integer> east = longestPath(matrix, maxRow, maxCol, row, col + 1, val, newVisited, memo);

        List<Integer> result = new ArrayList<>();
        if (north.size() > 0) {
            if (result.size() < north.size()) {
                result = north;
            } else if (result.size() == north.size()) {
                if (result.get(0) > north.get(0)) {
                    result = north;
                }
            }
        }

        if (west.size() > 0) {
            if (result.size() < west.size()) {
                result = west;
            } else if (result.size() == west.size()) {
                if (result.get(0) > west.get(0)) {
                    result = west;
                }
            }
        }

        if (south.size() > 0) {
            if (result.size() < south.size()) {
                result = south;
            } else if (result.size() == south.size()) {
                if (result.get(0) > south.get(0)) {
                    result = south;
                }
            }
        }

        if (east.size() > 0) {
            if (result.size() < east.size()) {
                result = east;
            } else if (result.size() == east.size()) {
                if (result.get(0) > east.get(0)) {
                    result = east;
                }
            }
        }

        result = new ArrayList<>(result);
        result.add(val);
        memo.put(key, result);
        return result;
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
        try (BufferedReader reader = Files.newBufferedReader(path)) {
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
    }

    public static void main(String[] args) throws IOException {
        System.out.println(longestPath(Paths.get("map-small.txt")));
        System.out.println(longestPath(Paths.get("map-large.txt")));
    }
}
