package com.social.backend.service;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.exception.WrongCredentialsException;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.model.factory.ModelFactory;
import com.social.backend.test.model.mutator.UserMutators;
import com.social.backend.test.model.type.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private @Mock UserRepository repository;
  private UserService service;

  @BeforeEach
  public void setUp() {
    service = new UserServiceImpl(repository, PasswordEncoderStub.getInstance());
  }

  @Test
  public void create() {
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          User entity = i.getArgument(0);
          UserMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.create(
            "johnsmith@example.com",
            "johnsmith",
            "John",
            "Smith",
            "password"
        ))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel());
  }

  @Test
  public void update_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.update(
            1L,
            "johnsmith@example.com",
            "johnsmith",
            "John",
            "Smith",
            Publicity.PUBLIC
        ))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void update() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(1L))
            .getModel()));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(
            1L,
            "johnsmith@example.com",
            "johnsmith",
            "John",
            "Smith",
            Publicity.PUBLIC
        ))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(1L))
            .with(UserMutators.email("johnsmith@example.com"))
            .with(UserMutators.username("johnsmith"))
            .with(UserMutators.firstName("John"))
            .with(UserMutators.lastName("Smith"))
            .with(UserMutators.publicity(Publicity.PUBLIC))
            .getModel());
  }

  @Test
  public void updateRole_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.updateRole(1L, false))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void updateRole() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.moder(false))
            .getModel()));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateRole(1L, true))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.moder(true))
            .getModel());
  }

  @Test
  public void changePassword_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.changePassword(1L, "actual", "change"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void changePassword_whenWrongActualPassword_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.password("{encoded}actual"))
            .getModel()));

    Assertions
        .assertThatThrownBy(() -> service.changePassword(1L, "wrong", "change"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void changePassword() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.password("{encoded}actual"))
            .getModel()));

    service.changePassword(1L, "actual", "change");

    Mockito
        .verify(repository)
        .save(Mockito.refEq(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.password("{encoded}change"))
            .getModel()));
  }

  @Test
  public void delete_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.delete(1L, "password"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void delete_whenWrongActualPassword_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.password("{encoded}password"))
            .getModel()));

    Assertions
        .assertThatThrownBy(() -> service.delete(1L, "wrong"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void delete() {
    User entity = ModelFactory
        .createWrapper(UserType.JOHN_SMITH)
        .with(UserMutators.id(1L))
        .with(UserMutators.password("{encoded}password"))
        .getModel();
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(entity));

    service.delete(1L, "password");

    Mockito
        .verify(repository)
        .delete(entity);
  }

  @Test
  public void addFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.addHimself"});
  }

  @Test
  public void addFriend_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void addFriend_whenNoTargetEntityWithId_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel()));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenPrivateTargetEntity_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel()));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(2L))
            .with(UserMutators.publicity(Publicity.PRIVATE))
            .getModel()));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.addPrivate"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenFriendAlreadyPresent_expectException() {
    User user = ModelFactory
        .createWrapper(UserType.JOHN_SMITH)
        .with(UserMutators.id(1L))
        .with(UserMutators.publicity(Publicity.PRIVATE))
        .getModel();
    User target = ModelFactory
        .createWrapper(UserType.FRED_BLOGGS)
        .with(UserMutators.id(2L))
        .with(UserMutators.publicity(Publicity.PUBLIC))
        .with(UserMutators.friends(user))
        .getModel();
    UserMutators.friends(target).accept(user);
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(user));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(target));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.addPresent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend() {
    User user = ModelFactory
        .createWrapper(UserType.JOHN_SMITH)
        .with(UserMutators.id(1L))
        .with(UserMutators.publicity(Publicity.PRIVATE))
        .getModel();
    User target = ModelFactory
        .createWrapper(UserType.FRED_BLOGGS)
        .with(UserMutators.id(2L))
        .with(UserMutators.publicity(Publicity.PUBLIC))
        .getModel();
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(user));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(target));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    service.addFriend(1L, 2L);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    Mockito
        .verify(repository, Mockito.times(2))
        .save(captor.capture());
    List<User> captures = captor.getAllValues();
    Assertions
        .assertThat(captures.get(0))
        .describedAs("Should add target to entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.publicity(Publicity.PRIVATE))
            .with(UserMutators.friends(ModelFactory
                .createWrapper(UserType.FRED_BLOGGS)
                .with(UserMutators.id(2L))
                .with(UserMutators.publicity(Publicity.PUBLIC))
                .getModel()))
            .getModel());
    Assertions
        .assertThat(captures.get(1))
        .describedAs("Should add entity to target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(2L))
            .with(UserMutators.publicity(Publicity.PUBLIC))
            .with(UserMutators.friends(ModelFactory
                .createWrapper(UserType.JOHN_SMITH)
                .with(UserMutators.id(1L))
                .with(UserMutators.publicity(Publicity.PRIVATE))
                .getModel()))
            .getModel());
  }

  @Test
  public void removeFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.removeHimself"});
  }

  @Test
  public void removeFriend_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void removeFriend_whenNoTargetWithId_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel()));

    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend_whenNoTargetInFriends_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel()));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(2L))
            .getModel()));

    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.removeAbsent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend() {
    User user = ModelFactory
        .createWrapper(UserType.JOHN_SMITH)
        .with(UserMutators.id(1L))
        .getModel();
    User target = ModelFactory
        .createWrapper(UserType.FRED_BLOGGS)
        .with(UserMutators.id(2L))
        .with(UserMutators.friends(user))
        .getModel();
    UserMutators.friends(target).accept(user);
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(user));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(target));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    service.removeFriend(1L, 2L);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    Mockito
        .verify(repository, Mockito.times(2))
        .save(captor.capture());
    List<User> captures = captor.getAllValues();
    Assertions
        .assertThat(captures.get(0))
        .describedAs("Should remove target from entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel());
    Assertions
        .assertThat(captures.get(1))
        .describedAs("Should remove entity from target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(2L))
            .getModel());
  }

  @Test
  public void getFriends() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .with(UserMutators.friends(ModelFactory
                .createWrapper(UserType.FRED_BLOGGS)
                .with(UserMutators.id(2L))
                .getModel()))
            .getModel()));

    Assertions
        .assertThat(service.getFriends(1L, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(User.class))
        .containsExactly(ModelFactory
            .createWrapper(UserType.FRED_BLOGGS)
            .with(UserMutators.id(2L))
            .getModel());
  }

  @Test
  public void find_byId_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.find(1L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void find_byId() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel()));

    Assertions
        .assertThat(service.find(1L))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel());
  }

  @Test
  public void findAll() {
    Mockito
        .when(repository.findAll(Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createWrapper(UserType.JOHN_SMITH)
                .with(UserMutators.id(1L))
                .getModel())
        ));

    Assertions
        .assertThat(service.findAll(Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(User.class))
        .containsExactly(ModelFactory
            .createWrapper(UserType.JOHN_SMITH)
            .with(UserMutators.id(1L))
            .getModel());
  }

}
