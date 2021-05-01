package org.xiaowu.txserver1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiaowu.txmanager.annotation.Xwtransactional;
import org.xiaowu.txmanager.util.HttpClient;

/**
 * @author 小五
 */
@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    @Xwtransactional(isStart = true)
    @Transactional
    public void test() {
        demoDao.insert("server1");
        System.out.println("server1执行完insert语句,将去调用server2");
        HttpClient.get("http://localhost:8082/server2/test");
        System.out.println("server1执行完调用server2");
        int i = 100/0;
    }
}
