package co.uk.landbay.mortgageapplication.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestorInterestDto {

  private String investorName;

  private BigDecimal interestEarned;

  private LocalDate periodStartDate;

  private LocalDate periodEndDate;

  public InvestorInterestDto(String investorName, BigDecimal interestEarned, LocalDate periodStartDate,
                             LocalDate periodEndDate) {
    this.investorName = investorName;
    this.interestEarned = interestEarned;
    this.periodStartDate = periodStartDate;
    this.periodEndDate = periodEndDate;
  }

  public String getInvestorName() {
    return investorName;
  }

  public void setInvestorName(String investorName) {
    this.investorName = investorName;
  }

  public BigDecimal getInterestEarned() {
    return interestEarned;
  }

  public void setInterestEarned(BigDecimal interestEarned) {
    this.interestEarned = interestEarned;
  }

  public LocalDate getPeriodStartDate() {
    return periodStartDate;
  }

  public void setPeriodStartDate(LocalDate periodStartDate) {
    this.periodStartDate = periodStartDate;
  }

  public LocalDate getPeriodEndDate() {
    return periodEndDate;
  }

  public void setPeriodEndDate(LocalDate periodEndDate) {
    this.periodEndDate = periodEndDate;
  }

}
