package site.FitUp.main.api.quest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class QuestResponse {
    @Builder
    @Getter
    public static class QuestDto {
        private String questId;
        private String content;
        private int exp;
        private boolean isSuccess;
    }
    @Builder
    @Getter
    public static class DailyQuest{
        private List<QuestDto> fitness;
        private QuestDto sleep;
        private QuestDto daily;
    }
    @Builder
    @Getter
    public static class CreateQuestsResponse {
        private DailyQuest dailyQuest;
    }
    @Builder
    @Getter
    public static class AcceptQuestsResponse {
        private long dailyResultSeq;
        private QuestRequest.RequestDailyQuest dailyQuest;
    }
    @Builder
    @Getter
    public static class QuestsRecord{
        private long dailyResultSeq;
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
        private int exp;
        @JsonProperty("isSuccess")
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
        private long dailyResultSeq;
        private String questId;
        private int questSuccessCount;
        private String questStatus;
        private String updatedAt;
    }
    @Builder
    @Getter
    public static class GetQuestTierResponse{
        private int previousExp;
        private int currentExp;
    }
}
