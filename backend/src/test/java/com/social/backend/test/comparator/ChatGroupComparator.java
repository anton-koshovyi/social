package com.social.backend.test.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.User;

class ChatGroupComparator extends ChatComparator<GroupChat> {

  private final Comparator<User> userComparator;

  ChatGroupComparator(Comparator<User> userComparator) {
    super(userComparator);
    this.userComparator = userComparator;
  }

  @Override
  public int compare(GroupChat left, GroupChat right) {
    return new CompareToBuilder()
        .appendSuper(super.compare(left, right))
        .append(left.getName(), right.getName())
        .append(left.getOwner(), right.getOwner(), userComparator)
        .toComparison();
  }

}
