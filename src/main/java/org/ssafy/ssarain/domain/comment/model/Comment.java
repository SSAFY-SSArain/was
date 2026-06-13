package org.ssafy.ssarain.domain.comment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.node.model.Node;
import org.ssafy.ssarain.domain.user.model.User;

import java.util.UUID;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Integer cid;

    @NotNull
    @Column(name = "nid", nullable = false)
    private Integer nid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nid", nullable = false, insertable = false, updatable = false)
    private Node node;

    @Column(name = "pid")
    private Integer pid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", insertable = false, updatable = false)
    private Comment parent;

    @NotNull
    @Column(name = "uid", nullable = false)
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uid", nullable = false, insertable = false, updatable = false)
    private User user;

    @Size(max = 255)
    @Column(name = "content")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(Integer nid, Integer pid, UUID uid, String content) {
        this.nid = nid;
        this.pid = pid;
        this.uid = uid;
        this.content = content;
    }

    public static Comment of(Integer nid, Integer pid, UUID uid, String content) {
        return Comment.builder()
                .nid(nid)
                .pid(pid)
                .uid(uid)
                .content(content)
                .build();
    }

}