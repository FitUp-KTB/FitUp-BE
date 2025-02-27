package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.UserStat;

public interface UserStatRepository extends JpaRepository<UserStat,Long> {
}
