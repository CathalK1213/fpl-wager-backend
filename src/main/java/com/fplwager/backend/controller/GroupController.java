package com.fplwager.backend.controller;

import com.fplwager.backend.dto.CreateGroupRequest;
import com.fplwager.backend.dto.GroupResponse;
import com.fplwager.backend.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(userDetails.getUsername(), request));
    }

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<GroupResponse> joinGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String inviteCode) {
        return ResponseEntity.ok(groupService.joinGroup(userDetails.getUsername(), inviteCode));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(groupService.getMyGroups(userDetails.getUsername()));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroup(groupId, userDetails.getUsername()));
    }
}