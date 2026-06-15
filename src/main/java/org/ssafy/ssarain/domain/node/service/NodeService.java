package org.ssafy.ssarain.domain.node.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.common.security.service.BrainAuthService;
import org.ssafy.ssarain.domain.brain.dao.BrainTopicRepository;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.comment.service.CommentService;
import org.ssafy.ssarain.domain.node.dao.NodeRepository;
import org.ssafy.ssarain.domain.node.dto.*;
import org.ssafy.ssarain.domain.node.model.Node;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final UserService          userService;
    private final NodeRepository       nodeRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final CommentService       commentService;


    @Transactional(readOnly = true)
    public NodePreviewListDto getNodePreview(Integer btid) {

        List<Node> nodes = nodeRepository.findByBrainTopic_Btid(btid);
        List<NodePreviewDto> nodePreviewList = nodes.stream()
                                                    .map(NodePreviewDto::from)
                                                    .toList();

        return NodePreviewListDto.from(nodePreviewList);
    }

    @Transactional(readOnly = true)
    public NodeDetailDto getNode(Integer nid) {

        Node node = nodeRepository.findById(nid)
                .orElseThrow(() -> new GlobalException(ErrorCode.NODE_NOT_FOUND));

        List<CommentDetailDto> comments = commentService.getCommentsByNid(nid);

        return NodeDetailDto.from(node, comments);
    }

    @Transactional
    public NodeDetailDto createNode(NodeCreateDto nodeCreateDto, UUID uid) {

        // 권한 검증에서 brainTopic 존재 검증
        BrainTopic brainTopic = brainTopicRepository.getReferenceById(nodeCreateDto.btid());

        User user = userService.getUserByUserId(uid);

        Node node = Node.of(brainTopic, user, nodeCreateDto.title(), nodeCreateDto.content());

        return NodeDetailDto.from(nodeRepository.save(node));
    }

    @Transactional(readOnly = true)
    public List<NodeInfoDto> findByBrainTopicId(Integer brainTopicId) {

        List<Node> nodes = nodeRepository.findByBrainTopic_Btid(brainTopicId);

        return nodes.stream()
                .map(NodeInfoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findTitlesByBrainTopicId(Integer brainTopicId) {

        List<Node> nodes = nodeRepository.findByBrainTopic_Btid(brainTopicId);

        return nodes.stream()
                .map(Node::getTitle)
                .toList();
    }
}
