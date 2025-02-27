package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.DailyResult;
import site.FitUp.main.model.User;

import java.util.List;

public interface DailyResultRepository extends JpaRepository<DailyResult,Long> {
    List<DailyResult> findAllByUserOrderByCreatedAtDesc(User user);
}
