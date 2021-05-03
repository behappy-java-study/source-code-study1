package org.xiaowu.xiaowu.spring.spring;

import java.lang.annotation.*;

/**
 * @author 小五
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    Mode value() default Mode.SINGLETON;
}
