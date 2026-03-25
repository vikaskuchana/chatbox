package com.chatgram.app.controller;


import com.chatgram.app.model.Group;
import com.chatgram.app.model.GroupMember;
import com.chatgram.app.security.JwtTokenProvider;
import com.chatgram.app.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final JwtTokenProvider jwtUtil;

    @PostMapping
    public ResponseEntity<Group> createGroup(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> payload) {

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        Group group = groupService.createGroup(
                payload.get("name"),
                payload.get("description"),
                userId
        );

        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long groupId,
            @RequestBody Map<String, Long> payload) {

        String token = authHeader.substring(7);
        Long addedBy = jwtUtil.extractUserId(token);

        groupService.addMember(groupId, payload.get("userId"), addedBy);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<Group>> getMyGroups(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(groupService.getUserGroups(userId));
    }
}