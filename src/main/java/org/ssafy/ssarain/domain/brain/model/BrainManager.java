package org.ssafy.ssarain.domain.brain.model;

import java.util.UUID;

import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "brain_manager")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrainManager extends BaseAuditingEntity {
    @Id
    private int bid;
    
    @Column(name = "uid", insertable = false, updatable = false)
    private UUID uid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid", nullable = false)
    private Brain brain;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    private User user;
    
    @Builder(access = AccessLevel.PRIVATE)
    private BrainManager(Brain brain, User user) {
        this.brain = brain;
        this.user = user;
    }
    
    public static BrainManager of(Brain brain, User user) {
        return BrainManager.builder().brain(brain).user(user).build();
    }
}
