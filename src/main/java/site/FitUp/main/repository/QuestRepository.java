package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.api.quest.dtos.QuestResponse;
import site.FitUp.main.common.enums.QuestType;
import site.FitUp.main.model.DailyResult;
import site.FitUp.main.model.Quest;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest,String> {

    List<Quest> findAllByDailyResultAndType(DailyResult dailyResult, QuestType type);
    Quest findByDailyResultAndQuestId(DailyResult dailyResult,String questId);
}
