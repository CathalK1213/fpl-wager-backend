package com.fplwager.backend.controller;

import com.fplwager.backend.dto.SendMessageRequest;
import com.fplwager.backend.dto.TrashTalkMessageDto;
import com.fplwager.backend.service.TrashTalkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrashTalkController {

    private final TrashTalkService trashTalkService;

    @PostMapping("/api/trash-talk")
    public ResponseEntity<TrashTalkMessageDto> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(trashTalkService.sendMessage(userDetails.getUsername(), request));
    }

    @GetMapping("/api/groups/{groupId}/trash-talk/{gameweek}")
    public ResponseEntity<List<TrashTalkMessageDto>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId,
            @PathVariable Integer gameweek) {
        return ResponseEntity.ok(trashTalkService.getMessages(userDetails.getUsername(), groupId, gameweek));
    }
}