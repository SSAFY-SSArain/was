package org.ssafy.ssarain.domain.node.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.topic.model.Topic;

import java.time.Instant;

@Getter
@Entity
@Table(
        name = "brain_topic",
        uniqueConstraints = @UniqueConstraint(name = "uk_brain_topic_bid_tid", columnNames = {"bid", "tid"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: 임시엔티티로, 병합 시 삭제 필요
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