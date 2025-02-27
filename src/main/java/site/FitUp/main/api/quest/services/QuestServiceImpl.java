package site.FitUp.main.api.quest.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        UserStatResult userStatResult=userStatResultRepository.findByUserStat(userStat);
        //더미 인풋 데이터
        String[] completedQuest={"벤치 프레스 60kg 5세트"};
        String[] failedQuest={"하루 2L 물 섭취"};
        //DB에서 새로 값 가져올거 가져와야함(gender,height,weight)
        JSONObject stats=new JSONObject()
                .put("strength",userStatResult.getStrength())
                .put("stamina",userStatResult.getStamina())
                .put("endurance",userStatResult.getEndurance());
        JSONObject lastQuest=new JSONObject()
                .put("completed",completedQuest)
                .put("failed",failedQuest);
        // JSON 형식으로 입력 데이터 설정
        JSONObject inputJson = new JSONObject()
                .put("user_id", userId)
                .put("gender", user.getGender().toString())
                .put("chronic",user.getChronic())
                .put("stat",stats)
                .put("main_category",request.getMainCategory())
                .put("sub_category",request.getSubCategory())
                .put("user_request",request.getUserRequest())
                .put("injury",request.getInjury())
                .put("goal",user.getGoal())
                .put("lasted_quest_status",lastQuest);

        // 시스템 명령어 설정
        String systemInstruction = getSystemInstruction();

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문에 시스템 규칙과 사용자 입력 데이터 포함
        JSONObject body = new JSONObject();
        body.put("contents", new JSONObject().put("parts", new JSONObject().put("text", systemInstruction+"\n"+inputJson.toString())));
//        body.put("user_input", inputJson);
        // HttpEntity에 요청 본문과 헤더 설정
        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        try {
            // RestTemplate을 사용하여 POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Response Body: " + response.getBody());
                JSONObject responseJson=new JSONObject(response.getBody());

                JSONArray candiate= responseJson.getJSONArray("candidates");

                if(candiate.length()>0){
                    JSONObject content = candiate.getJSONObject(0).getJSONObject("content");
                    JSONArray parts=content.getJSONArray("parts");
                    if(parts.length()>0){
                        String text = parts.getJSONObject(0).getString("text");

                        String cleanedJson = text.replaceAll("```json\\n|```", "").trim();
                        JSONObject parsedJson = new JSONObject(cleanedJson);

                        //스탯 저장해야하는 로직 들어갈 자리
                        //daily quest 파싱
                        JSONObject dailyQuestsJson=parsedJson.getJSONObject("daily_quests");


                        // Fitness 퀘스트 파싱
                        List<QuestResponse.QuestDto> fitnessQuestDtos = new ArrayList<>();
                        JSONObject fitnessJson = dailyQuestsJson.getJSONObject("fitness");

                        for (String key : fitnessJson.keySet()) {
                            String newQuestId=generateQuestId();
                            JSONObject questObj = fitnessJson.getJSONObject(key);
                            QuestResponse.QuestDto questDto = QuestResponse.QuestDto.builder()
                                    .questId(newQuestId) // UUID 기반 QuestId 생성
                                    .content(questObj.getString("contents"))
                                    .isSuccess(false)
                                    .build();
                            fitnessQuestDtos.add(questDto);
                        }

                        // Sleep 퀘스트 파싱
                        QuestResponse.QuestDto sleepQuestDto = QuestResponse.QuestDto.builder()
                                .questId(generateQuestId()) // UUID 기반 QuestId 생성
                                .content(dailyQuestsJson.getJSONObject("sleep").getString("contents"))
                                .isSuccess(false)
                                .build();

                        // Daily 퀘스트 파싱
                        QuestResponse.QuestDto dailyQuestDto = QuestResponse.QuestDto.builder()
                                .questId(generateQuestId()) // UUID 기반 QuestId 생성
                                .content(dailyQuestsJson.getJSONObject("daily").getString("contents"))
                                .isSuccess(false)
                                .build();

                        // 최종 객체 생성
                        QuestResponse.DailyQuest dailyQuestObject = QuestResponse.DailyQuest.builder()
                                .fitness(fitnessQuestDtos)
                                .sleep(sleepQuestDto)
                                .daily(dailyQuestDto)
                                .build();

                        return QuestResponse.CreateQuestsResponse.builder()// 여기에 실제 DB에서 생성된 결과 ID 넣을 것
                                .dailyQuest(dailyQuestObject)
                                .build();

                    }


                }
                // 응답을 처리하고 StatResponse로 반환 (JSON 파싱)
                return null;
            } else {
                logger.error("Error Response: " + response.getStatusCode());
                throw new Exception("Failed to get a valid response: " + response.getStatusCode());
            }
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
