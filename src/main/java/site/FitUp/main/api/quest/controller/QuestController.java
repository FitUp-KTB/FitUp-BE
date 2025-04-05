package site.FitUp.main.api.quest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.FitUp.main.api.quest.dtos.QuestRequest;
import site.FitUp.main.api.quest.dtos.QuestResponse;
import site.FitUp.main.api.quest.services.QuestService;
import site.FitUp.main.common.ApiResponse;
import site.FitUp.main.common.enums.QuestStatus;
import site.FitUp.main.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quests")
public class QuestController {
    private final QuestService questService;
    @PostMapping("")
    public ApiResponse<QuestResponse.CreateQuestsResponse>CreateQuestController( @RequestBody QuestRequest.CreateQuestsRequest request){

        List<QuestResponse.QuestDto> fitness=new ArrayList<>();
        fitness.add(QuestResponse.QuestDto.builder()
                .questId("QUEST1")
                .content("벤치프레스 65kg 5세트 수행")
                .isSuccess(false).build());
        fitness.add(QuestResponse.QuestDto.builder()
                .questId("QUEST2")
                .content("덤벨 벤치프레스 20kg 5세트").isSuccess(false).build());
        fitness.add(QuestResponse.QuestDto.builder()
                .questId("QUEST3")
                .content("인클라인 벤치프레스 55kg 5세트").isSuccess(false).build());
        QuestResponse.QuestDto sleep= QuestResponse.QuestDto.builder()
                .questId("QUEST4")
                .content("수면 7시간 30분 유지")
                .isSuccess(false).build();
        QuestResponse.QuestDto daily= QuestResponse.QuestDto.builder()
                .questId("QUEST5")
                .content("점심 식단에 단백질 20g 추가").isSuccess(false).build();
        QuestResponse.DailyQuest quests=QuestResponse.DailyQuest.builder()
                .fitness(fitness)
                .sleep(sleep)
                .daily(daily)
                .build();
//        return new ApiResponse<>(QuestResponse.CreateQuestsResponse.builder()
//                .dailyQuest(quests).build());

        return new ApiResponse<>(questService.createQuestsService(request,JwtUtil.getAuthenticatedUserId()));
    }
    @GetMapping("")
    public ApiResponse<QuestResponse.GetQuestsResponse>GetQuestsController(){

        return new ApiResponse<>(questService.getQuestsService(JwtUtil.getAuthenticatedUserId()));
    }
    @GetMapping("/{dailyResultSeq}")
    public ApiResponse<QuestResponse.GetQuestResponse>GetQuestController( @PathVariable(value = "dailyResultSeq", required = true) long dailyResultSeq){
        List<QuestResponse.QuestRecord> fitness=new ArrayList<>();
        fitness.add(QuestResponse.QuestRecord.builder()
                .questId("QUEST1")
                .content("벤치프레스 65kg 5세트 수행")
                .isSuccess(true).build());
        fitness.add(QuestResponse.QuestRecord.builder()
                .questId("QUEST2")
                .content("덤벨 벤치프레스 20kg 5세트")
                .isSuccess(false).build());
        fitness.add(QuestResponse.QuestRecord.builder()
                .questId("QUEST3")
                .content("인클라인 벤치프레스 55kg 5세트")
                .isSuccess(true).build());
        QuestResponse.QuestRecord sleep= QuestResponse.QuestRecord.builder()
                .questId("QUEST4")
                .content("수면 7시간 30분 유지")
                .isSuccess(false)
                .build();
        QuestResponse.QuestRecord daily= QuestResponse.QuestRecord.builder()
                .questId("QUEST5")
                .content("점심 식단에 단백질 20g 추가")
                .isSuccess(false)
                .build();
        QuestResponse.GetQuestResponse quests=QuestResponse.GetQuestResponse.builder()
                .fitness(fitness)
                .sleep(sleep)
                .daily(daily)
                .build();
//        return new ApiResponse<>(quests);
        return new ApiResponse<>(questService.getQuestService(JwtUtil.getAuthenticatedUserId(),dailyResultSeq));
    }
    @PostMapping("/{dailyResultSeq}/{questId}")
    public ApiResponse<QuestResponse.DoQuestResponse>DoQuestController( @PathVariable(value = "dailyResultSeq", required = true) int dailyResultSeq,@PathVariable(value = "questId", required = true) String questId){

        return new ApiResponse<>(questService.doQuestService(JwtUtil.getAuthenticatedUserId(),dailyResultSeq,questId));
    }

    @GetMapping("/tier")
    public ApiResponse<QuestResponse.GetQuestTierResponse>GetQuestsTierController(){

        return new ApiResponse<>(questService.getQuestTierService(JwtUtil.getAuthenticatedUserId()));
    }
    @PostMapping("/accept")
    public ApiResponse<QuestResponse.AcceptQuestsResponse>AcceptQuestsController(@RequestBody QuestRequest.AcceptQuestRequest request){

        return new ApiResponse<>(questService.acceptQuestService(request,JwtUtil.getAuthenticatedUserId()));
    }

}
