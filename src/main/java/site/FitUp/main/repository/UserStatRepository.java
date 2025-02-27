package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.User;
import site.FitUp.main.model.UserStat;

import java.util.List;

public interface UserStatRepository extends JpaRepository<UserStat,Long> {
    List<UserStat> findAllByUserOrderByCreatedAtDesc(User user);
    UserStat findTopByUserOrderByCreatedAtDesc(User user);
}
