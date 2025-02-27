package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.UserStatResult;

public interface UserStatResultRepository extends JpaRepository<UserStatResult,Long> {
}
