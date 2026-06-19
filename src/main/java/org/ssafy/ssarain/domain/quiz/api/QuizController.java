package org.ssafy.ssarain.domain.quiz.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.domain.quiz.dto.QuizListRes;
import org.ssafy.ssarain.domain.quiz.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizzes")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Q01: BrainTopic 퀴즈 조회")
    public ResponseEntity<BaseResponse<QuizListRes>> getQuizzes(
            @RequestParam(name = "btid") Integer brainTopicId
    ) {

        return BaseResponse.success(SuccessCode.QUIZ_INFO_SUCCESS, quizService.getQuizzes(brainTopicId));
    }

    @PostMapping
    @Operation(summary = "Q02: BrainTopic 퀴즈 생성", description = "btid에 연결된 neuron title을 기반으로 퀴즈 10개를 생성합니다.")
    public ResponseEntity<BaseResponse<QuizListRes>> generateQuizzes(
            @RequestParam(name = "btid") Integer brainTopicId
    ) {

        return BaseResponse.success(SuccessCode.QUIZ_CREATE_SUCCESS, quizService.generateQuizzes(brainTopicId));
    }
}
