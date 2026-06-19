package org.ssafy.ssarain.domain.quiz.model;

import java.util.ArrayList;
import java.util.List;

import org.ssafy.ssarain.common.model.BaseTimeEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "quizzes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qid", nullable = false, unique = true)
    private int qid;

    @NotNull
    @Column(name = "btid", nullable = false)
    private Integer brainTopicId;

    @Size(max = 500)
    @NotNull
    @Column(name = "question", nullable = false, length = 500)
    private String question;

    @Size(max = 1000)
    @NotNull
    @Column(name = "explanation", nullable = false, length = 1000)
    private String explanation;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "quiz_options", joinColumns = @JoinColumn(name = "qid"))
    private List<QuizOption> options = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Quiz(Integer brainTopicId, String question, String explanation, List<QuizOption> options) {
        this.brainTopicId = brainTopicId;
        this.question = question;
        this.explanation = explanation;
        this.options = new ArrayList<>(options);
    }

    public static Quiz of(Integer brainTopicId, String question, String explanation, List<QuizOption> options) {
        return Quiz.builder()
                .brainTopicId(brainTopicId)
                .question(question)
                .explanation(explanation)
                .options(options)
                .build();
    }
}
