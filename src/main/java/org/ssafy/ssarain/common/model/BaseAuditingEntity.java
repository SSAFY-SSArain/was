package org.ssafy.ssarain.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // 상속받는 자식 엔티티에 필드 상속
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변화를 감지하는 리스너 등록
@Getter
public abstract class BaseAuditingEntity {

    @CreatedDate // 처음 생성되어 저장되는 시간 입력
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 변경 될 때마다 최종 수정 시간 갱신
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
