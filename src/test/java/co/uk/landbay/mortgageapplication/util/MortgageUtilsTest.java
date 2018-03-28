package co.uk.landbay.mortgageapplication.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MortgageUtilsTest {

  private static final BigDecimal INVESTMENT_VALUE = new BigDecimal(10000);

  private static final BigDecimal INTEREST_RATE = new BigDecimal(5);

  private static final LocalDate START_DATE = LocalDate.of(2018, 3, 27);

  @Test
  public void calculateInterestEarned() {
    BigDecimal result = MortgageUtils.calculateInterestEarned(INVESTMENT_VALUE,
                                                              INTEREST_RATE,
                                                              START_DATE,
                                                              START_DATE.plusDays(5));
    assertThat(result, is(getBigDecimal(6.85)));
  }

  @Test
  public void calculateInterestEarned_zeroDays() {
    BigDecimal result = MortgageUtils.calculateInterestEarned(INVESTMENT_VALUE, INTEREST_RATE, START_DATE, START_DATE);
    assertThat(result, is(getBigDecimal(0)));
  }

  @Test
  public void calculateInterestEarned_negativeDays() {
    BigDecimal result = MortgageUtils.calculateInterestEarned(INVESTMENT_VALUE,
                                                              INTEREST_RATE,
                                                              START_DATE,
                                                              START_DATE.minusDays(1));
    assertThat(result, is(getBigDecimal(0)));
  }

  private BigDecimal getBigDecimal(double value) {
    return BigDecimal.valueOf(value).setScale(MortgageUtils.BIG_DECIMAL_SCALE_RESULT, RoundingMode.HALF_UP);
  }

}
