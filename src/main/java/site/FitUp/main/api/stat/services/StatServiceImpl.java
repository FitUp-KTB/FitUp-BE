package site.FitUp.main.api.stat.services;

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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import site.FitUp.main.api.stat.dtos.StatRequest;
import site.FitUp.main.api.stat.dtos.StatResponse;
import site.FitUp.main.common.enums.CharacterType;
import site.FitUp.main.model.User;
import site.FitUp.main.model.UserStat;
import site.FitUp.main.model.UserStatResult;
import site.FitUp.main.repository.UserRepository;
import site.FitUp.main.repository.UserStatRepository;
import site.FitUp.main.repository.UserStatResultRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

    private final RestTemplate restTemplate;
    private final UserStatRepository statRepository;
    private final UserRepository userRepository;
    private final UserStatResultRepository userStatResultRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${gemini.api.key}")
    private  String API_URL;

    public StatResponse.CreateStatResponse CreateStatService(StatRequest.CreateStatRequest request, String userId) throws Exception {
        User user=userRepository.findById(userId).orElse(null);
        //DB에서 새로 값 가져올거 가져와야함(gender,height,weight)
        // JSON 형식으로 입력 데이터 설정
        JSONObject inputJson = new JSONObject()
                .put("user_id", userId)
                .put("gender", user.getGender().toString())
                .put("chronic",user.getChronic())
                .put("height", request.getHeight())
                .put("weight", request.getWeight())
                .put("muscle_mass", request.getMuscleMass())
                .put("body_fat", request.getBodyFat())
                .put("pushups", request.getPushUps())
                .put("situps", request.getSitUps())
                .put("running_pace", request.getRunningPace())
                .put("running_time", request.getRunningTime())
                .put("squat", request.getSquat())
                .put("bench_press", request.getBenchPress())
                .put("deadlift", request.getDeadLift());

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 바디 설정
        HttpEntity<String> entity = new HttpEntity<>(inputJson.toString(), headers);

        try {
            // API 호출 (새로운 URL 적용)
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://musical-barnacle-6ww76ggw69vfx4vq-8000.app.github.dev/stats",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Raw Response: " + response.getBody());

                // 마크다운 제거 (` ```json\n ` 같은 형식)
                String cleanedJson = response.getBody()
                        .replaceAll("```[a-zA-Z]*\\s*", "") // ```json\n 또는 ``` 제거
                        .replaceAll("```", "") // 남아있는 ``` 제거
                        .replace("\\n", "") // \n 제거
                        .replace("\\", ""); // \ 제거

                // 문자열이 큰따옴표로 감싸진 경우 제거
                if (cleanedJson.startsWith("\"") && cleanedJson.endsWith("\"")) {
                    cleanedJson = cleanedJson.substring(1, cleanedJson.length() - 1);
                }

                logger.info("Cleaned JSON: " + cleanedJson);

                // JSON 파싱
                JSONObject responseJson = new JSONObject(cleanedJson);

                // 필요한 데이터 추출
                int strength = responseJson.getInt("strength");
                int endurance = responseJson.getInt("endurance");
                int speed = responseJson.getInt("speed");
                int flexibility = responseJson.getInt("flexibility");
                int stamina = responseJson.getInt("stamina");
                String characterType = responseJson.getString("character_type");

                // 요청 값 저장
                UserStat userStat = UserStat.builder()
                        .user(user)
                        .fat(request.getBodyFat())
                        .height(request.getHeight())
                        .weight(request.getWeight())
                        .muscleMass(request.getMuscleMass())
                        .pushUps(request.getPushUps())
                        .sitUp(request.getSitUps())
                        .runningPace(request.getRunningPace())
                        .runningTime(request.getRunningTime())
                        .squat(request.getSquat())
                        .benchPress(request.getBenchPress())
                        .deadLift(request.getDeadLift())
                        .build();
                UserStat newUserStat = statRepository.save(userStat);
                log.info(String.valueOf(newUserStat.getUserStatSeq()));
                statRepository.flush();

                // 응답 값 저장
                UserStatResult userStatResult = UserStatResult.builder()
                        .userStat(newUserStat)
                        .strength(strength)
                        .endurance(endurance)
                        .speed(speed)
                        .flexibility(flexibility)
                        .stamina(stamina)
                        .characterType(CharacterType.valueOf(characterType))
                        .build();

                UserStatResult newUserStatResult = userStatResultRepository.save(userStatResult);

                return StatResponse.CreateStatResponse.builder()
                        .userStatSeq(newUserStat.getUserStatSeq())
                        .strength(strength)
                        .endurance(endurance)
                        .speed(speed)
                        .flexibility(flexibility)
                        .stamina(stamina)
                        .characterType(newUserStatResult.getCharacterType().toString())
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
    public StatResponse.GetStatsResponse GetStatsService(String userId){
        User user=userRepository.findById(userId).orElse(null);
        List<UserStat> userStats=statRepository.findAllByUserOrderByCreatedAtDesc(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<StatResponse.GetStatResponse> getStatResponses=userStats.stream().map(userStat -> {
            UserStatResult userStatResult=userStatResultRepository.findByUserStat(userStat);
            return StatResponse.GetStatResponse.builder()
                    .userStatSeq(userStat.getUserStatSeq())
                    .strength(userStatResult.getStrength())
                    .endurance(userStatResult.getEndurance())
                    .speed(userStatResult.getSpeed())
                    .flexibility(userStatResult.getFlexibility())
                    .stamina(userStatResult.getStamina())
                    .characterType(userStatResult.getCharacterType().toString())
                    .createdAt(userStat.getCreatedAt().toLocalDate().format(formatter)).build();

        }).toList();
        return StatResponse.GetStatsResponse.builder()
                .stats(getStatResponses).build();
    }


    // 시스템 명령어
    private static String getSystemInstruction() {
        return """
                    너의 역할은 게임기반 헬스케어 서비스를 관리하는 시스템이야
                                                       기능은 신체 스펙 데이터와 운동 수행능력 데이터를 받아서 스펙으로 반환해줄거야\s
                                                       데이터 형식은 다음과 같아
                                                       입력 데이터는\s
                                                       {
                                                         "user_id": "12345",
                                                         "gender" : "male",
                                                         "chronic" : "척추 측만증",
                                                         "height": 175,
                                                         "weight": 70,
                                                         "muscle_mass": 35,
                                                         "body_fat": 18,
                                                         "pushups": 40,
                                                         "situps": 50,
                                                         "running_pace": 5.0,
                                                         "running_time": 30,
                                                         "squat": 100,
                                                         "bench_press": 80,
                                                         "deadlift": 120
                                                       }\s
                                                       형식의 JSON 파일이고
                                                       출력형식은
                                                       {
                                                         "user_id": "12345",
                                                         "chronic" : "척추 측만증",
                                                         "strength": 85,
                                                         "endurance": 78,
                                                         "speed": 72,
                                                         "flexibility": 65,
                                                         "stamina": 80,
                                                         "character_type": "power"
                                                       }
                                                       형식의 JSON 파일이야
                                                       
                                                       strength: 스쿼트, 벤치프레스, 데드리프트 무게를 기반으로 계산, 높은 무게를 들수록 높은 점수를 얻습니다.
                                                       
                                                       endurance: 팔굽혀펴기, 윗몸일으키기 횟수를 기반으로 계산 많은 횟수를 할수록 높은 점수를 얻습니다.
                                                       
                                                       speed: 달리기 페이스를 기반으로 계산 페이스가 빠를수록 높은 점수를 얻습니다.
                                                       
                                                       flexibility: (가상의) 유연성 관련 데이터가 없으므로 기본 점수를 할당했습니다. (만약 유연성 측정 데이터가 있다면 해당 데이터를 기반으로 계산합니다.)
                                                       
                                                       stamina: 달리기 시간을 기반으로 계산되었습니다. 오래 달릴수록 높은 점수를 얻습니다. 또한 endurance점수와 speed점수를 합산하여 반영합니다.
                                                       
                                                       character_type: strength, endurance, speed, flexibility, stamina 점수를 종합적으로 고려하여 판단 (높다는 기준은 다른 스탯 평균보다 20%이상 수치를 가질때)
                                                       {
                                                       runner	러닝 페이스 & 유지 시간이 높음
                                                       power    근력이 높음
                                                       diet 	체지방률이 높아 유산소를 주로 수행해야 하는 체형
                                                       balance	전반적인 운동 능력이 균등하게 분포되어있음
                                                       endurance	팔굽혀펴기 & 윗몸일으키기 반복 횟수가 많음
                                                       }
                                                       
                                                       입력 데이터의 성별이 "male"일때 평균은 {
                                                         "user_id": "12345",
                                                         "gender" : "male",
                                                         "chronic" : "",
                                                         "height": 175,
                                                         "weight": 70,
                                                         "muscle_mass": 33,
                                                         "body_fat": 25,
                                                         "pushups": 40,
                                                         "situps": 50,
                                                         "running_pace": 4.0,
                                                         "running_time": 30,
                                                         "squat": 60,
                                                         "bench_press": 60,
                                                         "deadlift": 60
                                                       } 이라고 생각하고 이 경우 스탯을 전부 50으로 해줘
                                                       
                                                       입력 데이터의 성별이 "female"일때 평균은 {
                                                         "user_id": "12345",
                                                         "gender" : "female",
                                                         "chronic" : "척추 측만증",
                                                         "height": 166,
                                                         "weight": 60,
                                                         "muscle_mass": 25,
                                                         "body_fat": 35,
                                                         "pushups": 10,
                                                         "situps": 30,
                                                         "running_pace": 4.0,
                                                         "running_time": 30,
                                                         "squat": 40,
                                                         "bench_press": 40,
                                                         "deadlift": 40
                                                       } 이라고 생각하고 이 경우 스탯을 전부 50으로 해줘
                                                       
                                                       모든 출력은 설명 및 상세 분석이 없이 단순 JSON만 반환해줘
                """;
    }



}
