package redmart.spreadsheet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SpreadsheetCalculator {
    public static class Cell {
        private final String name;
        private final String value;

        public Cell(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private final Map<String, List<String>> adjList = new HashMap<>();
    private final List<String> cellNames = new ArrayList<>();
    private final Map<String, String> expressions = new HashMap<>();

    public SpreadsheetCalculator(List<Cell> cells) {
        build(cells);
    }

    private void build(List<Cell> cells) {
        // build an adjacency list
        for (Cell cell : cells) {
            cellNames.add(cell.getName());
            expressions.put(cell.getName(), cell.getValue());
            String[] exp = cell.value.split("\\s+");
            for (String e : exp) {
                if (e.equals("+") || e.equals("+") || e.equals("*")
                    || e.equals("/") || isNumber(e)) {
                    continue;
                }
                if (!adjList.containsKey(e)) {
                    List<String> list = new ArrayList<>();
                    list.add(cell.getName());
                    adjList.put(e, list);
                } else {
                    adjList.get(e).add(cell.getName());
                }
            }
        }
    }

    private static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    Map<String, List<String>> getAdjList() {
        return Collections.unmodifiableMap(adjList);
    }

    boolean hasCycle() {
        return new CycleDetector(cellNames, adjList).hasCycle();
    }

    List<String> getPath() {
        return new TopologicalSort(cellNames, adjList).getPath();
    }

    public List<String> calculate() {
        if (hasCycle()) {
            throw new RuntimeException("A cycle is detected");
        }
        List<String> result = new ArrayList<>();
        List<String> cells = getPath();
        Map<String, Double> vars = new HashMap<>();
        for (String cell : cells) {
            double value = evaluate(expressions.get(cell), vars);
            vars.put(cell, value);
        }
        for (String cellName : cellNames) {
            result.add(String.format("%.5f", vars.get(cellName)));
        }
        return result;
    }

    static double evaluate(String expression, Map<String, Double> vars) {
        Stack<Double> stack = new Stack<>();
        String[] exp = expression.split("\\s+");
        for (int i = 0; i < exp.length; i++) {
            String e = exp[i];
            if (e.equals("+")) {
                double a = stack.pop();
                double b = stack.pop();
                stack.push(b + a);
            } else if (e.equals("*")) {
                double a = stack.pop();
                double b = stack.pop();
                stack.push(b * a);
            } else if (e.equals("-")) {
                double a = stack.pop();
                double b = stack.pop();
                stack.push(b - a);
            } else if (e.equals("/")) {
                double a = stack.pop();
                double b = stack.pop();
                stack.push(b / a);
            } else {
                if (isNumber(e)) {
                    stack.push(Double.parseDouble(e));
                } else {
                    stack.push(vars.get(e));
                }
            }
        }
        double result = stack.pop();
        return result;
    }

    private static class CycleDetector {
        private boolean cycle = false;
        private final Set<String> marked = new HashSet<>();
        private final Map<String, Boolean> onStack = new HashMap<>();

        public CycleDetector(List<String> vertices, Map<String, List<String>> adjList) {
            for (String vertex : vertices) {
                onStack.put(vertex, false);
            }
            for (String vertex : vertices) {
                if (!marked.contains(vertex)) {
                    detectCycle(adjList, vertex);
                }
            }
        }

        private void detectCycle(Map<String, List<String>> adjList, String vertex) {
            marked.add(vertex);
            onStack.put(vertex, true);
            if (adjList.containsKey(vertex)) {
                for (String adj : adjList.get(vertex)) {
                    if (!marked.contains(adj)) {
                        detectCycle(adjList, adj);
                    } else {
                        if (onStack.get(adj)) {
                            cycle = true;
                        }
                    }
                }
            }
            onStack.put(vertex, false);
        }

        public boolean hasCycle() {
            return cycle;
        }
    }

    private static class TopologicalSort {
        private final Set<String> marked = new HashSet<>();
        private final Stack<String> path = new Stack<>();

        public TopologicalSort(List<String> vertices, Map<String, List<String>> adjList) {
            for (String vertex : vertices) {
                if (!marked.contains(vertex)) {
                    topologicalSort(adjList, vertex);
                }
            }
        }

        private void topologicalSort(Map<String, List<String>> adjList, String vertex) {
            marked.add(vertex);
            if (adjList.containsKey(vertex)) {
                for (String adj : adjList.get(vertex)) {
                    if (!marked.contains(adj)) {
                        topologicalSort(adjList, adj);
                    }
                }
            }
            path.add(vertex);
        }

        public List<String> getPath() {
            List<String> p = new ArrayList<>();
            while (!path.isEmpty()) {
                p.add(path.pop());
            }
            return p;
        }
    }

    public static void main(String[] args) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String sizeStr = reader.readLine();
                String[] size = sizeStr.split("\\s+");
                int colSize = Integer.parseInt(size[0]);
                int rowSize = Integer.parseInt(size[1]);
                char row = 'A';
                int col = 1;
                List<Cell> cells = new ArrayList<>();
                for (int i = 1; i <= rowSize * colSize; i++) {
                    String cellName = "" + row + col;
                    String exp = reader.readLine();
                    Cell cell = new Cell(cellName, exp);
                    cells.add(cell);
                    if (i %  colSize == 0) {
                        col = 1;
                        row++;
                    } else {
                        col++;
                    }
                }
                SpreadsheetCalculator calculator = new SpreadsheetCalculator(cells);
                calculator.calculate().forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
