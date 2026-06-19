package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;

public interface BrainTopicRepository extends JpaRepository<BrainTopic, Integer> {

    @EntityGraph(attributePaths = {"brain", "topic"})
    List<BrainTopic> findByBid(int bid);

    @EntityGraph(attributePaths = {"brain", "topic"})
    Optional<BrainTopic> findByBidAndTid(int bid, int tid);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            INSERT INTO brain_topics (bid, tid, generate_count, created_at, updated_at)
            WITH RECURSIVE Ancestors AS (
                SELECT tid, pid
                FROM topics
                WHERE tid IN (:tid)
                
                UNION
                
                SELECT t.tid, t.pid
                FROM topics t
                INNER JOIN Ancestors a ON t.tid = a.pid
            )
            SELECT :bid, a.tid, 0, NOW(6), NOW(6)
            FROM Ancestors a
            WHERE NOT EXISTS (
                SELECT bt.btid
                FROM brain_topics bt
                WHERE bt.tid = a.tid AND bt.bid = :bid
            )
            """, 
        nativeQuery = true)
    int addTopicWithAncestors(int bid, List<Integer> tid);
}
