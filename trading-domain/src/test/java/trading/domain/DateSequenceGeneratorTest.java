package trading.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class DateSequenceGeneratorTest {
    private DateSequenceGenerator dateSequenceGenerator;

    @Before
    public void before() {
        this.dateSequenceGenerator = new DateSequenceGenerator(LocalDate.of(2018, 5, 1));
    }

    @Test
    public void firstGeneratedDateEqualsInitialDate() {
        Assert.assertEquals(LocalDate.of(2018,5, 1), this.dateSequenceGenerator.nextDate());
    }

    @Test
    public void secondGeneratedDateEqualsInitialDatePlusOneDay() {
        this.dateSequenceGenerator.nextDate();
        Assert.assertEquals(LocalDate.of(2018,5, 2), this.dateSequenceGenerator.nextDate());
    }

    @Test
    public void thirdGeneratedDateEqualsInitialDatePlusTwoDays() {
        this.dateSequenceGenerator.nextDate();
        this.dateSequenceGenerator.nextDate();
        Assert.assertEquals(LocalDate.of(2018,5, 3), this.dateSequenceGenerator.nextDate());
    }

    @Test
    public void generatesSequenceOfOneDay() {
        List<LocalDate> dates = this.dateSequenceGenerator.nextDates(1);
        Assert.assertEquals(1, dates.size());
        Assert.assertEquals(LocalDate.of(2018, 5, 1), dates.get(0));
    }

    @Test
    public void generatesSequenceOfTwoDays() {
        List<LocalDate> dates = this.dateSequenceGenerator.nextDates(2);
        Assert.assertEquals(2, dates.size());
        Assert.assertEquals(LocalDate.of(2018, 5, 1), dates.get(0));
        Assert.assertEquals(LocalDate.of(2018, 5, 2), dates.get(1));
    }

    @Test
    public void sequenceGenerationFails_ifDayCountZero() {
        try {
            this.dateSequenceGenerator.nextDates(0);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The day count must not be zero.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
    @Test
    public void sequenceGenerationFails_ifDayCountNegative() {
        try {
            this.dateSequenceGenerator.nextDates(-1);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The day count must not be negative.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
