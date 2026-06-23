package org.ssafy.ssarain.domain.brain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainWaitingRepository;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberListDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberRoleUpdateDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;
import org.ssafy.ssarain.domain.brain.model.JoinPolicy;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.model.User;

@ExtendWith(MockitoExtension.class)
class BrainMemberServiceTest {

    private static final int BID = 1;

    @Mock
    private BrainRepository brainRepository;

    @Mock
    private BrainMemberRepository brainMemberRepository;

    @Mock
    private BrainWaitingRepository brainWaitingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BrainMemberService brainMemberService;

    @Test
    void admin_권한을_양도하면_대상은_admin이_되고_기존_admin은_manager가_된다() {
        UUID adminUid  = UUID.randomUUID();
        UUID targetUid = UUID.randomUUID();
        BrainMember admin  = brainMember(BrainMemberRole.ADMIN);
        BrainMember target = brainMember(BrainMemberRole.USER);

        when(brainRepository.existsById(BID)).thenReturn(true);
        when(brainMemberRepository.findById(new BrainMember.BrainMemberId(BID, targetUid)))
                .thenReturn(Optional.of(target));
        when(brainMemberRepository.findByBmidBidAndRole(BID, BrainMemberRole.ADMIN))
                .thenReturn(Optional.of(admin));

        brainMemberService.updateMemberRole(
                BID,
                adminUid,
                targetUid,
                new BrainMemberRoleUpdateDto(BrainMemberRole.ADMIN)
        );

        assertThat(target.getRole()).isEqualTo(BrainMemberRole.ADMIN);
        assertThat(admin.getRole()).isEqualTo(BrainMemberRole.MANAGER);
    }

    @Test
    void admin이_탈퇴하면_가장_오래된_manager가_admin이_된다() {
        UUID adminUid = UUID.randomUUID();
        BrainMember admin   = brainMember(BrainMemberRole.ADMIN);
        BrainMember manager = brainMember(BrainMemberRole.MANAGER);

        mockAdminDeletion(adminUid, admin);

        // admin 제외 가장 오래된 manager 찾기
        when(brainMemberRepository.findFirstByBmidBidAndRoleAndBmidUidNotInOrderByCreatedAtAsc(
                BID, BrainMemberRole.MANAGER, List.of(adminUid)))
                .thenReturn(Optional.of(manager));

        brainMemberService.deleteMembers(BID, adminUid, new BrainMemberListDto(List.of(adminUid)));

        assertThat(manager.getRole()).isEqualTo(BrainMemberRole.ADMIN);
        verify(brainRepository, never()).deleteById(BID);
    }

    @Test
    void admin이_탈퇴할_때_manager가_없으면_가장_오래된_user가_admin이_된다() {
        UUID adminUid = UUID.randomUUID();
        BrainMember admin = brainMember(BrainMemberRole.ADMIN);
        BrainMember user = brainMember(BrainMemberRole.USER);

        mockAdminDeletion(adminUid, admin);
        when(brainMemberRepository.findFirstByBmidBidAndRoleAndBmidUidNotInOrderByCreatedAtAsc(
                BID, BrainMemberRole.MANAGER, List.of(adminUid)))
                .thenReturn(Optional.empty());
        when(brainMemberRepository.findFirstByBmidBidAndRoleAndBmidUidNotInOrderByCreatedAtAsc(
                BID, BrainMemberRole.USER, List.of(adminUid)))
                .thenReturn(Optional.of(user));

        brainMemberService.deleteMembers(BID, adminUid, new BrainMemberListDto(List.of(adminUid)));

        assertThat(user.getRole()).isEqualTo(BrainMemberRole.ADMIN);
        verify(brainRepository, never()).deleteById(BID);
    }

    @Test
    void admin이_탈퇴할_때_승계자가_없으면_brain을_삭제한다() {
        UUID adminUid = UUID.randomUUID();
        BrainMember admin = brainMember(BrainMemberRole.ADMIN);

        mockAdminDeletion(adminUid, admin);
        when(brainMemberRepository.findFirstByBmidBidAndRoleAndBmidUidNotInOrderByCreatedAtAsc(
                BID, BrainMemberRole.MANAGER, List.of(adminUid)))
                .thenReturn(Optional.empty());
        when(brainMemberRepository.findFirstByBmidBidAndRoleAndBmidUidNotInOrderByCreatedAtAsc(
                BID, BrainMemberRole.USER, List.of(adminUid)))
                .thenReturn(Optional.empty());

        brainMemberService.deleteMembers(BID, adminUid, new BrainMemberListDto(List.of(adminUid)));

        verify(brainRepository).deleteById(BID);
    }

    private void mockAdminDeletion(UUID adminUid, BrainMember admin) {
        when(brainRepository.existsById(BID)).thenReturn(true);
        when(brainMemberRepository.findAllById(anyList())).thenReturn(List.of(admin));
        when(brainMemberRepository.findRoleByBmidUidAndBmidBid(adminUid, BID))
                .thenReturn(BrainMemberRole.ADMIN);
    }

    private BrainMember brainMember(BrainMemberRole role) {
        Brain brain = Brain.of("brain-" + UUID.randomUUID(), "", JoinPolicy.PROTECTED);
        User user = User.of(UUID.randomUUID() + "@example.com", "user", "password");

        BrainMember brainMember = BrainMember.of(brain, user);
        brainMember.changeRole(role);

        return brainMember;
    }
}
