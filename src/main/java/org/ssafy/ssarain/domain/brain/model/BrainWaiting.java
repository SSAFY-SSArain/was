package org.ssafy.ssarain.domain.brain.model;

import java.io.Serializable;
import java.util.UUID;

import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.user.model.User;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "brain_waiting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrainWaiting extends BaseAuditingEntity {
    @EmbeddedId
    private BrainWaitingId bmid;
    
    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BrainWaitingId implements Serializable {
        private static final long serialVersionUID = -2670386917619770837L;
        
        private int bid;
        private UUID uid;
    }
    
    @MapsId("bid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid", nullable = false)
    private Brain brain;

    @MapsId("uid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;
    
    @Builder(access = AccessLevel.PRIVATE)
    private BrainWaiting(Brain brain, User user) {
        this.bmid = new BrainWaitingId(brain.getBid(), user.getUid());
        this.brain = brain;
        this.user = user;
    }
    
    public static BrainWaiting of(Brain brain, User user) {
        return BrainWaiting.builder().brain(brain).user(user).build();
    }
}
