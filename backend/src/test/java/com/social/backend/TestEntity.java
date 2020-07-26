package com.social.backend;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@SuppressWarnings("checkstyle:DeclarationOrder")
public final class TestEntity {
    public static User user() {
        User user = new User();
        user.setEmail("email@mail.com");
        user.setUsername("username");
        user.setFirstName("first");
        user.setLastName("last");
        user.setPassword("encoded");
        return user;
    }
    
    public static Post post() {
        Post post = new Post();
        post.setBody("post body");
        return post;
    }
    
    public static Comment comment() {
        Comment comment = new Comment();
        comment.setBody("comment body");
        return comment;
    }
    
    public static PrivateChat privateChat() {
        return new PrivateChat();
    }
    
    public static GroupChat groupChat() {
        GroupChat groupChat = new GroupChat();
        groupChat.setName("name");
        return groupChat;
    }
    
    private TestEntity() {}
}
