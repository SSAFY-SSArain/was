package org.ssafy.ssarain.infra.ai.gemini.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.quiz.client.AiQuizClient;
import org.ssafy.ssarain.domain.quiz.dto.GeneratedQuizDto;
import org.ssafy.ssarain.infra.ai.gemini.dto.GeminiQuizGenerateRes;
import org.ssafy.ssarain.infra.ai.gemini.properties.GeminiProperties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GeminiAiQuizClient implements AiQuizClient {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper       objectMapper;
    private final GeminiProperties   geminiProperties;

    @Override
    public List<GeneratedQuizDto> generateQuizzes(List<String> nodeTitles, int count) {
        try {
            String response = restClientBuilder.build()
                    .post()
                    .uri(buildUri())
                    .header("x-goog-api-key", geminiProperties.getApiKey())
                    .body(buildRequest(nodeTitles, count))
                    .retrieve()
                    .body(String.class);

            String json = extractText(response);
            GeminiQuizGenerateRes quizGenerateRes = objectMapper.readValue(normalizeJson(json), GeminiQuizGenerateRes.class);
            return quizGenerateRes.quizzes();
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.GEMINI_REQUEST_FAILED, e);
        }
    }

    private String buildUri() {
        return UriComponentsBuilder.fromUriString(GEMINI_API_URL)
                .buildAndExpand(geminiProperties.getModel())
                .toUriString();
    }

    private Map<String, Object> buildRequest(List<String> nodeTitles, int count) {
        return Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", buildPrompt(nodeTitles, count)))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "responseMimeType", "application/json"
                )
        );
    }

    private String buildPrompt(List<String> nodeTitles, int count) {
        return """
                다음 학습 노드 제목들을 기반으로 객관식 퀴즈 %d개를 만들어줘.

                조건:
                - 반드시 JSON만 반환해.
                - 최상위 객체는 quizzes 배열만 가져야 해.
                - 각 quiz는 question, explanation, options를 가져야 해.
                - options는 반드시 5개여야 해.
                - options의 각 항목은 option, isCorrect를 가져야 해.
                - 각 문제마다 정답은 반드시 1개만 true여야 해.
                - question, explanation, option은 한국어로 작성해.
                - qid는 넣지 마. 서버가 DB 저장 후 부여해.

                반환 형식:
                {
                  "quizzes": [
                    {
                      "question": "질문",
                      "explanation": "해설",
                      "options": [
                        {"option": "선택지", "isCorrect": true},
                        {"option": "선택지", "isCorrect": false}
                      ]
                    }
                  ]
                }

                학습 노드 제목:
                %s
                """.formatted(count, String.join("\n", nodeTitles));
    }

    private String extractText(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode textNode = root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            throw new GlobalException(ErrorCode.GEMINI_INVALID_RESPONSE);
        }
        return textNode.asText();
    }

    // 잘못된 포멧 응답 정규화
    private String normalizeJson(String text) {
        String json = text.trim();
        if (json.startsWith("```json")) {
            json = json.substring("```json".length()).trim();
        } else if (json.startsWith("```")) {
            json = json.substring("```".length()).trim();
        }
        if (json.endsWith("```")) {
            json = json.substring(0, json.length() - "```".length()).trim();
        }
        return json;
    }
}
