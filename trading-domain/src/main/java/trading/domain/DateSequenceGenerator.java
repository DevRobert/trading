package trading.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateSequenceGenerator {
    private LocalDate nextDate;

    public DateSequenceGenerator(LocalDate initialDate) {
        this.nextDate = initialDate;
    }

    public LocalDate nextDate() {
        LocalDate result = this.nextDate;
        this.nextDate = this.nextDate.plusDays(1);
        return result;
    }

    public List<LocalDate> nextDates(int dayCount) {
        if(dayCount == 0) {
            throw new RuntimeException("The day count must not be zero.");
        }

        if(dayCount < 0) {
            throw new RuntimeException("The day count must not be negative.");
        }

        List<LocalDate> result = new ArrayList<>();

        for(int dayIndex = 0; dayIndex < dayCount; dayIndex++) {
            result.add(this.nextDate());
        }

        return result;
    }
}
