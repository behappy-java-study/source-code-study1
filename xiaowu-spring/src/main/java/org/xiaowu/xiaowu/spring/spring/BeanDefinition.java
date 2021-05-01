package org.xiaowu.xiaowu.spring.spring;

import lombok.Getter;
import lombok.Setter;

/**
 * 同spring的BeanDefinition
 */
@Getter
@Setter
public class BeanDefinition {

    private Class clazz;

    private Mode scope;

}
