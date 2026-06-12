package org.ssafy.ssarain.domain.node.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.domain.node.dto.NodeInfoDto;

@Service
public class NodeService {

    public List<NodeInfoDto> findByBrainTopicId(Integer brainTopicId) {
        // TODO: Node 도메인 개발 후 실제 로직 작성할 것
        return List.of();
    }

    public List<String> findTitlesByBrainTopicId(Integer brainTopicId) {
        return List.of(
                "자바 객체지향 프로그래밍의 캡슐화와 상속",
                "JVM 메모리 구조와 가비지 컬렉션",
                "MySQL 인덱스가 조회 성능에 미치는 영향",
                "동적 계획법의 점화식 설계 방법",
                "그리디 알고리즘의 최적 부분 구조 판단",
                "Spring Security JWT 인증 흐름",
                "Redis를 활용한 Refresh Token 관리",
                "JPA 연관관계와 지연 로딩",
                "트랜잭션 격리 수준과 동시성 문제",
                "REST API 응답 형식 설계"
        );
    }
}
