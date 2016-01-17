package redmart.spreadsheet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpreadsheetCalculatorTest {
    @Test
    public void testBuildAdjList() {
        List<SpreadsheetCalculator.Cell> cells = new ArrayList<>();
        cells.add(new SpreadsheetCalculator.Cell("A1", "A2"));
        cells.add(new SpreadsheetCalculator.Cell("A2", "4 5 *"));
        cells.add(new SpreadsheetCalculator.Cell("A3", "A1"));
        cells.add(new SpreadsheetCalculator.Cell("B1", "A1 B2 / 2 +"));
        cells.add(new SpreadsheetCalculator.Cell("B2", "3"));
        cells.add(new SpreadsheetCalculator.Cell("B3", "39 B1 B2 * /"));

        SpreadsheetCalculator spreadsheet = new SpreadsheetCalculator(cells);
        Map<String, List<String>> adjList = spreadsheet.getAdjList();
        assertEquals("[A3, B1]", adjList.get("A1").toString());
        assertEquals("[A1]", adjList.get("A2").toString());
        assertEquals("[B3]", adjList.get("B1").toString());
        assertEquals("[B1, B3]", adjList.get("B2").toString());
    }

    @Test
    public void testDetectCycle() {
        List<SpreadsheetCalculator.Cell> cells = new ArrayList<>();
        cells.add(new SpreadsheetCalculator.Cell("A1", "A3"));
        cells.add(new SpreadsheetCalculator.Cell("A2", "4 5 *"));
        cells.add(new SpreadsheetCalculator.Cell("A3", "A1"));

        SpreadsheetCalculator spreadsheet = new SpreadsheetCalculator(cells);
        assertTrue(spreadsheet.hasCycle());
    }

    @Test
    public void testGetPath() {
        List<SpreadsheetCalculator.Cell> cells = new ArrayList<>();
        cells.add(new SpreadsheetCalculator.Cell("A1", "A2"));
        cells.add(new SpreadsheetCalculator.Cell("A2", "4 5 *"));
        cells.add(new SpreadsheetCalculator.Cell("A3", "A1"));
        cells.add(new SpreadsheetCalculator.Cell("B1", "A1 B2 / 2 +"));
        cells.add(new SpreadsheetCalculator.Cell("B2", "3"));
        cells.add(new SpreadsheetCalculator.Cell("B3", "39 B1 B2 * /"));

        SpreadsheetCalculator spreadsheet = new SpreadsheetCalculator(cells);
        List<String> path = spreadsheet.getPath();
        assertEquals("[B2, A2, A1, B1, B3, A3]", path.toString());
    }

    @Test
    public void testEvaluate() {
        Map<String, Double> vars = new HashMap<>();
        vars.put("A1", 4.0);
        vars.put("A2", 2.0);

        String exp = "3 2 4 + *";
        assertEquals(18.0, SpreadsheetCalculator.evaluate(exp, vars), 0.0);

        exp = "3 3 * 3 /";
        assertEquals(3.0, SpreadsheetCalculator.evaluate(exp, vars), 0.0);

        exp = "1";
        assertEquals(1.0, SpreadsheetCalculator.evaluate(exp, vars), 0.0);

        exp = "A1 3 + A2 -";
        assertEquals(5.0, SpreadsheetCalculator.evaluate(exp, vars), 0.0);

        exp = "3 2 1 + *";
        assertEquals(9.0, SpreadsheetCalculator.evaluate(exp, vars), 0.0);
    }

    @Test
    public void testCalculate() {
        List<SpreadsheetCalculator.Cell> cells = new ArrayList<>();
        cells.add(new SpreadsheetCalculator.Cell("A1", "A2"));
        cells.add(new SpreadsheetCalculator.Cell("A2", "4 5 *"));
        cells.add(new SpreadsheetCalculator.Cell("A3", "A1"));
        cells.add(new SpreadsheetCalculator.Cell("B1", "A1 B2 / 2 +"));
        cells.add(new SpreadsheetCalculator.Cell("B2", "3"));
        cells.add(new SpreadsheetCalculator.Cell("B3", "39 B1 B2 * /"));

        SpreadsheetCalculator spreadsheet = new SpreadsheetCalculator(cells);
        List<String> result = spreadsheet.calculate();
        assertEquals("[20.00000, 20.00000, 20.00000, 8.66667, 3.00000, 1.50000]", result.toString());
    }
}