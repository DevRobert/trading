package trading.simulation;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ParameterTupleSourceTest {
    @Test
    public void oneParameter() {
        List<List<Object>> parameterLists = new ArrayList<>();
        parameterLists.add(Arrays.asList(new Object[] { 1, 2, 3 }));

        ParameterTupleSource parameterTupleSource = new ParameterTupleSource(parameterLists);

        Iterator<Object[]> iterator = parameterTupleSource;

        List<Object[]> expected = Arrays.asList(
                new Object[] { 1 },
                new Object[] { 2 },
                new Object[] { 3 }
        );

        List<Object[]> actual = iteratorToList(iterator);

        assertEquals(expected, actual);

        Assert.assertEquals(3, parameterTupleSource.size());
    }

    @Test
    public void twoParameters() {
        List<List<Object>> parameterLists = new ArrayList<>();
        parameterLists.add(Arrays.asList(new Object[] { 1, 2, 3 }));
        parameterLists.add(Arrays.asList(new Object[] { "a", "b" }));

        ParameterTupleSource parameterTupleSource = new ParameterTupleSource(parameterLists);

        Iterator<Object[]> iterator = parameterTupleSource;

        List<Object[]> expected = Arrays.asList(
                new Object[] { 1, "a"},
                new Object[] { 1, "b"},
                new Object[] { 2, "a"},
                new Object[] { 2, "b"},
                new Object[] { 3, "a"},
                new Object[] { 3, "b"}
        );

        List<Object[]> actual = iteratorToList(iterator);

        assertEquals(expected, actual);

        Assert.assertEquals(6, parameterTupleSource.size());
    }

    @Test
    public void threeParameters() {
        List<List<Object>> parameterLists = new ArrayList<>();
        parameterLists.add(Arrays.asList(new Object[] { 1, 2, 3 }));
        parameterLists.add(Arrays.asList(new Object[] { "a", "b" }));
        parameterLists.add(Arrays.asList(new Object[] { 0.1, 0.2 }));

        ParameterTupleSource parameterTupleSource = new ParameterTupleSource(parameterLists);

        Iterator<Object[]> iterator = parameterTupleSource;

        List<Object[]> expected = Arrays.asList(
                new Object[] { 1, "a", 0.1},
                new Object[] { 1, "a", 0.2},
                new Object[] { 1, "b", 0.1},
                new Object[] { 1, "b", 0.2},
                new Object[] { 2, "a", 0.1},
                new Object[] { 2, "a", 0.2},
                new Object[] { 2, "b", 0.1},
                new Object[] { 2, "b", 0.2},
                new Object[] { 3, "a", 0.1},
                new Object[] { 3, "a", 0.2},
                new Object[] { 3, "b", 0.1},
                new Object[] { 3, "b", 0.2}
        );

        List<Object[]> actual = iteratorToList(iterator);

        assertEquals(expected, actual);

        Assert.assertEquals(12, parameterTupleSource.size());
    }

    @Test
    public void failsIfNoNextTupleAvailable() {
        List<List<Object>> parameterLists = new ArrayList<>();
        parameterLists.add(Arrays.asList(new Object[] { 1 }));

        ParameterTupleSource parameterTupleSource = new ParameterTupleSource(parameterLists);

        Iterator<Object[]> iterator = parameterTupleSource;

        iterator.next();

        Assert.assertFalse(iterator.hasNext());

        try {
            iterator.next();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("No next parameter tuple available.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    private static List<Object[]> iteratorToList(Iterator<Object[]> iterator) {
        ArrayList<Object[]> result = new ArrayList<>();

        while(iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    private static void assertEquals(List<Object[]> expected, List<Object[]> actual) {
        Assert.assertEquals(expected.size(), actual.size());

        for(int parameterTupleIndex = 0; parameterTupleIndex < expected.size(); parameterTupleIndex++) {
            Object[] expectedParameterTuple = expected.get(parameterTupleIndex);
            Object[] actualParameterTuple = actual.get(parameterTupleIndex);

            Assert.assertArrayEquals("Difference in parameter tuple index " + parameterTupleIndex, expectedParameterTuple, actualParameterTuple);
        }
    }
}
