package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.DailyResult;

public interface DailyResultRepository extends JpaRepository<DailyResult,Long> {
}
