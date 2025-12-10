package com.classroom.service;

import com.classroom.model.Group;
import com.classroom.model.Membership;
import com.classroom.repository.GroupRepository;
import com.classroom.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public Group createGroup(String name, String description, Long createdBy) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(createdBy);
        group = groupRepository.save(group);

        // Automatically add creator as administrator (TEACHER) of the group
        // Check if membership doesn't already exist (in case of errors)
        if (!membershipRepository.existsByUserIdAndGroupId(createdBy, group.getGroupId())) {
            Membership membership = new Membership();
            membership.setGroupId(group.getGroupId());
            membership.setUserId(createdBy);
            membership.setRole(Membership.Role.TEACHER);
            membershipRepository.save(membership);
        }

        return group;
    }

    public Optional<Group> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    public List<Group> getGroupsByUser(Long userId) {
        List<Membership> memberships = membershipRepository.findByUserId(userId);
        return memberships.stream()
                .map(m -> groupRepository.findById(m.getGroupId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Membership addMember(Long groupId, Long addedByUserId, Long userId, Membership.Role role) {
        // Check if user who adds is TEACHER
        Optional<Membership> adderMembership = membershipRepository.findByUserIdAndGroupId(addedByUserId, groupId);
        if (adderMembership.isEmpty() || adderMembership.get().getRole() != Membership.Role.TEACHER) {
            throw new RuntimeException("Only administrator (TEACHER) can add users to group");
        }

        // Check if user is not already a member of the group
        if (membershipRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new RuntimeException("User is already a member of the group");
        }

        Membership membership = new Membership();
        membership.setGroupId(groupId);
        membership.setUserId(userId);
        membership.setRole(role);
        return membershipRepository.save(membership);
    }

    public List<Membership> getGroupMembers(Long groupId) {
        return membershipRepository.findByGroupId(groupId);
    }

    public boolean isUserMember(Long groupId, Long userId) {
        return membershipRepository.existsByUserIdAndGroupId(userId, groupId);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public Group updateGroup(Long groupId, Long userId, String name, String description) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user is teacher of the group or creator
        Optional<Membership> membership = membershipRepository.findByUserIdAndGroupId(userId, groupId);
        boolean isTeacher = membership.isPresent() && membership.get().getRole() == Membership.Role.TEACHER;
        boolean isCreator = group.getCreatedBy().equals(userId);

        if (!isTeacher && !isCreator) {
            throw new RuntimeException("Only teacher or group creator can edit group");
        }

        if (name != null && !name.trim().isEmpty()) {
            group.setName(name.trim());
        }
        if (description != null) {
            group.setDescription(description.trim());
        }

        return groupRepository.save(group);
    }
}

