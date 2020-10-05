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

    boolean stringValue() default false;

    String headTexture() default "";

    String displayItem() default "";

    String description() default "";

    boolean recreateTooltip() default false;

    String fromList() default "";

    String fromListMapper() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Optional {

        String defaultValue();

    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Number {

        int minValue() default Integer.MIN_VALUE;

        int maxValue() default Integer.MAX_VALUE;

    }

}
