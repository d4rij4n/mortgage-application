package co.uk.landbay.mortgageapplication.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mortgage")
public class Mortgage implements Serializable {

  private long mortgageId;

  private BigDecimal interestRate;

  private InterestRateType interestRateType;

  private BigDecimal mortgageValue;

  private LocalDate startDate;

  private LocalDate endDate;

  private List<MortgagePart> mortgageParts;

  @Id
  @Column(name = "mortgage_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long getMortgageId() {
    return mortgageId;
  }

  public void setMortgageId(long mortgageId) {
    this.mortgageId = mortgageId;
  }

  @NotNull
  @Column(name = "interest_rate")
  public BigDecimal getInterestRate() {
    return interestRate;
  }

  public void setInterestRate(BigDecimal interestRate) {
    this.interestRate = interestRate;
  }

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "interest_rate_type")
  public InterestRateType getInterestRateType() {
    return interestRateType;
  }

  public void setInterestRateType(InterestRateType interestRateType) {
    this.interestRateType = interestRateType;
  }

  @NotNull
  @Column(name = "mortgage_value")
  public BigDecimal getMortgageValue() {
    return mortgageValue;
  }

  public void setMortgageValue(BigDecimal mortgageValue) {
    this.mortgageValue = mortgageValue;
  }

  @NotNull
  @Column(name = "start_date")
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  @Column(name = "end_date")
  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  @JsonManagedReference
  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mortgage")
  public List<MortgagePart> getMortgageParts() {
    return mortgageParts;
  }

  public void setMortgageParts(List<MortgagePart> mortgageParts) {
    this.mortgageParts = mortgageParts;
  }

  @Transient
  public void addMortgagePart(MortgagePart mortgagePart) {
    if (mortgageParts == null) {
      mortgageParts = new ArrayList<>();
    }

    mortgageParts.add(mortgagePart);
  }

}
