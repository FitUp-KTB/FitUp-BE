package site.FitUp.main.api.quest.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import site.FitUp.main.api.quest.dtos.QuestRequest;
import site.FitUp.main.api.quest.dtos.QuestResponse;
import site.FitUp.main.common.enums.QuestStatus;
import site.FitUp.main.common.enums.QuestType;
import site.FitUp.main.model.*;
import site.FitUp.main.repository.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestServiceImpl implements QuestService{
    private final RestTemplate restTemplate;
    private final UserStatRepository userStatRepository;
    private final UserStatResultRepository userStatResultRepository;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final DailyResultRepository dailyResultRepository;
    @Value("${gemini.api.key}")
    private  String API_URL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public QuestResponse.CreateQuestsResponse createQuestsService(QuestRequest.CreateQuestsRequest request, String userId){
        User user= userRepository.findById(userId).orElse(null);
        UserStat userStat=userStatRepository.findTopByUserOrderByCreatedAtDesc(user);
        if(userStat==null){
            userStat=UserStat.builder()
                    .pushUps(0)
                    .sitUp(0)
                    .runningPace(0.0)
                    .runningTime(0)
                    .squat(0)
                    .benchPress(0)
                    .deadLift(0).build();
        }
        log.info("Request Body : "+request.getMainCategory());
        JSONObject stats = new JSONObject()
                .put("pushups", userStat.getPushUps())
                .put("situps", userStat.getSitUp())
                .put("running_pace", userStat.getRunningPace())
                .put("running_time", userStat.getRunningTime())
                .put("squat", userStat.getSquat())
                .put("bench_press", userStat.getBenchPress())
                .put("deadlift", userStat.getDeadLift());

        JSONObject requestBody = new JSONObject()
                .put("user_id", userId)
                .put("gender", user.getGender().toString())
                .put("chronic", user.getChronic())
                .put("stats", stats)
                .put("main_category", request.getMainCategory())
                .put("sub_category", request.getSubCategory())
                .put("user_request", request.getUserRequest())
                .put("goal", user.getGoal());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            // **새로운 API URL로 요청**
            ResponseEntity<String> response = restTemplate.exchange(

                    API_URL+"/query", // 변경할 API URL
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {

                // **응답 문자열에서 마크다운 제거**
                String rawResponse = response.getBody();
                logger.info("Raw Response: " + rawResponse);

                // 마크다운(```json\n`) 및 ``` 제거
                String cleanedJson = rawResponse.replaceAll("```[a-zA-Z]*\\s*", "").replaceAll("```", "");

                // \n 및 \ 제거
                cleanedJson = cleanedJson.replace("\\n", "").replace("\\", "").trim();

                // 문자열이 큰따옴표로 감싸져 있는 경우 제거
                if (cleanedJson.startsWith("\"") && cleanedJson.endsWith("\"")) {
                    cleanedJson = cleanedJson.substring(1, cleanedJson.length() - 1);
                }

                // 로그 출력 (정리된 JSON)
                logger.info("Cleaned JSON: " + cleanedJson);

                // **JSON 파싱**
                JSONObject responseJson = new JSONObject(cleanedJson);

                // **응답 JSON에서 올바른 키 값 사용 (daily_quests)**
                JSONObject dailyQuestsJson = responseJson.getJSONObject("daily_quests");

                // **Fitness 퀘스트 파싱**
                List<QuestResponse.QuestDto> fitnessQuestDtos = new ArrayList<>();
                JSONObject fitnessObject = dailyQuestsJson.getJSONObject("fitness");

                for (String key : fitnessObject.keySet()) {
                    JSONObject questObj = fitnessObject.getJSONObject(key);
                    QuestResponse.QuestDto questDto = QuestResponse.QuestDto.builder()
                            .questId(generateQuestId())
                            .content(questObj.getString("contents"))  // "description" → "contents"
                            .exp(questObj.getInt("points"))           // "exp" → "points"
                            .isSuccess(false)
                            .build();
                    fitnessQuestDtos.add(questDto);
                }

                // **수면(Sleep) 퀘스트 파싱**
                QuestResponse.QuestDto sleepQuestDto = QuestResponse.QuestDto.builder()
                        .questId(generateQuestId())
                        .content(dailyQuestsJson.getJSONObject("sleep").getString("contents")) // "description" → "contents"
                        .exp(dailyQuestsJson.getJSONObject("sleep").getInt("points"))         // "exp" → "points"
                        .isSuccess(false)
                        .build();

                // **일상(Daily) 퀘스트 파싱**
                QuestResponse.QuestDto dailyQuestDto = QuestResponse.QuestDto.builder()
                        .questId(generateQuestId())
                        .content(dailyQuestsJson.getJSONObject("daily").getString("contents")) // "description" → "contents"
                        .exp(dailyQuestsJson.getJSONObject("daily").getInt("points"))         // "exp" → "points"
                        .isSuccess(false)
                        .build();

                // **최종 응답 객체 생성**
                QuestResponse.DailyQuest dailyQuestObject = QuestResponse.DailyQuest.builder()
                        .fitness(fitnessQuestDtos)
                        .sleep(sleepQuestDto)
                        .daily(dailyQuestDto)
                        .build();

                return QuestResponse.CreateQuestsResponse.builder()
                        .dailyQuest(dailyQuestObject)
                        .build();



            } else {
                logger.error("Error Response: " + response.getStatusCode());
                throw new Exception("Failed to get a valid response: " + response.getStatusCode());
            }
        } catch (JSONException e) {
            logger.error("JSON Parsing Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error Response: " + e);
        }

        return null;
    }

    @Transactional
    public QuestResponse.AcceptQuestsResponse acceptQuestService(QuestRequest.AcceptQuestRequest request, String userId){
        User user=userRepository.findById(userId).orElse(null);
        //데일리 퀘스트 생성
        DailyResult dailyResult=DailyResult.builder()
                .questStatus(QuestStatus.FAIL)
                .pointSum(0)
                .questSuccessCount(0)
                .user(user).build();
        DailyResult newDailyResult=dailyResultRepository.save(dailyResult);

        //피트니스 퀘스트
        for(QuestRequest.RequestQuestDto fitness:request.getDailyQuest().getFitness()){
            Quest quest=Quest.builder()
                    .questId(fitness.getQuestId())
                    .content(fitness.getContent())
                    .dailyResult(newDailyResult)
                    .isSuccess(false)
                    .type(QuestType.FITNESS)
                    .point(fitness.getExp()).build();
            questRepository.save(quest);
        }
        //잠 퀘스트
        Quest sleepQuest=Quest.builder()
                .questId(request.getDailyQuest().getSleep().getQuestId())
                .content(request.getDailyQuest().getSleep().getContent())
                .point(request.getDailyQuest().getSleep().getExp())
                .type(QuestType.SLEEP)
                .isSuccess(false)
                .dailyResult(newDailyResult).build();
        questRepository.save(sleepQuest);
        //생활 패턴 퀘스트
        Quest dailyQeust=Quest.builder()
                .questId(request.getDailyQuest().getDaily().getQuestId())
                .content(request.getDailyQuest().getDaily().getContent())
                .point(request.getDailyQuest().getDaily().getExp())
                .type(QuestType.DAILY)
                .isSuccess(false)
                .dailyResult(newDailyResult).build();
        questRepository.save(dailyQeust);

        return QuestResponse.AcceptQuestsResponse.builder()
                .dailyResultSeq(newDailyResult.getDailyResultSeq())
                .dailyQuest(request.getDailyQuest()).build();

    }
    public QuestResponse.GetQuestsResponse getQuestsService(String userId){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user=userRepository.findById(userId).orElse(null);
        List<DailyResult> dailyResults=dailyResultRepository.findAllByUserOrderByCreatedAtDesc(user);
        List<QuestResponse.QuestsRecord> questsRecords=dailyResults.stream().map(dailyResult -> {
            return QuestResponse.QuestsRecord.builder()
                    .dailyResultSeq(dailyResult.getDailyResultSeq())
                    .questStatus(dailyResult.getQuestStatus().toString())
                    .questSuccessCount(dailyResult.getQuestSuccessCount())
                    .createdAt(dailyResult.getCreatedAt().toLocalDate().format(formatter)).build();

        }).toList();
        return QuestResponse.GetQuestsResponse.builder().quests(questsRecords).build();
    }

    public QuestResponse.GetQuestResponse getQuestService(String userId,long dailyResultSeq){
        DailyResult dailyResult=dailyResultRepository.findById(dailyResultSeq).orElse(null);
        List<Quest> fitnessList=questRepository.findAllByDailyResultAndType(dailyResult,QuestType.FITNESS);
        Quest sleep=questRepository.findAllByDailyResultAndType(dailyResult,QuestType.SLEEP).get(0);
        Quest daily=questRepository.findAllByDailyResultAndType(dailyResult,QuestType.DAILY).get(0);
        List<QuestResponse.QuestRecord> fitnessResponse=fitnessList.stream().map(fitness->{
            return QuestResponse.QuestRecord.builder()
                    .questId(fitness.getQuestId())
                    .content(fitness.getContent())
                    .exp(fitness.getPoint())
                    .isSuccess(fitness.getIsSuccess()).build();
        }).toList();
        QuestResponse.QuestRecord sleepResponse=QuestResponse.QuestRecord.builder()
                .questId(sleep.getQuestId())
                .content(sleep.getContent())
                .exp(sleep.getPoint())
                .isSuccess(sleep.getIsSuccess())
                .build();
        QuestResponse.QuestRecord dailyResponse=QuestResponse.QuestRecord.builder()
                .questId(daily.getQuestId())
                .content(daily.getContent())
                .exp(daily.getPoint())
                .isSuccess(daily.getIsSuccess())
                .build();
        return QuestResponse.GetQuestResponse.builder()
                .fitness(fitnessResponse)
                .sleep(sleepResponse)
                .daily(dailyResponse).build();
    }
    @Transactional
    public QuestResponse.DoQuestResponse doQuestService(String userId,long dailyResultSeq,String questId){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user=userRepository.findById(userId).orElse(null);
        DailyResult dailyResult=dailyResultRepository.findById(dailyResultSeq).orElse(null);
        Quest quest=questRepository.findByDailyResultAndQuestId(dailyResult,questId);
        if(!quest.getIsSuccess()) {
            quest.setIsSuccess(true);
            int currentCount = dailyResult.getQuestSuccessCount();
            int currentExp=dailyResult.getPointSum();
            dailyResult.setQuestSuccessCount(currentCount + 1);
            dailyResult.setPointSum(currentExp+quest.getPoint());
            //만약 운동 퀘스트가 하나도 완료되지 않는 상태에서 운동퀘스트가 완료됬으면 Success로 변경
            if (dailyResult.getQuestStatus().equals(QuestStatus.FAIL) && quest.getType().equals(QuestType.FITNESS)) {
                dailyResult.setQuestStatus(QuestStatus.SUCCESS);
            }
            //완료된 퀘스트가 5 이상이면, perfect
            if (dailyResult.getQuestSuccessCount() >= 5) {
                dailyResult.setQuestStatus(QuestStatus.PERFECT);
            }
        }
        return QuestResponse.DoQuestResponse.builder()
                .dailyResultSeq(dailyResultSeq)
                .questId(quest.getQuestId())
                .questStatus(dailyResult.getQuestStatus().toString())
                .questSuccessCount(dailyResult.getQuestSuccessCount())
                .updatedAt(dailyResult.getUpdatedAt().toLocalDate().format(formatter))
                .build();
    }

    public QuestResponse.GetQuestTierResponse getQuestTierService(String userId){
        User user= userRepository.findById(userId).orElse(null);
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int month=now.getMonthValue();
        int previousMonth = (month + 10) % 12 + 1;
        int nowMonthExp= Optional.ofNullable(dailyResultRepository.sumPointSumByMonthAndUser(month, user)).orElse(0);

        int previousMonthExp= Optional.ofNullable(dailyResultRepository.sumPointSumByMonthAndUser(previousMonth, user)).orElse(0);
        return QuestResponse.GetQuestTierResponse.builder()
                .currentExp(nowMonthExp)
                .previousExp(previousMonthExp)
                .build();


    }
    public String getSystemInstruction(){
        return  """
                    너는 사람들의 운동을 돕는 게임 기반의 퀘스트 생성 시스템이야
                    너가 수행해야 할 역할은 퀘스트 생성이야
                    {
                      "user_id": "12345",
                      "gender" : "male",
                      "chronic" : "척추 측만증",
                      "stats": {
                        "strength": 70,
                        "stamina": 60,
                        "endurance": 50
                      },
                      "main_category" : "헬스",
                      "sub_category" : "하체"
                      "user_request" : "오늘은 하체 운동을 하고 싶어",
                      "goal": "근력 증가",
                      "last_quest_status": {
                        "completed": ["벤치프레스 60kg 5세트"],
                        "failed": ["하루 2L 물 섭취"]
                      }
                    }
                    다음과 같은 입력 데이터를 받아서
                    {
                      "user_id": "12345",
                      "daily_quests": {
                        "fitness" : {
                    1 : {"contents" : "스쿼트 80kg 5세트 수행", "points" : 10},
                    2 : {"contents" : "레그 익스텐션 50kg 5세트", "points" : 5},
                    3 : {"contents" : "레그프레스 160kg 5세트", "points" : 20}
                    }
                        "sleep" : {"contents" : "수면 8시간 유지", "points" : 5},
                        "daily" : {"contents" : "아침 공복에 물 500ml 마시기", "points" : 5}
                      }
                    }
                    
                    형태의 출력을 내도록 할거야
                    
                    daily_quests의 daily는 목표에 맞는 식단이나 생활습관 등 관련하여 퀘스트를 만들어주고 goal과 last_quest_status에만 영향을 받게 해줘
                    
                    daily_quests의 fitness는 입력 데이터의 main_category,sub_category, user_request와 goal, stats, last_quest_status, gender, chronic의 영향을 받아 종목 및 난이도가 조정될거야 종목은 최대한 세부적으로 선정을 해줘, 세트 운동의 경우 몇개 몇세트인지도 알려줘야해 chronic 의 값이 없으면 상관이 없지만 있을 때 해당 질환으로 인해 특정 운동이 악영향이 있거나 할 수 있으니 강도나 종목의 선정을 진행할 때 반영해줘
                    ---
                    main_category == "부상"  일때 sub category는 없이 user request에 부상 부위랑 증상 내용이 있으니까 "daily_quests"의 "fitness" 값을 운동말고 처방 혹은 휴식관리를 추천해줘.
                    ---
                    그리고 모든 퀘스트를 생성할 때 수행 완료시 포인트를 만들건데 포인트는 운동 카테고리는 너가 판단 해서 쉬우면 5점 보통이면 10점 어려우면 20점으로 해주고 수면과 생활습관 퀘스트는 5점으로 고정해줘
                    모든 출력은 설명 및 상세 분석이 없이 단순 JSON만 반환해줘
                """;
    }
    // UUID를 사용하여 QuestId 생성하는 메서드 추가
    private String generateQuestId() {
        return "QUEST-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8); // 8자리 랜덤 ID
    }
}
