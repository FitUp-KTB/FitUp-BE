package site.FitUp.main.api.quest.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class QuestRequest {
    @Getter
    @Setter
    public static class CreateQuestsRequest{
        private String mainCategory;
        private String subCategory;
        private String userRequest;
        private String injury;
    }
    @Getter
    @Setter
    public static class AcceptQuestRequest{
        private RequestDailyQuest dailyQuest;
    }
    @Builder
    @Getter
    public static class RequestQuestDto {
        private String questId;
        private String content;
        private boolean isSuccess;
    }
    @Builder
    @Getter
    public static class RequestDailyQuest{
        private List<RequestQuestDto> fitness;
        private RequestQuestDto sleep;
        private RequestQuestDto daily;
    }

}
