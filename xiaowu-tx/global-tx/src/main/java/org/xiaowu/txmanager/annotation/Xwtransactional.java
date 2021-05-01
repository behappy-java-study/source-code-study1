package org.xiaowu.txmanager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注此注解的类意味会向tx-manager中加入/创建一个事务
 * @author 小五
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Xwtransactional {

    /**
     * 代表属于分布式事务
     * @return
     */
    boolean isStart() default false;

    boolean isEnd() default false;
}
