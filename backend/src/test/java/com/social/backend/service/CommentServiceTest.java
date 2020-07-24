package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(CommentServiceImpl.class)
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void create_exception_whenPostOfPrivateAuthor_andCommentNotOfPostAuthor() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        User commentAuthor = entityManager.persist(new User()
                .setEmail("commentator@mail.com")
                .setUsername("commentator")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        
        assertThatThrownBy(() -> commentService.create(post, commentAuthor, "body"))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.privatePost"});
    }
    
    @Test
    public void create_exception_whenPostOfInternalAuthor_andCommentNotOfFriend() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.INTERNAL)
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        User commentAuthor = entityManager.persist(new User()
                .setEmail("commentator@mail.com")
                .setUsername("commentator")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        assertThatThrownBy(() -> commentService.create(post, commentAuthor, "body"))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.internalPost"});
    }
    
    @Test
    public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
    
        commentService.create(post, postAuthor, "body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .ignoringFields("post", "author")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void create_whenPostOfInternalAuthor_andCommentOfFriend() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.INTERNAL)
                .setPassword("encoded")
                .setFriends(new ArrayList<>(ImmutableList.of(new User()
                        .setId(2L)
                        .setEmail("commentator@mail.com")
                        .setUsername("commentator")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")))));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        User commentAuthor = entityManager.persist(new User()
                .setEmail("commentator@mail.com")
                .setUsername("commentator")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded")
                .setFriends(new ArrayList<>(ImmutableList.of(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")))));
    
        commentService.create(post, commentAuthor, "body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .ignoringFields("post", "author")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void create_whenPostOfPublicAuthor() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        User commentAuthor = entityManager.persist(new User()
                .setEmail("commentator@mail.com")
                .setUsername("commentator")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        commentService.create(post, commentAuthor, "body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .ignoringFields("post", "author")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void update_exception_whenNoCommentWithIdAndAuthorId() {
        assertThatThrownBy(() -> commentService.update(1L, 2L, "body"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void update() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        entityManager.persist(new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody("comment body")
                .setPost(post)
                .setAuthor(postAuthor));
    
        commentService.update(1L, 1L, "new body");
    
        assertThat(entityManager.find(Comment.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "updated")
                .ignoringFields("post", "author")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("new body"));
    }
    
    @Test
    public void delete_exception_whenNoCommentWithIdAndAuthorId() {
        assertThatThrownBy(() -> commentService.delete(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void delete() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        entityManager.persist(new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody("comment body")
                .setPost(post)
                .setAuthor(postAuthor));
        
        commentService.delete(1L, 1L);
        
        assertThat(entityManager.find(Comment.class, 1L))
                .isNull();
    }
    
    @Test
    public void findAllByPostId_exception_onNullPageable() {
        assertThatThrownBy(() -> commentService.findAllByPostId(1L, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAllByPostId() {
        User postAuthor = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(postAuthor));
        entityManager.persist(new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody("comment body")
                .setPost(post)
                .setAuthor(postAuthor));
    
        assertThat(commentService.findAllByPostId(1L, Pageable.unpaged()))
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithNames(notNullActual(), "created")
                .usingElementComparatorIgnoringFields("post", "author")
                .isEqualTo(ImmutableList.of(new Comment()
                        .setId(1L)
                        .setBody("comment body")));
    }
    
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    private static <T> Comparator<T> notNullActual() {
        return (T actual, T expected) -> (actual != null) ? 0 : 1;
    }
}
