package site.FitUp.main.api.quest.services;

import site.FitUp.main.api.quest.dtos.QuestRequest;
import site.FitUp.main.api.quest.dtos.QuestResponse;
import site.FitUp.main.model.Quest;

public interface QuestService {
    QuestResponse.CreateQuestsResponse createQuestsService(QuestRequest.CreateQuestsRequest request, String userId);
    QuestResponse.AcceptQuestsResponse acceptQuestService(QuestRequest.AcceptQuestRequest request, String userId);

    QuestResponse.GetQuestsResponse getQuestsService(String userId);

    QuestResponse.GetQuestResponse getQuestService(String userId,long dailyResultSeq);
    QuestResponse.DoQuestResponse doQuestService(String userId,long dailyResultSeq,String questId);

    QuestResponse.GetQuestTierResponse getQuestTierService(String userId);
}
