package com.fplwager.backend.controller;

import com.fplwager.backend.dto.SendWagerMessageRequest;
import com.fplwager.backend.dto.WagerMessageDto;
import com.fplwager.backend.service.WagerChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WagerChatController {

    private final WagerChatService wagerChatService;

    @PostMapping("/api/wager-chat")
    public ResponseEntity<WagerMessageDto> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendWagerMessageRequest request) {
        return ResponseEntity.ok(
                wagerChatService.sendMessage(userDetails.getUsername(), request)
        );
    }

    @GetMapping("/api/wagers/{wagerId}/messages")
    public ResponseEntity<List<WagerMessageDto>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long wagerId) {
        return ResponseEntity.ok(
                wagerChatService.getMessages(userDetails.getUsername(), wagerId)
        );
    }
}