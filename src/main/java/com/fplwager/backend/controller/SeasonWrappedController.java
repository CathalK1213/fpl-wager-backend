package com.fplwager.backend.controller;

import com.fplwager.backend.dto.SeasonWrappedResponse;
import com.fplwager.backend.service.SeasonWrappedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class SeasonWrappedController {

    private final SeasonWrappedService seasonWrappedService;

    @GetMapping("/{groupId}/wrapped")
    public ResponseEntity<SeasonWrappedResponse> getWrapped(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                seasonWrappedService.getWrapped(userDetails.getUsername(), groupId));
    }
}