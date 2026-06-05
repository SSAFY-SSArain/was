package org.ssafy.ssarain.infra.ai.gemini.dto;

import java.util.List;

import org.ssafy.ssarain.domain.quiz.model.Quiz;

public record GeneratedQuizDto(
        String question,
        String explanation,
        List<GeneratedQuizOptionDto> options
) {

    public Quiz toEntity(Integer brainTopicId) {
        return Quiz.of(
                brainTopicId,
                question,
                explanation,
                options.stream()
                        .map(GeneratedQuizOptionDto::toEntity)
                        .toList()
        );
    }
}
