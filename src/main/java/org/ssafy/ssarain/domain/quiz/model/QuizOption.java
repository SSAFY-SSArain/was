package org.ssafy.ssarain.domain.quiz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizOption {

    @Size(max = 255)
    @NotNull
    @Column(name = "option_content", nullable = false)
    private String option;

    @NotNull
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Builder(access = AccessLevel.PRIVATE)
    private QuizOption(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public static QuizOption of(String option, Boolean isCorrect) {
        return QuizOption.builder()
                .option(option)
                .isCorrect(isCorrect)
                .build();
    }
}
