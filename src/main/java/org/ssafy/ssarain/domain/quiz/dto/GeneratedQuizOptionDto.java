package org.ssafy.ssarain.domain.quiz.dto;

import org.ssafy.ssarain.domain.quiz.model.QuizOption;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeneratedQuizOptionDto(
        String option,

        @JsonProperty("isCorrect")
        Boolean isCorrect
) {

    public QuizOption toEntity() {
        return QuizOption.of(option, isCorrect);
    }
}
