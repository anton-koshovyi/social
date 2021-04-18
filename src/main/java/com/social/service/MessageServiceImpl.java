package com.social.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.exception.NotFoundException;
import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;
import com.social.repository.MessageRepository;
import com.social.util.NullableUtil;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

  private final MessageRepository repository;

  @Autowired
  public MessageServiceImpl(MessageRepository repository) {
    this.repository = repository;
  }

  @Override
  public Message create(Chat chat, User author, String body) {
    Message entity = new Message();
    entity.setBody(body);
    entity.setChat(chat);
    entity.setAuthor(author);
    return repository.save(entity);
  }

  @Override
  public Message update(Long id, User author, String body) {
    Message entity = findByIdAndAuthor(id, author);
    entity.setUpdatedAt(ZonedDateTime.now());
    NullableUtil.set(entity::setBody, body);
    return repository.save(entity);
  }

  @Override
  public void delete(Long id, User author) {
    Message entity = findByIdAndAuthor(id, author);
    repository.delete(entity);
  }

  @Override
  public Page<Message> findAll(Chat chat, Pageable pageable) {
    return repository.findAllByChat(chat, pageable);
  }

  private Message findByIdAndAuthor(Long id, User author) {
    return repository.findByIdAndAuthor(id, author)
        .orElseThrow(() -> new NotFoundException(
            "notFound.message.byIdAndAuthor", id, author.getId()));
  }

}