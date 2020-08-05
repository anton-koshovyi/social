package com.social.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepositoryBase;
import com.social.backend.repository.ChatRepositoryGroup;
import com.social.backend.repository.ChatRepositoryPrivate;
import com.social.backend.util.NullableUtil;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

  private final ChatRepositoryBase<Chat> baseRepository;
  private final ChatRepositoryPrivate privateRepository;
  private final ChatRepositoryGroup groupRepository;

  @Autowired
  public ChatServiceImpl(ChatRepositoryBase<Chat> baseRepository,
                         ChatRepositoryPrivate privateRepository,
                         ChatRepositoryGroup groupRepository) {
    this.baseRepository = baseRepository;
    this.privateRepository = privateRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public Chat createPrivate(User user, User target) {
    if (privateRepository.existsByMembersIn(Arrays.asList(user, target))) {
      throw new IllegalActionException(
          "illegalAction.chat.private.alreadyExist", target.getId());
    }

    if (!target.isPublic() && !target.hasFriendship(user)) {
      throw new IllegalActionException(
          "illegalAction.chat.private.createNotFriend", target.getId());
    }

    PrivateChat entity = new PrivateChat();
    entity.setMembers(Sets.newHashSet(user, target));
    return baseRepository.save(entity);
  }

  @Override
  public void deletePrivate(Long id, User member) {
    Chat entity = findPrivateByIdAndMember(id, member);
    baseRepository.delete(entity);
  }

  @Override
  public Chat createGroup(User creator, String name, Set<User> members) {
    for (User member : members) {
      if (!member.isPublic() && !member.hasFriendship(creator)) {
        throw new IllegalActionException(
            "illegalAction.chat.group.addNotFriend", member.getId());
      }
    }

    Set<User> finalMembers = new HashSet<>(members);
    finalMembers.add(creator);

    GroupChat entity = new GroupChat();
    entity.setName(name);
    entity.setOwner(creator);
    entity.setMembers(finalMembers);
    return baseRepository.save(entity);
  }

  @Override
  public Chat updateGroup(Long id, User member, String name) {
    GroupChat entity = findGroupByIdAndMember(id, member);
    NullableUtil.set(entity::setName, name);
    return baseRepository.save(entity);
  }

  @Override
  public Chat updateGroupMembers(Long id, User owner, Set<User> members) {
    GroupChat entity = findGroupByIdAndOwner(id, owner);

    if (!members.contains(owner)) {
      throw new IllegalActionException(
          "illegalAction.chat.group.removeOwner", id, owner.getId());
    }

    Set<User> finalMembers = new HashSet<>();

    for (User member : members) {
      if (entity.hasMember(member)) {
        finalMembers.add(member);
        continue;
      }

      if (!member.isPublic() && !member.hasFriendship(owner)) {
        throw new IllegalActionException(
            "illegalAction.chat.group.addNotFriend", member.getId());
      }

      finalMembers.add(member);
    }

    entity.setMembers(finalMembers);
    return baseRepository.save(entity);
  }

  @Override
  public Chat changeOwner(Long id, User owner, User newOwner) {
    GroupChat entity = findGroupByIdAndOwner(id, owner);

    if (!entity.hasMember(newOwner)) {
      throw new IllegalActionException(
          "illegalAction.chat.group.setOwnerNotMember", id, newOwner.getId());
    }

    entity.setOwner(newOwner);
    return baseRepository.save(entity);
  }

  @Override
  public void leaveGroup(Long id, User member) {
    GroupChat entity = findGroupByIdAndMember(id, member);

    if (entity.isOwner(member)) {
      throw new IllegalActionException(
          "illegalAction.chat.group.leaveOwner", id, member.getId());
    }

    Set<User> finalMembers = new HashSet<>(entity.getMembers());
    finalMembers.remove(member);
    entity.setMembers(finalMembers);
    baseRepository.save(entity);
  }

  @Override
  public void deleteGroup(Long id, User owner) {
    GroupChat entity = findGroupByIdAndOwner(id, owner);
    baseRepository.delete(entity);
  }

  @Override
  public Page<User> getMembers(Long id, User member, Pageable pageable) {
    Set<User> members = this.find(id, member).getMembers();
    return new PageImpl<>(new ArrayList<>(members), pageable, members.size());
  }

  @Override
  public Chat find(Long id, User member) {
    return baseRepository.findByIdAndMembersContaining(id, member)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.byIdAndMember", id, member.getId()));
  }

  @Override
  public Page<Chat> findAll(User member, Pageable pageable) {
    return baseRepository.findAllByMembersContaining(member, pageable);
  }

  private PrivateChat findPrivateByIdAndMember(Long id, User member) {
    return privateRepository.findByIdAndMembersContaining(id, member)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.private.byIdAndMember", id, member.getId()));
  }

  private GroupChat findGroupByIdAndMember(Long id, User member) {
    return groupRepository.findByIdAndMembersContaining(id, member)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.group.byIdAndMember", id, member.getId()));
  }

  private GroupChat findGroupByIdAndOwner(Long id, User owner) {
    return groupRepository.findByIdAndOwner(id, owner)
        .orElseThrow(() -> new NotFoundException(
            "notFound.chat.group.byIdAndOwner", id, owner.getId()));
  }

}
