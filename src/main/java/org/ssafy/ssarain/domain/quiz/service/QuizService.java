package org.ssafy.ssarain.domain.quiz.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.service.BrainTopicService;
import org.ssafy.ssarain.domain.neuron.service.NeuronService;
import org.ssafy.ssarain.domain.quiz.client.AiQuizClient;
import org.ssafy.ssarain.domain.quiz.dao.QuizRepository;
import org.ssafy.ssarain.domain.quiz.dto.GeneratedQuizDto;
import org.ssafy.ssarain.domain.quiz.dto.GeneratedQuizOptionDto;
import org.ssafy.ssarain.domain.quiz.dto.QuizInfoDto;
import org.ssafy.ssarain.domain.quiz.dto.QuizListRes;
import org.ssafy.ssarain.domain.quiz.model.Quiz;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {

    private static final int MAX_GENERATION_COUNT = 3;
    private static final int QUIZ_GENERATION_COUNT = 10;
    private static final int QUIZ_OPTION_COUNT = 5;

    private final QuizRepository    quizRepository;
    private final BrainTopicService brainTopicService;
    private final NeuronService       neuronService;
    private final AiQuizClient      aiQuizClient;

    public QuizListRes getQuizzes(Integer brainTopicId) {
        validateBrainTopicId(brainTopicId);

        List<QuizInfoDto> quizzes = quizRepository.findByBrainTopicIdOrderByQidAsc(brainTopicId)
                .stream()
                .map(QuizInfoDto::from)
                .toList();

        return QuizListRes.of(quizzes);
    }

    @Transactional
    public QuizListRes generateQuizzes(Integer brainTopicId) {
        validateBrainTopicId(brainTopicId);
        validateGenerationLimit(brainTopicId);

        List<String> neuronTitles = findQuizSourceNeuronTitles(brainTopicId);

        List<GeneratedQuizDto> generatedQuizzes = aiQuizClient.generateQuizzes(neuronTitles, QUIZ_GENERATION_COUNT);
        validateGeneratedQuizzes(generatedQuizzes);

        List<Quiz> quizzes = generatedQuizzes.stream()
                .map(generatedQuiz -> generatedQuiz.toEntity(brainTopicId))
                .toList();

        List<QuizInfoDto> savedQuizzes = quizRepository.saveAll(quizzes)
                .stream()
                .map(QuizInfoDto::from)
                .toList();

        return QuizListRes.of(savedQuizzes);
    }

    /*
       Util Method
    */

    private void validateBrainTopicId(Integer brainTopicId) {
        if (brainTopicId == null) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }
        if (!brainTopicService.existBrainTopic(brainTopicId)) {
            throw new GlobalException(ErrorCode.BRAIN_TOPIC_NOT_FOUND);
        }
    }

    private void validateGenerationLimit(Integer brainTopicId) {
        long savedQuizCount = quizRepository.countByBrainTopicId(brainTopicId);
        long generatedCount = savedQuizCount / QUIZ_GENERATION_COUNT;

        if (generatedCount >= MAX_GENERATION_COUNT) {
            throw new GlobalException(ErrorCode.QUIZ_GENERATION_LIMIT_EXCEEDED);
        }
    }

    private List<String> findQuizSourceNeuronTitles(Integer brainTopicId) {
        List<String> neuronTitles = neuronService.findTitlesByBrainTopicId(brainTopicId);

        if (neuronTitles.isEmpty()) {
            throw new GlobalException(ErrorCode.QUIZ_SOURCE_NEURON_NOT_FOUND);
        }

        return neuronTitles;
    }

    private void validateGeneratedQuizzes(List<GeneratedQuizDto> generatedQuizzes) {
        if (generatedQuizzes == null || generatedQuizzes.size() != QUIZ_GENERATION_COUNT) {
            throw new GlobalException(ErrorCode.GEMINI_INVALID_RESPONSE);
        }

        generatedQuizzes.forEach(this::validateGeneratedQuiz);
    }

    private void validateGeneratedQuiz(GeneratedQuizDto generatedQuiz) {
        if (generatedQuiz == null
                || isBlank(generatedQuiz.question())
                || isBlank(generatedQuiz.explanation())
                || !hasValidOptionCount(generatedQuiz)
                || hasInvalidOption(generatedQuiz)
                || countCorrectOptions(generatedQuiz) != 1) {
            throw new GlobalException(ErrorCode.GEMINI_INVALID_RESPONSE);
        }
    }

    private boolean hasValidOptionCount(GeneratedQuizDto generatedQuiz) {
        return generatedQuiz.options() != null && generatedQuiz.options().size() == QUIZ_OPTION_COUNT;
    }

    private boolean hasInvalidOption(GeneratedQuizDto generatedQuiz) {
        return generatedQuiz.options().stream()
                .anyMatch(this::isInvalidOption);
    }

    private boolean isInvalidOption(GeneratedQuizOptionDto option) {
        return option == null || isBlank(option.option()) || option.isCorrect() == null;
    }

    private long countCorrectOptions(GeneratedQuizDto generatedQuiz) {
        return generatedQuiz.options().stream()
                .filter(option -> Boolean.TRUE.equals(option.isCorrect()))
                .count();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
