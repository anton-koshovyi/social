package com.social.backend.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.reply.ContentDto;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.MessageService;
import com.social.backend.service.UserService;

@RestController
public class MessageController {
    private final MessageService messageService;
    private final ChatService chatService;
    private final UserService userService;
    
    @Autowired
    public MessageController(MessageService messageService,
                             ChatService chatService,
                             UserService userService) {
        this.messageService = messageService;
        this.chatService = chatService;
        this.userService = userService;
    }
    
    @GetMapping("/chats/{chatId}/messages")
    public Page<Message> chatAll(@PathVariable Long chatId,
                                 Pageable pageable) {
        return messageService.findAllByChatId(chatId, pageable);
    }
    
    @PostMapping("/chats/{chatId}/messages")
    public Message created(@PathVariable Long chatId,
                           @AuthenticationPrincipal(expression = "id") Long userId,
                           @Valid @RequestBody ContentDto dto) {
        User author = userService.findById(userId);
        Chat chat = chatService.findByIdAndUser(chatId, author);
        String body = dto.getBody();
        return messageService.create(chat, author, body);
    }
    
    @PutMapping("/chats/{chatId}/messages/{id}")
    public Message updated(@PathVariable Long id,
                           @AuthenticationPrincipal(expression = "id") Long userId,
                           @Valid @RequestBody ContentDto dto) {
        String body = dto.getBody();
        return messageService.update(id, userId, body);
    }
    
    @DeleteMapping("/chats/{chatId}/messages/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal(expression = "id") Long userId) {
        messageService.delete(id, userId);
    }
}