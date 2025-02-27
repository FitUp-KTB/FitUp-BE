package site.FitUp.main.api.quest.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class QuestResponse {
    @Builder
    @Getter
    public static class Quest{
        private String questId;
        private String content;
        private boolean isSuccess;
    }
    @Builder
    @Getter
    public static class DailyQuest{
        private List<Quest> fitness;
        private Quest sleep;
        private Quest daily;
    }
    @Builder
    @Getter
    public static class CreateQuestsResponse{
        private int dailyResultSeq;
        private DailyQuest dailyQuest;
    }
    @Builder
    @Getter
    public static class QuestsRecord{
        private int dailyResultSeq;
        private String questStatus;
        private int questSuccessCount;
        private String createdAt;
    }
    @Builder
    @Getter
    public static class GetQuestsResponse{
        private List<QuestsRecord> quests;
    }
    @Builder
    @Getter
    public static class QuestRecord{
        private String questId;
        private String content;

        private boolean isSuccess;
    }
    @Builder
    @Getter
    public static class GetQuestResponse{
        List<QuestRecord> fitness;
        QuestRecord sleep;
        QuestRecord daily;

    }

    @Builder
    @Getter
    public static class DoQuestResponse{
        private int dailyResultSeq;
        private String questId;
        private int questSuccessCount;
        private String questStatus;
        private String updatedAt;
    }
}
