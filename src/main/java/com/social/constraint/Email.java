package com.social.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

import com.social.validator.EmailValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Size(min = 1, max = 320)
@javax.validation.constraints.Email
@Constraint(validatedBy = EmailValidator.class)
public @interface Email {

  String message() default "email already exists";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
