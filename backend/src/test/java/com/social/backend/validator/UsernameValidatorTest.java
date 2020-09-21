package com.social.backend.validator;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.social.backend.constraint.Username;
import com.social.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UsernameValidatorTest {

  @Mock
  private UserRepository userRepository;
  private Validator validator;

  @BeforeEach
  public void setUp() {
    GenericApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(UserRepository.class, () -> userRepository);
    context.registerBean(EmailValidator.class);
    context.registerBean(LocalValidatorFactoryBean.class);
    context.refresh();

    validator = context.getBean(Validator.class);
  }

  @Test
  public void whenNullField_expectNoViolations() {
    Assertions
        .assertThat(validator.validate(new Target(null)))
        .isEmpty();
  }

  @Test
  public void whenExistentUsername_expectFieldViolation() {
    Mockito
        .when(userRepository.existsByUsername("johnsmith"))
        .thenReturn(true);

    Assertions
        .assertThat(validator.validate(new Target("johnsmith")))
        .extracting(
            "getPropertyPath.toString",
            "getMessage"
        )
        .containsExactly(new Tuple(
            "field",
            "username already exists"
        ));
  }

  @Test
  public void whenNonexistentUsername_expectNoViolations() {
    Assertions
        .assertThat(validator.validate(new Target("johnsmith")))
        .isEmpty();
  }


  private static class Target {

    @Username
    private final String field;

    private Target(String field) {
      this.field = field;
    }

  }

}
