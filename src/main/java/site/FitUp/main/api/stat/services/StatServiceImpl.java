package site.FitUp.main.api.stat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${gemini.api.key}")
    private  String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=AIzaSyB3ObgBe9TCNzc7ZBAfK4lF_sDsSwC8NZ8";

    public StatResponse.CreateStatResponse CreateStatService(StatRequest.CreateStatRequest request, String userId) throws Exception {


        // JSON 형식으로 입력 데이터 설정
        JSONObject inputJson = new JSONObject()
                .put("user_id", "12345")
                .put("gender", "male")
                .put("height", 175)
                .put("weight", 70)
                .put("muscle_mass", 35)
                .put("body_fat", 18)
                .put("pushups", 40)
                .put("situps", 50)
                .put("running_pace", 5.0)
                .put("running_time", 30)
                .put("squat", 100)
                .put("bench_press", 80)
                .put("deadlift", 120);

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

                JSONArray candiate= responseJson.getJSONArray("parts");

                if(candiate.length()>0){
                    JSONObject content = candiate.getJSONObject(0).getJSONObject("content");
                    JSONArray parts=content.getJSONArray("parts");
                    if(parts.length()>0){
                        String text = parts.getJSONObject(0).getString("text");

                        String cleanedJson = text.replaceAll("```json\\n|```", "").trim();
                        JSONObject parsedJson = new JSONObject(cleanedJson);
                    }
                    return null;

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



    // 시스템 명령어
    private static String getSystemInstruction() {
        return """
            너의 역할은 게임기반 헬스케어 서비스를 관리하는 시스템이야
            기능은 신체 스펙 데이터와 운동 수행능력 데이터를 받아서 스펙으로 반환해줄거야
            데이터 형식은 다음과 같아

            입력 데이터는
            {
              "user_id": "12345",
              "gender" : "male",
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
            }

            형식의 JSON 파일이고 출력 형식은
            {
              "user_id": "12345",
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
