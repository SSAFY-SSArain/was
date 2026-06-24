package org.ssafy.ssarain.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseTimeEntity {

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private LocalDateTime updatedAt;
}
