package org.ssafy.ssarain.domain.node.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.user.model.User;

@Getter
@Entity
@Table(name = "node")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Node extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nid", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "btid", nullable = false)
    private BrainTopic brainTopic;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Node(BrainTopic brainTopic, User user, String title, String content) {
        this.brainTopic = brainTopic;
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static Node of(BrainTopic brainTopic, User user, String title, String content) {
        return Node.builder()
                .brainTopic(brainTopic)
                .user(user)
                .title(title)
                .content(content)
                .build();
    }
}
