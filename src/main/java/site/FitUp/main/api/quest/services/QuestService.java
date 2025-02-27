package site.FitUp.main.api.quest.services;

import site.FitUp.main.api.quest.dtos.QuestRequest;
import site.FitUp.main.api.quest.dtos.QuestResponse;

public interface QuestService {
    QuestResponse.CreateQuestsResponse createQuestsService(QuestRequest.CreateQuestsRequest request,String userId);
}
