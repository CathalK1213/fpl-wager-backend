package com.fplwager.backend.service;

import com.fplwager.backend.dto.CreateGroupRequest;
import com.fplwager.backend.dto.GroupResponse;
import com.fplwager.backend.model.Group;
import com.fplwager.backend.model.GroupMember;
import com.fplwager.backend.model.User;
import com.fplwager.backend.repository.GroupMemberRepository;
import com.fplwager.backend.repository.GroupRepository;
import com.fplwager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public GroupResponse createGroup(String username, CreateGroupRequest request) {
        User user = getUser(username);

        String inviteCode = generateUniqueInviteCode();

        Group group = Group.builder()
                .name(request.getName())
                .inviteCode(inviteCode)
                .admin(user)
                .build();

        group = groupRepository.save(group);

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();

        groupMemberRepository.save(member);

        return toGroupResponse(group);
    }

    @Transactional
    public GroupResponse joinGroup(String username, String inviteCode) {
        User user = getUser(username);

        Group group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("You are already a member of this group");
        }

        if (groupMemberRepository.findByGroup(group).size() >= 20) {
            throw new IllegalArgumentException("Group is full (max 20 members)");
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();

        groupMemberRepository.save(member);

        return toGroupResponse(group);
    }

    public List<GroupResponse> getMyGroups(String username) {
        User user = getUser(username);
        return groupMemberRepository.findByUser(user).stream()
                .map(gm -> toGroupResponse(gm.getGroup()))
                .toList();
    }

    public GroupResponse getGroup(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User user = getUser(username);

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        return toGroupResponse(group);
    }

    private GroupResponse toGroupResponse(Group group) {
        List<GroupMember> members = groupMemberRepository.findByGroup(group);

        List<GroupResponse.MemberResponse> memberResponses = members.stream()
                .map(m -> GroupResponse.MemberResponse.builder()
                        .userId(m.getUser().getId())
                        .username(m.getUser().getUsername())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .toList();

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .inviteCode(group.getInviteCode())
                .adminUsername(group.getAdmin().getUsername())
                .members(memberResponses)
                .createdAt(group.getCreatedAt())
                .build();
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (groupRepository.existsByInviteCode(code));
        return code;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}