package org.ssafy.ssarain.domain.quiz.dto;

import org.ssafy.ssarain.domain.quiz.model.QuizOption;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record QuizOptionDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "자동으로 메모리를 관리해 준다")
        String option,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "true")
        Boolean isCorrect
) {

    public static QuizOptionDto from(QuizOption quizOption) {
        return new QuizOptionDto(quizOption.getOption(), quizOption.getIsCorrect());
    }
}
