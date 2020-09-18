package com.social.backend.mapper.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.social.backend.dto.post.PostDto;
import com.social.backend.model.post.Post;

@Mapper(uses = UserMapper.class)
public interface PostMapper {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(target = "comments", expression = "java(model.getComments().size())")
  PostDto toDto(Post model);

}
