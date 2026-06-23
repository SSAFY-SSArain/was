package org.ssafy.ssarain.domain.topic.model;

import org.ssafy.ssarain.common.model.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "topics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tid", nullable = false, unique = true)
    private int tid;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", nullable = true)
    private Topic parentTopic;
    
    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    public Integer getPid() {
        if (parentTopic == null)
            return null;
        return parentTopic.getTid();
    }
    
    @Builder(access = AccessLevel.PRIVATE)
    private Topic(Topic parentTopic, String name) {
        this.parentTopic = parentTopic;
        this.name = name;
    }
    
    public static Topic of(Topic parentTopic, String name) {
        return Topic.builder().parentTopic(parentTopic).name(name).build();
    }
}
