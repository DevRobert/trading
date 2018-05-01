package trading.domain.strategy.compound;

import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

public class Scores {
    private final Map<ISIN, Score> values;
    private final LocalDate date;

    public Scores(Map<ISIN, Score> values, LocalDate date) {
        if(date == null) {
            throw new RuntimeException("The date must be specified.");
        }

        this.values = values;
        this.date = date;
    }

    public Score get(ISIN isin) {
        return this.values.get(isin);
    }

    public ISIN[] getIsinsOrderByScoreDescending() {
        ISIN[] isins = this.values.keySet().toArray(new ISIN[0]);

        Arrays.sort(isins, (isin1, isin2) -> {
            double score1 = values.get(isin1).getValue();
            double score2 = values.get(isin2).getValue();

            if(score1 == score2) {
                return isin1.getText().compareTo(isin2.getText()); // ascending
            }

            return Double.compare(score2, score1); // descending
        });

        return isins;
    }

    public LocalDate getDate() {
        return this.date;
    }
}
