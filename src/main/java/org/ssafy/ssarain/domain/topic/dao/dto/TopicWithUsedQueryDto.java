package org.ssafy.ssarain.domain.topic.dao.dto;

/**
 * 쿼리 Projection을 위한 DB DTO입니다.
 */
public record TopicWithUsedQueryDto(
        int tid, Integer pid, String name, long using
        ) {
    
    /**
     * DB 스펙 상, using 컬럼을 long 타입으로 반환하므로 변환 로직 필요
     * @return
     */
    public boolean isUsing() {
        return using == 1;
    }
}
