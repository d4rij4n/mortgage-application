package co.uk.landbay.mortgageapplication.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class MortgageUtils {

  static final int BIG_DECIMAL_SCALE_RESULT = 2;

  /* Current assumption is that scale of 10 is precise enough for demo.
     Real world application probably applies a higher scale. */
  private static final int BIG_DECIMAL_SCALE_DIVISION = 10;

  /* Number of days per year is set to 365 for simplicity - leap years are ignored.
     For Julian Calendar the value should be 365.25 */
  private static final int NUMBER_OF_DAYS_IN_YEAR = 365;

  public static BigDecimal calculateInterestEarned(BigDecimal investmentValue, BigDecimal annualInterestRate, LocalDate startDate,
                                                   LocalDate endDate) {
    long daysBetween = DAYS.between(startDate, endDate);

    if (endDate.isBefore(startDate)) {
      return new BigDecimal(0).setScale(BIG_DECIMAL_SCALE_RESULT, RoundingMode.HALF_UP);
    }

    BigDecimal convertedAnnualInterestRate = annualInterestRate.divide(new BigDecimal(100),
                                                                       BIG_DECIMAL_SCALE_DIVISION,
                                                                       RoundingMode.HALF_UP);
    BigDecimal dailyInterestRate = convertedAnnualInterestRate.divide(new BigDecimal(NUMBER_OF_DAYS_IN_YEAR),
                                                                      BIG_DECIMAL_SCALE_DIVISION,
                                                                      RoundingMode.HALF_UP);

    return investmentValue.multiply(dailyInterestRate.multiply(new BigDecimal(daysBetween)))
                          .setScale(BIG_DECIMAL_SCALE_RESULT, RoundingMode.HALF_UP);
  }

}
