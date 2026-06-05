package org.ssafy.ssarain.infra.ai.gemini.service;

import java.util.List;

import org.ssafy.ssarain.infra.ai.gemini.dto.GeneratedQuizDto;

public interface GeminiQuizClient {

    List<GeneratedQuizDto> generateQuizzes(List<String> nodeTitles, int count);
}
