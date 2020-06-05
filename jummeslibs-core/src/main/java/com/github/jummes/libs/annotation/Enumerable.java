package com.github.jummes.libs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.jummes.libs.model.Model;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Enumerable {
    public Class<? extends Model>[] classArray() default {};
}
