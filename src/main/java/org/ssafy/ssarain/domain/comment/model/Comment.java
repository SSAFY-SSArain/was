package org.ssafy.ssarain.domain.comment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.ssafy.ssarain.domain.neuron.model.Neuron;
import org.ssafy.ssarain.domain.user.model.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Integer cid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "nid", nullable = false)
    private Neuron neuron;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pid")
    private Comment parent;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Size(max = 255)
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(Neuron neuron, Comment parent, User user, String content) {
        this.neuron = neuron;
        this.parent = parent;
        this.user = user;
        this.content = content;
    }

    public static Comment of(Neuron neuron, Comment parent, User user, String content) {
        return Comment.builder()
                .neuron(neuron)
                .parent(parent)
                .user(user)
                .content(content)
                .build();
    }

    public Integer getPid() {
        return parent == null ? null : parent.getCid();
    }


    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.content = "삭제된 댓글입니다.";
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
