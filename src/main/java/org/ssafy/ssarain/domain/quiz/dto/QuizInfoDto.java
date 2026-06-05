package org.ssafy.ssarain.domain.quiz.dto;

import java.util.List;

import org.ssafy.ssarain.domain.quiz.model.Quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record QuizInfoDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int qid,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "다음 중 자바(Java)의 특징으로 올바른 것은?")
        String question,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "자바는 가비지 컬렉터(GC)가 메모리를 자동으로 관리해 줍니다.")
        String explanation,

        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<QuizOptionDto> options
) {

    public static QuizInfoDto from(Quiz quiz) {
        return new QuizInfoDto(
                quiz.getQid(),
                quiz.getQuestion(),
                quiz.getExplanation(),
                quiz.getOptions().stream()
                        .map(QuizOptionDto::from)
                        .toList()
        );
    }
}
