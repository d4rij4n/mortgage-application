package co.uk.landbay.mortgageapplication;

import co.uk.landbay.mortgageapplication.model.InterestRateType;
import co.uk.landbay.mortgageapplication.model.Mortgage;
import co.uk.landbay.mortgageapplication.model.MortgagePart;
import co.uk.landbay.mortgageapplication.repository.MortgagePartRepository;
import co.uk.landbay.mortgageapplication.repository.MortgageRepository;
import co.uk.landbay.mortgageapplication.util.MortgageUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MortgageApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MortgageApplicationIT {

  private static final String INTEREST_RATE = "2.5";

  private static final String MORTGAGE_VALUE = "10000.00";

  private static final String INVESTOR_NAME = "Peter";

  private static final String INVESTOR_NAME_TWO = "Jane";

  private static final String INVESTMENT_VALUE = "2000.00";

  private static final String INVESTMENT_VALUE_TWO = "4000.00";

  private static Long MORTGAGE_ID = 1L;

  private static Long MORTGAGE_PART_ID = 1L;

  private static LocalDate START_DATE = LocalDate.of(2017, 1, 1);

  private static LocalDate END_DATE = LocalDate.of(2027, 1, 1);

  @Autowired
  private MockMvc mvc;

  @Autowired
  private MortgageRepository mortgageRepository;

  @Autowired
  private MortgagePartRepository mortgagePartRepository;

  @Test
  public void getMortgageRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    mortgage.addMortgagePart(mortgagePart);

    mortgageRepository.save(mortgage);
    mortgagePartRepository.save(mortgagePart);

    mvc.perform(get("/api/mortgage/" + MORTGAGE_ID).contentType(MediaType.APPLICATION_JSON))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.mortgageId", is(MORTGAGE_ID.intValue())))
       .andExpect(jsonPath("$.interestRate", is(new Double(INTEREST_RATE))))
       .andExpect(jsonPath("$.interestRateType", is(InterestRateType.FIXED_RATE.name())))
       .andExpect(jsonPath("$.mortgageValue", is(new Double(MORTGAGE_VALUE))))
       .andExpect(jsonPath("$.startDate", is(START_DATE.toString())))
       .andExpect(jsonPath("$.endDate", is(END_DATE.toString())))
       .andExpect(jsonPath("$.mortgageParts", hasSize(1)))
       .andExpect(jsonPath("$.mortgageParts[0].mortgagePartId", is(MORTGAGE_PART_ID.intValue())))
       .andExpect(jsonPath("$.mortgageParts[0].investorName", is(INVESTOR_NAME)))
       .andExpect(jsonPath("$.mortgageParts[0].investmentValue", is(new Double(INVESTMENT_VALUE))));
  }

  @Test
  public void createMortgageRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    mvc.perform((post("/api/mortgage/").content(TestUtils.createJson(mortgage)).contentType(MediaType.APPLICATION_JSON)))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.mortgageId", is(MORTGAGE_ID.intValue())))
       .andExpect(jsonPath("$.interestRate", is(new Double(INTEREST_RATE))))
       .andExpect(jsonPath("$.interestRateType", is(InterestRateType.FIXED_RATE.name())))
       .andExpect(jsonPath("$.mortgageValue", is(new Double(MORTGAGE_VALUE))))
       .andExpect(jsonPath("$.startDate", is(START_DATE.toString())))
       .andExpect(jsonPath("$.endDate", is(END_DATE.toString())));

    Optional<Mortgage> createdMortgageOpt = mortgageRepository.findById(MORTGAGE_ID);
    assertThat(mortgageRepository.findById(MORTGAGE_ID).isPresent(), is(true));

    Mortgage createdMortgage = createdMortgageOpt.get();
    assertThat(createdMortgage.getMortgageId(), is(MORTGAGE_ID));
    assertThat(createdMortgage.getInterestRate(), is(equalTo(getBigDecimal(INTEREST_RATE))));
    assertThat(createdMortgage.getInterestRateType(), is(equalTo(InterestRateType.FIXED_RATE)));
    assertThat(createdMortgage.getMortgageValue(), is(equalTo(getBigDecimal(MORTGAGE_VALUE))));
    assertThat(createdMortgage.getStartDate(), is(equalTo(START_DATE)));
    assertThat(createdMortgage.getEndDate(), is(equalTo(END_DATE)));
  }

  @Test
  public void createInvestmentRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    mortgageRepository.save(mortgage);

    mvc.perform(post("/api/mortgage/" + MORTGAGE_ID.intValue() + "/investment").content(TestUtils.createJson(mortgagePart))
                                                                               .contentType(MediaType.APPLICATION_JSON))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.mortgagePartId", is(MORTGAGE_PART_ID.intValue())))
       .andExpect(jsonPath("$.investorName", is(INVESTOR_NAME)))
       .andExpect(jsonPath("$.investmentValue", is(new Double(INVESTMENT_VALUE))));

    Optional<Mortgage> retrievedMortgageOpt = mortgageRepository.findById(MORTGAGE_ID);
    assertThat(retrievedMortgageOpt.isPresent(), is(true));

    Mortgage retrievedMortgage = retrievedMortgageOpt.get();
    assertThat(retrievedMortgage.getMortgageParts(), hasSize(1));
    MortgagePart createdMortgagePart = retrievedMortgage.getMortgageParts().get(0);

    assertThat(createdMortgagePart.getMortgagePartId(), is(MORTGAGE_PART_ID));
    assertThat(createdMortgagePart.getInvestorName(), is(equalTo(INVESTOR_NAME)));
    assertThat(createdMortgagePart.getInvestmentValue(), is(equalTo(getBigDecimal(INVESTMENT_VALUE))));
  }

  @Test
  public void deleteMortgageRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    mortgage.addMortgagePart(mortgagePart);

    mortgageRepository.save(mortgage);
    mortgagePartRepository.save(mortgagePart);

    mvc.perform(delete("/api/mortgage/" + MORTGAGE_ID.intValue()).contentType(MediaType.APPLICATION_JSON))
       .andExpect(status().isOk());

    assertThat(mortgageRepository.findById(MORTGAGE_ID).isPresent(), is(false));
  }

  @Test
  public void calculateInterestRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePartOne = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    MortgagePart mortgagePartTwo = getMortgagePart(mortgage, INVESTOR_NAME_TWO, INVESTMENT_VALUE_TWO);
    mortgage.addMortgagePart(mortgagePartOne);
    mortgage.addMortgagePart(mortgagePartTwo);

    mortgageRepository.save(mortgage);
    mortgagePartRepository.save(mortgagePartOne);
    mortgagePartRepository.save(mortgagePartTwo);

    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusDays(5);

    mvc.perform(get("/api/mortgages/investor/interests").param("startDate", startDate.toString())
                                                        .param("endDate", endDate.toString())
                                                        .contentType(MediaType.APPLICATION_JSON))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$[0].investorName", is(INVESTOR_NAME)))
       .andExpect(jsonPath("$[0].interestEarned", is(calculateInterest(INVESTMENT_VALUE, startDate, endDate))))
       .andExpect(jsonPath("$[0].periodStartDate", is(startDate.toString())))
       .andExpect(jsonPath("$[0].periodEndDate", is(endDate.toString())))
       .andExpect(jsonPath("$[1].investorName", is(INVESTOR_NAME_TWO)))
       .andExpect(jsonPath("$[1].interestEarned", is(calculateInterest(INVESTMENT_VALUE_TWO, startDate, endDate))))
       .andExpect(jsonPath("$[1].periodStartDate", is(startDate.toString())))
       .andExpect(jsonPath("$[1].periodEndDate", is(endDate.toString())));
  }

  private Double calculateInterest(String investmentValue, LocalDate startDate, LocalDate endDate) {
    return MortgageUtils.calculateInterestEarned(new BigDecimal(investmentValue),
                                                 new BigDecimal(INTEREST_RATE),
                                                 startDate,
                                                 endDate).doubleValue();
  }

  private Mortgage getMortgage(Long mortgageId, String interestRate) {
    Mortgage mortgage = new Mortgage();
    mortgage.setMortgageId(mortgageId);
    mortgage.setInterestRate(new BigDecimal(interestRate));
    mortgage.setInterestRateType(InterestRateType.FIXED_RATE);
    mortgage.setMortgageValue(new BigDecimal(MORTGAGE_VALUE));
    mortgage.setStartDate(START_DATE);
    mortgage.setEndDate(END_DATE);
    return mortgage;
  }

  private MortgagePart getMortgagePart(Mortgage mortgage, String investorName, String investmentValue) {
    MortgagePart mortgagePart = new MortgagePart();
    mortgagePart.setMortgage(mortgage);
    mortgagePart.setInvestmentValue(new BigDecimal(investmentValue));
    mortgagePart.setInvestorName(investorName);
    return mortgagePart;
  }

  private BigDecimal getBigDecimal(String value) {
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
  }

}
