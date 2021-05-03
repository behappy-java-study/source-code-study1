package org.xiaowu.xiaowu.spring;

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
//        ConfigurableApplicationContext context = SpringApplication.run(XiaowuSpringApplication.class, args);
//        UserService userService = (UserService) context.getBean("userService");
//        userService.test();
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserService) annotationConfigApplicationContext.getBean("userService");
        userService.test();
    }

}
