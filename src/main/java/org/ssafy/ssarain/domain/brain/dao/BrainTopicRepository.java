package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;

public interface BrainTopicRepository extends JpaRepository<BrainTopic, Integer> {

	int countByBidAndTidIn(int bid, List<Integer> tids);
	
	boolean existsByBidAndTid(int bid, int tid);

    @Query("SELECT DISTINCT bt.topic.tid FROM BrainTopic bt WHERE bt.brain.bid IN :bid")
    Set<Integer> findDistinctTidByBidIn(Iterable<Integer> bid);
	
    @EntityGraph(attributePaths = {"brain", "topic"})
    Optional<BrainTopic> findByBidAndTid(int bid, int tid);
    
    @Query(value = """
    		WITH RECURSIVE Descendant AS (
                SELECT btid, tid
                FROM brain_topics
                WHERE bid = :bid AND tid IN :tid 
                
                UNION ALL
                
                SELECT bt.btid, t.tid
                FROM brain_topics bt
                LEFT OUTER JOIN topics t ON t.tid = bt.tid
                INNER JOIN Descendant d ON t.pid = d.tid
                WHERE bt.bid = :bid
            )
            SELECT btid
            FROM Descendant
    		""",
    		nativeQuery = true)
    List<Integer> findDescendantBtidByBidAndTidIn(int bid, List<Integer> tid);
    
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
    
    @Query(value = """
            WITH RECURSIVE Descendant AS (
                SELECT bt.*, 1 AS `depth`
                FROM brain_topics bt
                LEFT OUTER JOIN topics t ON t.tid = bt.tid
                WHERE bid = :bid AND if(isnull(:tid), isnull(t.pid), bt.tid = :tid) 
                
                UNION ALL
                
                SELECT bt.*, (d.depth + 1) AS `depth`
                FROM brain_topics bt
                LEFT OUTER JOIN topics t ON t.tid = bt.tid
                INNER JOIN Descendant d ON t.pid = d.tid
                WHERE bt.bid = :bid AND (d.depth + 1) <= :depth
            )
            SELECT *
            FROM Descendant d
            """, 
            nativeQuery = true)
    List<BrainTopic> findByPidAndBid(int bid, Integer tid, int depth);
}
