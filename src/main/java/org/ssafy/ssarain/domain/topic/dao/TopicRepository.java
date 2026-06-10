package org.ssafy.ssarain.domain.topic.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.topic.model.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    /**
     * JPA 특성상 외래키로 인해 불필요한 JOIN이 발생해서<br>
     * 쿼리를 직접 작성해 JOIN 단계를 제거했습니다.
     * @author 백성수
     * @param pid
     * @return
     */
    @Query("SELECT t FROM Topic t WHERE t.parentTopic.tid = :pid")
    List<Topic> findByPid(int pid);
    
    boolean existsByName(String name);
    
    long countByTidIn(List<Integer> ids);
}
