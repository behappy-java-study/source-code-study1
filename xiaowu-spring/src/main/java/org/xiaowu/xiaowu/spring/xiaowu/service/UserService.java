package org.xiaowu.xiaowu.spring.xiaowu.service;

import lombok.Getter;
import lombok.Setter;
import org.xiaowu.xiaowu.spring.spring.*;

/**
 * @author 小五
 * @date 2021/4/18 16:25
 */
@Component
@Scope(Mode.SINGLETON)
public class UserService implements InitializingBean {

    @Autowired
    private OrderService orderService;

    @Setter
    @Getter
    private String name;

    public void test(){
        System.out.println(orderService);
        System.out.println(name);
    }


    @Override
    public void afterPropertiesSet() {
        System.out.println("执行afterPropertiesSet方法");
    }
}
