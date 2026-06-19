package org.ssafy.ssarain.domain.brain.model;

import org.hibernate.annotations.ColumnDefault;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.topic.model.Topic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "brain_topics",
        uniqueConstraints = @UniqueConstraint(name = "uk_brain_topics_bid_tid", columnNames = {"bid", "tid"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrainTopic extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "btid", nullable = false, unique = true)
    private int btid;
    
    @Column(name = "bid", insertable = false, updatable = false)
    private int bid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid", nullable = false)
    private Brain brain;
    
    @Column(name = "tid", insertable = false, updatable = false)
    private int tid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tid", nullable = false)
    private Topic topic;

    @Column(name = "generate_count", nullable = false)
    @ColumnDefault("0")
    private byte generateCount = 0;

    @Builder(access = AccessLevel.PRIVATE)
    private BrainTopic(Brain brain, Topic topic) {
        this.brain = brain;
        this.topic = topic;
    }

    public static BrainTopic of(Brain brain, Topic topic) {
        return BrainTopic.builder().brain(brain).topic(topic).build();
    }
}
