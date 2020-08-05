package com.social.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.reply.ContentDto;
import com.social.backend.dto.reply.ContentDto.CreateGroup;
import com.social.backend.dto.reply.ContentDto.UpdateGroup;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.service.CommentService;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;

@RestController
public class CommentController {

  private final CommentService commentService;
  private final PostService postService;
  private final UserService userService;

  @Autowired
  public CommentController(CommentService commentService,
                           PostService postService,
                           UserService userService) {
    this.commentService = commentService;
    this.postService = postService;
    this.userService = userService;
  }

  @GetMapping("/posts/{postId}/comments")
  public Page<Comment> getAll(@PathVariable Long postId,
                              Pageable pageable) {
    Post post = postService.find(postId);
    return commentService.findAll(post, pageable);
  }

  @PostMapping("/posts/{postId}/comments")
  public Comment create(@PathVariable Long postId,
                        @AuthenticationPrincipal(expression = "id") Long userId,
                        @Validated(CreateGroup.class) @RequestBody ContentDto dto) {
    Post post = postService.find(postId);
    User author = userService.find(userId);
    String body = dto.getBody();
    return commentService.create(post, author, body);
  }

  @PatchMapping("/posts/{postId}/comments/{id}")
  public Comment update(@PathVariable Long id,
                        @AuthenticationPrincipal(expression = "id") Long userId,
                        @Validated(UpdateGroup.class) @RequestBody ContentDto dto) {
    User author = userService.find(userId);
    String body = dto.getBody();
    return commentService.update(id, author, body);
  }

  @DeleteMapping("/posts/{postId}/comments/{id}")
  public void delete(@PathVariable Long id,
                     @AuthenticationPrincipal(expression = "id") Long userId) {
    User author = userService.find(userId);
    commentService.delete(id, author);
  }

}
