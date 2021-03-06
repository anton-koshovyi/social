package com.social.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;

public interface MessageService {
  
  Message create(Chat chat, User author, String body);
  
  Message update(Long id, User author, String body);
  
  void delete(Long id, User author);
  
  Page<Message> findAll(Chat chat, Pageable pageable);
  
}
