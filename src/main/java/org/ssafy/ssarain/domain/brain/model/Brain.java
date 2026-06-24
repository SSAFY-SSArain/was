package org.ssafy.ssarain.domain.brain.model;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.ssafy.ssarain.common.model.BaseTimeEntity;
import org.ssafy.ssarain.domain.brain.dto.request.BrainUpdateDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "brains")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brain extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid", nullable = false, unique = true)
    private int bid;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Size(max = 200)
    @Column(name = "description", nullable = false, length = 200)
    @ColumnDefault("''")
    private String description = "";
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "join_policy", nullable = false, columnDefinition = "ENUM('PUBLIC', 'PROTECTED')")
    @ColumnDefault("'PROTECTED'")
    private JoinPolicy joinPolicy = JoinPolicy.PROTECTED;
    
    @Column(name = "is_merged", nullable = false)
    @ColumnDefault("false")
    private boolean isMerged = false;
    
    /*
     * 연관된 엔티티
     */
    
    @OneToMany(mappedBy = "brain", fetch = FetchType.LAZY)
    private List<BrainMember> brainMembers;
    
    @Builder(access = AccessLevel.PRIVATE)
    private Brain(String name, String description, JoinPolicy joinPolicy, Boolean isMerged) {
        this.name = name;
        this.description = description;
        this.joinPolicy = joinPolicy;
        if (isMerged != null) {
            this.isMerged = isMerged;
        }
    }
    
    public void update(BrainUpdateDto dto) {
        if (dto.name() != null) {
            this.name = dto.name();
        }
        if (dto.description() != null) {
            this.description = dto.description();
        }
        if (dto.joinPolicy() != null) {
            this.joinPolicy = dto.joinPolicy();
        }
    }
    
    public static Brain of(String name, String description, JoinPolicy joinPolicy) {
        return Brain.builder().name(name).description(description).joinPolicy(joinPolicy).build();
    }
    
    public static Brain mergedOf(String name, String description, JoinPolicy joinPolicy) {
        return Brain.builder().name(name).description(description).joinPolicy(joinPolicy).isMerged(true).build();
    }
}
