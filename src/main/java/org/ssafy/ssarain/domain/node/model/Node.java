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

import java.util.UUID;

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
    @Column(name = "btid", nullable = false)
    private Integer btid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "btid", nullable = false, insertable = false, updatable = false)
    private BrainTopic brainTopic;

    @NotNull
    @Column(name = "uid", nullable = false)
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uid", nullable = false, insertable = false, updatable = false)
    private User user;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Node(Integer btid, UUID uid, String title, String content) {
        this.btid = btid;
        this.uid = uid;
        this.title = title;
        this.content = content;
    }

    public static Node of(Integer btid, UUID uid, String title, String content) {
        return Node.builder()
                .btid(btid)
                .uid(uid)
                .title(title)
                .content(content)
                .build();
    }
}
