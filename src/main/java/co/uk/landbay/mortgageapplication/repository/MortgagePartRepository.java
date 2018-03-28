package co.uk.landbay.mortgageapplication.repository;

import co.uk.landbay.mortgageapplication.model.MortgagePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgagePartRepository extends JpaRepository<MortgagePart, Long> {
}
