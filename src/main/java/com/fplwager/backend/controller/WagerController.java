package com.fplwager.backend.controller;

import com.fplwager.backend.dto.ProposeWagerRequest;
import com.fplwager.backend.dto.WagerResponse;
import com.fplwager.backend.service.WagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wagers")
@RequiredArgsConstructor
public class WagerController {

    private final WagerService wagerService;

    @PostMapping
    public ResponseEntity<WagerResponse> proposeWager(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProposeWagerRequest request) {
        return ResponseEntity.ok(wagerService.proposeWager(userDetails.getUsername(), request));
    }

    @PostMapping("/{wagerId}/respond")
    public ResponseEntity<WagerResponse> respondToWager(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long wagerId,
            @RequestParam String action) {
        return ResponseEntity.ok(wagerService.respondToWager(userDetails.getUsername(), wagerId, action));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<WagerResponse>> getGroupWagers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(wagerService.getGroupWagers(userDetails.getUsername(), groupId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<WagerResponse>> getMyWagers(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wagerService.getMyWagers(userDetails.getUsername()));
    }
}