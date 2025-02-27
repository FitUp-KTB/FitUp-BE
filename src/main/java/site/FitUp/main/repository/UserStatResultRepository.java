package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.UserStat;
import site.FitUp.main.model.UserStatResult;

import java.util.List;

public interface UserStatResultRepository extends JpaRepository<UserStatResult,Long> {
    UserStatResult findByUserStat(UserStat userStat);
}
