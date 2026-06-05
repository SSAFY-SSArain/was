package org.ssafy.ssarain.domain.quiz.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record QuizListRes(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<QuizInfoDto> quizzes
) {

    public static QuizListRes of(List<QuizInfoDto> quizzes) {
        return new QuizListRes(quizzes);
    }
}
