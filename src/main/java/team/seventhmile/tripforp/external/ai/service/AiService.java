package team.seventhmile.tripforp.external.ai.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import team.seventhmile.tripforp.global.common.GeminiRateLimitInterceptor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;
    private final GeminiRateLimitInterceptor geminiRateLimitInterceptor;
    @Value("${gemini.api.key}")
    private String key;
    @Value("${gemini.api.url}")
    private String url;

    public String generateContent(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", key);

        prompt = generatePromptV1(prompt);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
            );

            return parseGeminiResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    private String generatePromptV1(String prompt) {
        return "주어진 장소들의 위치와 특성을 고려하여 효율적이고 합리적인 여행 코스를 만들어주세요. 각 장소의 정보는 [id, 장소명, 도로명주소, 위도, 경도, 카테고리] 형식으로 제공됩니다:\n"
            + prompt + "\n"
            + "다음 조건을 고려해서 최적의 코스를 추천해주세요:\n"
            + "\n"
            + "1. 장소 간의 거리를 우선적으로 고려해야 함\n"
            + "2. 대신 적절한 카테고리의 배치가 필요함. 특히 같은 카테고리의 연속은 되도록 피해야 함.\n"
            + "3. 카테고리가 알 수 없음인 경우 장소명을 통해 유추해야 함.\n"
            + "4. 하루 일정 기준으로 개인 선호도는 특별히 고려하지 않고 트렌디한 코스로 제안해야함.\n"
            + "5. 다른 지역에 있어도 제외시키지 않아야 함.\n"
            + "6. 코스를 id값만 쉼표로 구분하여 응답해야함.\n"
            + "7. 다른 설명은 포함하면 안됨.";
    }

    private String generatePromptV2(String prompt) {
        return "주어진 장소들의 위치와 특성을 고려하여 효율적이고 합리적인 여행 코스를 만들어주세요. 각 장소의 정보는 [id, 장소명, 도로명주소, 카테고리] 형식으로 제공됩니다:\n"
            + prompt + "\n"
            + "다음 조건을 고려해서 최적의 코스를 추천해주세요:\n"
            + "\n"
            + "1. 장소 간의 거리를 우선적으로 고려해야 함\n"
            + "2. 대신 적절한 카테고리의 배치가 필요함. 특히 같은 카테고리의 연속은 되도록 피해야 함.\n"
            + "3. 카테고리가 알 수 없음인 경우 장소명을 통해 유추해야 함.\n"
            + "4. 하루 일정 기준으로 개인 선호도는 특별히 고려하지 않고 트렌디한 코스로 제안해야함.\n"
            + "5. 코스를 id값만 쉼표로 구분하여 응답해야함.\n"
            + "6. 다른 설명은 포함하면 안됨.";
    }

    private String parseGeminiResponse(Map<String, Object> responseBody) {
        // Gemini API 응답 구조에 맞게 파싱
        try {
            String jsonString = new JSONObject(responseBody).toString();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);

            JSONArray candidates = (JSONArray) json.get("candidates");
            JSONObject firstCandidate = (JSONObject) candidates.get(0);

            JSONObject content = (JSONObject) firstCandidate.get("content");

            JSONArray parts = (JSONArray) content.get("parts");
            JSONObject firstPart = (JSONObject) parts.get(0);

            return (String) firstPart.get("text");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }

    public long getChance(UserDetails userDetails) {
        return geminiRateLimitInterceptor.getChance(userDetails);
    }

}
