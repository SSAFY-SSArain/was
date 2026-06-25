package org.ssafy.ssarain.domain.brain.model;

import java.io.Serializable;

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
@Table(name = "merge_brains")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MergeBrain {
    @EmbeddedId
    private MergeBrainId mbid;
    
    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class MergeBrainId implements Serializable {
        private static final long serialVersionUID = -7621926700993799671L;
        
        private int mainid;
        private int memberid;
    }
    
    @MapsId("mainid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainid", nullable = false)
    private Brain mainBrain;

    @MapsId("memberid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", nullable = false)
    private Brain memberBrain;
    
    @Builder(access = AccessLevel.PRIVATE)
    private MergeBrain(Brain mainBrain, Brain memberBrain) {
        this.mbid = new MergeBrainId(mainBrain.getBid(), memberBrain.getBid());
        this.mainBrain = mainBrain;
        this.memberBrain = memberBrain;
    }
    
    public static MergeBrain of(Brain mainBrain, Brain memberBrain) {
        return MergeBrain.builder().mainBrain(mainBrain).memberBrain(memberBrain).build();
    }
}
