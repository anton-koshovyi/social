package com.social.backend.test.model.wrapper.post;

import java.time.ZonedDateTime;

import com.social.backend.model.post.Post;
import com.social.backend.test.model.mutator.PostMutators;
import com.social.backend.test.model.wrapper.AbstractWrapper;

abstract class PostWrapper extends AbstractWrapper<Post> {

  PostWrapper(Long id,
              String title,
              String body) {
    super(new Post());
    super
        .with(PostMutators.id(id))
        .with(PostMutators.createdAt(ZonedDateTime.now()))
        .with(PostMutators.title(title))
        .with(PostMutators.body(body));
  }

}
