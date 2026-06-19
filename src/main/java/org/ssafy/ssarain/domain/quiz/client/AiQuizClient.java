package org.ssafy.ssarain.domain.quiz.client;

import java.util.List;

import org.ssafy.ssarain.domain.quiz.dto.GeneratedQuizDto;

public interface AiQuizClient {

    List<GeneratedQuizDto> generateQuizzes(List<String> neuronTitles, int count);
}
