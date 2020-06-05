package com.github.jummes.libs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field that is modifyable throught a GUI
 *
 * @author Marco
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serializable {

    public boolean stringValue() default false;

    public String headTexture() default "";

    public String displayItem() default "";

    public String description() default "";

    public String fromList() default "";

    public String fromListMapper() default "";
}
