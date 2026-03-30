package com.fplwager.backend.service;

import com.fplwager.backend.dto.SendWagerMessageRequest;
import com.fplwager.backend.dto.WagerMessageDto;
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
public class WagerChatService {

    private final WagerMessageRepository wagerMessageRepository;
    private final WagerRepository wagerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public WagerMessageDto sendMessage(String username, SendWagerMessageRequest request) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wager wager = wagerRepository.findById(request.getWagerId())
                .orElseThrow(() -> new IllegalArgumentException("Wager not found"));

        if (!isParticipant(wager, sender)) {
            throw new IllegalArgumentException("You are not a participant in this wager");
        }

        WagerMessage message = WagerMessage.builder()
                .wager(wager)
                .sender(sender)
                .content(request.getContent())
                .gifUrl(request.getGifUrl())
                .videoUrl(request.getVideoUrl())
                .messageType(request.getMessageType())
                .build();

        message = wagerMessageRepository.save(message);
        WagerMessageDto dto = toDto(message);

        messagingTemplate.convertAndSend(
                "/topic/wager/" + request.getWagerId() + "/chat",
                dto
        );

        return dto;
    }

    public List<WagerMessageDto> getMessages(String username, Long wagerId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wager wager = wagerRepository.findById(wagerId)
                .orElseThrow(() -> new IllegalArgumentException("Wager not found"));

        if (!isParticipant(wager, user)) {
            throw new IllegalArgumentException("You are not a participant in this wager");
        }

        return wagerMessageRepository.findByWagerOrderBySentAtAsc(wager)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private boolean isParticipant(Wager wager, User user) {
        return wager.getProposer().getId().equals(user.getId())
                || wager.getOpponent().getId().equals(user.getId());
    }

    private WagerMessageDto toDto(WagerMessage message) {
        return WagerMessageDto.builder()
                .id(message.getId())
                .wagerId(message.getWager().getId())
                .senderUsername(message.getSender().getUsername())
                .content(message.getContent())
                .gifUrl(message.getGifUrl())
                .videoUrl(message.getVideoUrl())
                .messageType(message.getMessageType())
                .sentAt(message.getSentAt())
                .build();
    }
}