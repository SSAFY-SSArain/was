package org.ssafy.ssarain.domain.neuron.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.ssafy.ssarain.domain.user.model.User;

import jakarta.persistence.Column;
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
@Table(name = "neuron_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NeuronLike {
    @EmbeddedId
    private NeuronLikeId nlid;

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class NeuronLikeId implements Serializable {
        private static final long serialVersionUID = -1358777655500236674L;

        @Column(columnDefinition = "BINARY(16)")
        private UUID uid;

        private int nid;
    }

    @MapsId("uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @MapsId("nid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "nid", nullable = false)
    private Neuron neuron;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private NeuronLike(User user, Neuron neuron) {
        this.nlid = new NeuronLikeId(user.getUid(), neuron.getNid());
        this.user = user;
        this.neuron = neuron;
    }

    public static NeuronLike of(User user, Neuron neuron) {
        return NeuronLike.builder()
                .user(user)
                .neuron(neuron)
                .build();
    }
}
