package com.chatgram.app.service;


import com.chatgram.app.model.Group;
import com.chatgram.app.model.GroupMember;
import com.chatgram.app.model.GroupRole;
import com.chatgram.app.repository.GroupMemberRepository;
import com.chatgram.app.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final AuditService auditService;

    @Transactional
    public Group createGroup(String name, String description, Long createdBy) {
        Group group = Group.builder()
                .name(name)
                .description(description)
                .createdBy(createdBy)
                .build();

        group = groupRepository.save(group);

        // Add creator as admin
        GroupMember member = GroupMember.builder()
                .groupId(group.getId())
                .userId(createdBy)
                .role(GroupRole.ADMIN)
                .build();

        groupMemberRepository.save(member);

        auditService.logAction(createdBy, "GROUP_CREATED", "Group", group.getId().toString(), null);

        return group;
    }

    @Transactional
    public void addMember(Long groupId, Long userId, Long addedBy) {
        // Verify admin
        GroupMember admin = groupMemberRepository.findByGroupIdAndUserId(groupId, addedBy)
                .orElseThrow(() -> new RuntimeException("You are not a member"));

        if (admin.getRole() != GroupRole.ADMIN) {
            throw new RuntimeException("Only admins can add members");
        }

        GroupMember newMember = GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role(GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(newMember);

        auditService.logAction(addedBy, "MEMBER_ADDED", "Group", groupId.toString(), null);
    }

    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }

    public List<Group> getUserGroups(Long userId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);
        return memberships.stream()
                .map(m -> groupRepository.findById(m.getGroupId()).orElse(null))
                .filter(g -> g != null)
                .toList();
    }
}