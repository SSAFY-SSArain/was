package org.ssafy.ssarain.domain.brain.model;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "brain_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrainMember extends BaseAuditingEntity {
    @EmbeddedId
    private BrainMemberId bmid;
    
    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BrainMemberId implements Serializable {
        private static final long serialVersionUID = -7631747102627678704L;
        
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
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    @Column(name = "role", nullable = false, length = 20)
    private BrainMemberRole role = BrainMemberRole.USER;
    
    @Builder(access = AccessLevel.PRIVATE)
    private BrainMember(Brain brain, User user, BrainMemberRole role) {
        this.bmid = new BrainMemberId(brain.getBid(), user.getUid());
        this.brain = brain;
        this.user = user;
        this.role = role;
    }
    
    public static BrainMember of(Brain brain, User user) {
        return BrainMember.builder().brain(brain).user(user).build();
    }
    
    public static BrainMember adminOf(Brain brain, User user) {
        return BrainMember.builder().brain(brain).user(user).role(BrainMemberRole.ADMIN).build();
    }
}
