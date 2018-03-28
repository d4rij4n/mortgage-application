package co.uk.landbay.mortgageapplication.controller;

import co.uk.landbay.mortgageapplication.TestUtils;
import co.uk.landbay.mortgageapplication.model.InterestRateType;
import co.uk.landbay.mortgageapplication.model.Mortgage;
import co.uk.landbay.mortgageapplication.model.MortgagePart;
import co.uk.landbay.mortgageapplication.repository.MortgagePartRepository;
import co.uk.landbay.mortgageapplication.repository.MortgageRepository;
import co.uk.landbay.mortgageapplication.util.MortgageUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MortgageController.class)
public class MortgageControllerTest {

  private static final String INVESTOR_NAME = "Peter";

  private static final String INVESTOR_NAME_TWO = "Jane";

  private static final String INTEREST_RATE = "2.5";

  private static final String MORTGAGE_VALUE = "10000.00";

  private static final String INVESTMENT_VALUE = "2000.00";

  private static final String INVESTMENT_VALUE_TWO = "4000.00";

  private static Long MORTGAGE_ID = 1L;

  private static Long INVALID_MORTGAGE_ID = 10L;

  private static Long MORTGAGE_PART_ID = 1L;

  private static LocalDate START_DATE = LocalDate.of(2017, 1, 1);

  private static LocalDate END_DATE = LocalDate.of(2027, 1, 1);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MortgageRepository mortgageRepository;

  @MockBean
  private MortgagePartRepository mortgagePartRepository;

  @InjectMocks
  private MortgageController mortgageController;

  @Test
  public void getMortgageRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    mortgage.addMortgagePart(mortgagePart);

    when(mortgageRepository.findById(MORTGAGE_ID)).thenReturn(Optional.of(mortgage));

    mockMvc.perform(get("/api/mortgage/" + MORTGAGE_ID).contentType(MediaType.APPLICATION_JSON))
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

    verify(mortgageRepository).findById(MORTGAGE_ID);
  }

  @Test
  public void getMortgageRequest_withMissingMortgageId() throws Exception {
    when(mortgageRepository.findById(INVALID_MORTGAGE_ID)).thenReturn(Optional.empty());
    mockMvc.perform(get("/api/mortgage/" + INVALID_MORTGAGE_ID).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
  }

  @Test
  public void createMortgageRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    when(mortgageRepository.save(any(Mortgage.class))).thenReturn(mortgage);

    mockMvc.perform((post("/api/mortgage/").content(TestUtils.createJson(mortgage)).contentType(MediaType.APPLICATION_JSON)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.mortgageId", is(MORTGAGE_ID.intValue())))
           .andExpect(jsonPath("$.interestRate", is(new Double(INTEREST_RATE))))
           .andExpect(jsonPath("$.interestRateType", is(InterestRateType.FIXED_RATE.name())))
           .andExpect(jsonPath("$.mortgageValue", is(new Double(MORTGAGE_VALUE))))
           .andExpect(jsonPath("$.startDate", is(START_DATE.toString())))
           .andExpect(jsonPath("$.endDate", is(END_DATE.toString())));

    verify(mortgageRepository).save(any(Mortgage.class));
  }

  @Test
  public void createInvestmentRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    when(mortgageRepository.findById(MORTGAGE_ID)).thenReturn(Optional.of(mortgage));
    when(mortgagePartRepository.save(any(MortgagePart.class))).thenReturn(mortgagePart);

    mockMvc.perform(post("/api/mortgage/" + MORTGAGE_ID.intValue() + "/investment").content(TestUtils.createJson(mortgagePart))
                                                                                   .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.mortgagePartId", is(MORTGAGE_PART_ID.intValue())))
           .andExpect(jsonPath("$.investorName", is(INVESTOR_NAME)))
           .andExpect(jsonPath("$.investmentValue", is(new Double(INVESTMENT_VALUE))));

    verify(mortgageRepository).findById(MORTGAGE_ID);
    verify(mortgagePartRepository).save(any(MortgagePart.class));
  }

  @Test
  public void createInvestmentRequest_withMissingMortgageId() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    when(mortgageRepository.findById(INVALID_MORTGAGE_ID)).thenReturn(Optional.empty());
    when(mortgagePartRepository.save(any(MortgagePart.class))).thenReturn(mortgagePart);

    mockMvc.perform(post("/api/mortgage/" + INVALID_MORTGAGE_ID.intValue() + "/investment").content(TestUtils.createJson(
            mortgagePart)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

    verifyZeroInteractions(mortgagePartRepository);
  }

  @Test
  public void deleteMortgageRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePart = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    mortgage.addMortgagePart(mortgagePart);
    when(mortgageRepository.findById(MORTGAGE_ID)).thenReturn(Optional.of(mortgage));

    mockMvc.perform(delete("/api/mortgage/" + MORTGAGE_ID.intValue()).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());

    verify(mortgageRepository).delete(mortgage);
  }

  @Test
  public void deleteMortgageRequest_withMissingMortgageId() throws Exception {
    when(mortgageRepository.findById(INVALID_MORTGAGE_ID)).thenReturn(Optional.empty());
    mockMvc.perform(delete("/api/mortgage/" + INVALID_MORTGAGE_ID.intValue()).contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    verify(mortgageRepository, times(0)).delete(any());
  }

  @Test
  public void calculateInterestRequest() throws Exception {
    Mortgage mortgage = getMortgage(MORTGAGE_ID, INTEREST_RATE);
    MortgagePart mortgagePartOne = getMortgagePart(mortgage, INVESTOR_NAME, INVESTMENT_VALUE);
    MortgagePart mortgagePartTwo = getMortgagePart(mortgage, INVESTOR_NAME_TWO, INVESTMENT_VALUE_TWO);
    mortgage.addMortgagePart(mortgagePartOne);
    mortgage.addMortgagePart(mortgagePartTwo);

    when(mortgageRepository.findAll()).thenReturn(Collections.singletonList(mortgage));

    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusDays(5);

    mockMvc.perform(get("/api/mortgages/investor/interests").param("startDate", startDate.toString())
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
    mortgagePart.setMortgagePartId(MORTGAGE_PART_ID);
    mortgagePart.setMortgage(mortgage);
    mortgagePart.setInvestmentValue(new BigDecimal(investmentValue));
    mortgagePart.setInvestorName(investorName);
    return mortgagePart;
  }

}
