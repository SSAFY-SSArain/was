package org.ssafy.ssarain.domain.quiz.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.quiz.model.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @EntityGraph(attributePaths = "options")
    List<Quiz> findByBrainTopicIdInOrderByQidAsc(List<Integer> brainTopicId);

    long countByBrainTopicId(Integer brainTopicId);
}
