package com.social.backend.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Size(min = 1, max = 64)
@Constraint(validatedBy = {})
public @interface FirstName {
    // Should not be used. To enable, annotate class
    // with javax.validation.ReportAsSingleViolation
    
    String message() default "invalid first name";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}