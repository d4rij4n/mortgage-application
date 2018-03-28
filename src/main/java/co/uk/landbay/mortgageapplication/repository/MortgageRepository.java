package co.uk.landbay.mortgageapplication.repository;

import co.uk.landbay.mortgageapplication.model.Mortgage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageRepository extends JpaRepository<Mortgage, Long> {
}
