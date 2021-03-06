package trading.domain.strategy.compound;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ScoresTest {
    @Test
    public void returnsIsinsSortedByScoreAndText() {
        // Sort descending by score and ascending by text
        // Alphabetical sorting required to get reproducible results

        Map<ISIN, Score> values = new HashMap<>();
        values.put(new ISIN("A"), new Score(0.2));
        values.put(new ISIN("E"), new Score(1.0));
        values.put(new ISIN("C"), new Score(0.5));
        values.put(new ISIN("B"), new Score(0.5));
        values.put(new ISIN("D"), new Score(0.5));


        Scores scores = new Scores(values, LocalDate.now());
        ISIN[] sortedScores = scores.getIsinsOrderByScoreDescending();

        ISIN[] expectedScores = new ISIN[] {
                new ISIN("E"), // 1.0
                new ISIN("B"), // 0.5
                new ISIN("C"), // 0.5
                new ISIN("D"), // 0.5
                new ISIN("A"), // 0.2
        };

        Assert.assertArrayEquals(expectedScores, sortedScores);
    }

    @Test
    public void returnsDate() {
        Map<ISIN, Score> values = new HashMap<>();
        LocalDate date = LocalDate.of(2018, 4, 3);

        Scores scores = new Scores(values, date);

        Assert.assertSame(date, scores.getDate());
    }

    @Test
    public void initializationFails_ifDateNotSpecified() {
        Map<ISIN, Score> values = new HashMap<>();
        LocalDate date = null;

        try {
            new Scores(values, date);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The date must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
