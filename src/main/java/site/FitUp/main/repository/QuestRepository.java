package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.Quest;

public interface QuestRepository extends JpaRepository<Quest,String> {
}
