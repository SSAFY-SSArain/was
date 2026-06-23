package org.ssafy.ssarain.domain.topic.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.ssafy.ssarain.domain.topic.dao.dto.TopicPathQueryDto;
import org.ssafy.ssarain.domain.topic.dao.dto.TopicWithUsedQueryDto;
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
    
    /**
     * 효율적인 검색을 위해 MySQL 기반 쿼리 최적화를 적용했습니다.
     * @author 백성수
     * @param bid
     * @return
     */
    @Query(value = """    
            SELECT tid, pid, name,
                EXISTS (SELECT * FROM brain_topics WHERE bid = :bid AND tid = t.tid) AS `using`
            FROM topics t
            """,
            nativeQuery = true)
    List<TopicWithUsedQueryDto> findWithUsingByBid(Integer bid);
    
    /**
     * 효율적인 검색을 위해 MySQL 기반 쿼리 최적화를 적용했습니다.
     * @author 백성수
     * @param bid
     * @return
     */
    @Query(value = """
            WITH RECURSIVE Descendant AS (
                SELECT tid, pid, name, 1 AS `depth`
                FROM topics
                WHERE pid = :pid
                
                UNION ALL
                
                SELECT t.tid, t.pid, t.name, (d.depth + 1)
                FROM topics t
                INNER JOIN Descendant d ON t.pid = d.tid
                WHERE (d.depth + 1) <= :maxDepth
            )
            SELECT tid, pid, name,
                EXISTS (SELECT 1 FROM brain_topics WHERE bid = :bid AND tid = d.tid) AS `using`
            FROM Descendant d
            """, 
            nativeQuery = true)
    List<TopicWithUsedQueryDto> findWithUsingByPidAndBid(int pid, Integer bid, int maxDepth);

    @Query(value = """
            WITH RECURSIVE Ancestor AS (
                SELECT t.tid AS target_tid, t.tid, t.pid, t.name, 0 AS `depth`
                FROM topics t
                WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))

                UNION ALL

                SELECT a.target_tid, p.tid, p.pid, p.name, (a.depth + 1)
                FROM topics p
                INNER JOIN Ancestor a ON a.pid = p.tid
            )
            SELECT target_tid AS targetTid, tid, pid, name,
                CASE WHEN EXISTS (SELECT 1 FROM brain_topics WHERE bid = :bid AND tid = a.tid) THEN 1 ELSE 0 END AS `using`,
                `depth`
            FROM Ancestor a
            ORDER BY target_tid ASC, `depth` DESC
            """,
            nativeQuery = true)
    List<TopicPathQueryDto> findPathsByNameContaining(@Param("name") String name, @Param("bid") Integer bid);
    
    boolean existsByName(String name);
    
    long countByTidIn(List<Integer> ids);
}
