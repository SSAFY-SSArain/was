package org.ssafy.ssarain.infra.ai.gemini.dto;

import java.util.List;

public record GeminiQuizGenerateRes(
        List<GeneratedQuizDto> quizzes
) {
}
