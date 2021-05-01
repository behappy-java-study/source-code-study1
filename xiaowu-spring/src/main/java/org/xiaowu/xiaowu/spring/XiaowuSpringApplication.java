package org.xiaowu.xiaowu.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xiaowu.xiaowu.spring.spring.AnnotationConfigApplicationContext;
import org.xiaowu.xiaowu.spring.xiaowu.AppConfig;
import org.xiaowu.xiaowu.spring.xiaowu.service.UserService;

/**
 * 1. AnnotationConfigApplicationContext
 * 2. BeanDefinition
 * 3. BeanNameAware
 * 4. InitializingBean
 * 5. BeanPostProcessor
 */
//@SpringBootApplication
public class XiaowuSpringApplication {

    public static void main(String[] args) throws Exception {
//        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("");
//        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//        SpringApplication.run(SourceCodeStudyApplication.class, args);
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserService) annotationConfigApplicationContext.getBean("userService");
        userService.test();
    }

}
