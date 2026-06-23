package org.ssafy.ssarain.domain.neuron.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.ssafy.ssarain.common.model.BaseTimeEntity;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.user.model.User;

@Getter
@Entity
@Table(name = "neurons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Neuron extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nid", nullable = false)
    private Integer nid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "btid", nullable = false)
    private BrainTopic brainTopic;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Size(max = 100)
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Size(max = 10000)
    @Column(name = "content", nullable = false)
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Neuron(BrainTopic brainTopic, User user, String title, String content) {
        this.brainTopic = brainTopic;
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static Neuron of(BrainTopic brainTopic, User user, String title, String content) {
        return Neuron.builder()
                .brainTopic(brainTopic)
                .user(user)
                .title(title)
                .content(content)
                .build();
    }
}
