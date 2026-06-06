package org.ssafy.ssarain.infra.ai.gemini.dto;

import java.util.List;

import org.ssafy.ssarain.domain.quiz.dto.GeneratedQuizDto;

public record GeminiQuizGenerateRes(
        List<GeneratedQuizDto> quizzes
) {
}
