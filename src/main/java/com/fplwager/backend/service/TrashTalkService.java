package com.fplwager.backend.service;

import com.fplwager.backend.dto.SendMessageRequest;
import com.fplwager.backend.dto.TrashTalkMessageDto;
import com.fplwager.backend.model.*;
import com.fplwager.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrashTalkService {

    private final TrashTalkRepository trashTalkRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TrashTalkMessageDto sendMessage(String username, SendMessageRequest request) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, sender)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        TrashTalkMessage message = TrashTalkMessage.builder()
                .group(group)
                .sender(sender)
                .gameweek(request.getGameweek())
                .content(request.getContent())
                .gifUrl(request.getGifUrl())
                .messageType(request.getMessageType())
                .build();

        message = trashTalkRepository.save(message);
        TrashTalkMessageDto dto = toDto(message);

        messagingTemplate.convertAndSend(
                "/topic/group/" + request.getGroupId() + "/gameweek/" + request.getGameweek(),
                dto
        );

        return dto;
    }

    public List<TrashTalkMessageDto> getMessages(String username, Long groupId, Integer gameweek) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        return trashTalkRepository.findByGroupAndGameweekOrderBySentAtAsc(group, gameweek)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TrashTalkMessageDto toDto(TrashTalkMessage message) {
        return TrashTalkMessageDto.builder()
                .id(message.getId())
                .senderUsername(message.getSender().getUsername())
                .groupId(message.getGroup().getId())
                .gameweek(message.getGameweek())
                .content(message.getContent())
                .gifUrl(message.getGifUrl())
                .messageType(message.getMessageType())
                .sentAt(message.getSentAt())
                .build();
    }
}