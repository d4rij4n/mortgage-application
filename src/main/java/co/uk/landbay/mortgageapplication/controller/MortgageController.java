package co.uk.landbay.mortgageapplication.controller;

import co.uk.landbay.mortgageapplication.exception.ResourceNotFoundException;
import co.uk.landbay.mortgageapplication.model.Mortgage;
import co.uk.landbay.mortgageapplication.model.MortgagePart;
import co.uk.landbay.mortgageapplication.model.dto.InvestorInterestDto;
import co.uk.landbay.mortgageapplication.repository.MortgagePartRepository;
import co.uk.landbay.mortgageapplication.repository.MortgageRepository;
import co.uk.landbay.mortgageapplication.util.MortgageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MortgageController {

  private MortgageRepository mortgageRepository;

  private MortgagePartRepository mortgagePartRepository;

  @Autowired
  public MortgageController(MortgageRepository mortgageRepository, MortgagePartRepository mortgagePartRepository) {
    this.mortgageRepository = mortgageRepository;
    this.mortgagePartRepository = mortgagePartRepository;
  }

  @PostMapping("/mortgage")
  public Mortgage createMortgage(@Valid @RequestBody Mortgage mortgage) {
    return mortgageRepository.save(mortgage);
  }

  @PostMapping("/mortgage/{id}/investment")
  public MortgagePart createInvestment(@PathVariable(value = "id") long mortgageId,
                                       @Valid @RequestBody MortgagePart mortgagePart) {
    Mortgage mortgage = getMortgageById(mortgageId);
    mortgage.addMortgagePart(mortgagePart);
    mortgagePart.setMortgage(mortgage);

    return mortgagePartRepository.save(mortgagePart);
  }

  @GetMapping("/mortgage/{id}")
  public Mortgage getMortgage(@PathVariable(value = "id") long mortgageId) {
    return getMortgageById(mortgageId);
  }

  @DeleteMapping("/mortgage/{id}")
  public ResponseEntity<?> deleteMortgage(@PathVariable(value = "id") long mortgageId) {
    Mortgage mortgage = getMortgageById(mortgageId);
    mortgageRepository.delete(mortgage);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/mortgages/investor/interests")
  public List<InvestorInterestDto> calculateInterest(
          @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
          @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    List<Mortgage> mortgages = mortgageRepository.findAll();

    if (mortgages.isEmpty()) {
      return Collections.emptyList();
    }

    /* Ideally, the code below should be part of a service layer class, but is not due to mostly CRUD
       operation methods in the controller */
    List<InvestorInterestDto> investorInterestDtos = new ArrayList<>();
    for (Mortgage mortgage : mortgages) {
      for (MortgagePart mortgagePart : mortgage.getMortgageParts()) {
        investorInterestDtos.add(new InvestorInterestDto(mortgagePart.getInvestorName(),
                                                         MortgageUtils.calculateInterestEarned(mortgagePart.getInvestmentValue(),
                                                                                               mortgage.getInterestRate(),
                                                                                               startDate,
                                                                                               endDate),
                                                         startDate,
                                                         endDate));
      }
    }

    return investorInterestDtos;
  }

  private Mortgage getMortgageById(long mortgageId) {
    return mortgageRepository.findById(mortgageId).orElseThrow(() -> new ResourceNotFoundException("Mortgage", "id", mortgageId));
  }

}
