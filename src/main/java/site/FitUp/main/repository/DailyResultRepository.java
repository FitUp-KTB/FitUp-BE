package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.FitUp.main.model.DailyResult;
import site.FitUp.main.model.Quest;
import site.FitUp.main.model.User;

import java.util.List;

public interface DailyResultRepository extends JpaRepository<DailyResult,Long> {
    List<DailyResult> findAllByUserOrderByCreatedAtDesc(User user);
    @Query("SELECT d FROM DailyResult d WHERE FUNCTION('MONTH', d.createdAt) = :month")
    List<DailyResult> findAllByCreatedAtMonth(@Param("month") int month);

    @Query("SELECT SUM(d.pointSum) FROM DailyResult d WHERE FUNCTION('MONTH', d.createdAt) = :month AND d.user = :user")
    Integer sumPointSumByMonthAndUser(@Param("month") int month, @Param("user") User user);


}
