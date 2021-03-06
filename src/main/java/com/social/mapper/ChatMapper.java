package com.social.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.social.dto.chat.ChatDto;
import com.social.dto.chat.GroupChatDto;
import com.social.dto.chat.PrivateChatDto;
import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.PrivateChat;

@Mapper(uses = UserMapper.class)
public interface ChatMapper {

  ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

  @Mapping(target = "type", constant = "private")
  PrivateChatDto toDto(PrivateChat model);

  @Mapping(target = "type", constant = "group")
  @Mapping(target = "members", expression = "java(model.getMembers().size())")
  GroupChatDto toDto(GroupChat model);

  default ChatDto toDto(Chat model) {
    if (model instanceof PrivateChat) {
      return this.toDto((PrivateChat) model);
    }

    if (model instanceof GroupChat) {
      return this.toDto((GroupChat) model);
    }

    return null;
  }

}
