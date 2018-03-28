package co.uk.landbay.mortgageapplication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "mortgage_part")
public class MortgagePart implements Serializable {

  private long mortgagePartId;

  private String investorName;

  private BigDecimal investmentValue;

  private Mortgage mortgage;

  @Id
  @Column(name = "mortgage_part_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long getMortgagePartId() {
    return mortgagePartId;
  }

  public void setMortgagePartId(long mortgagePartId) {
    this.mortgagePartId = mortgagePartId;
  }

  @NotBlank
  @Column(name = "investor_name")
  public String getInvestorName() {
    return investorName;
  }

  public void setInvestorName(String investorName) {
    this.investorName = investorName;
  }

  @NotNull
  @Column(name = "investment_value")
  public BigDecimal getInvestmentValue() {
    return investmentValue;
  }

  public void setInvestmentValue(BigDecimal investmentValue) {
    this.investmentValue = investmentValue;
  }

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "mortgage_id")
  public Mortgage getMortgage() {
    return mortgage;
  }

  public void setMortgage(Mortgage mortgage) {
    this.mortgage = mortgage;
  }

}
