package org.xiaowu.txserver2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiaowu.txmanager.annotation.Xwtransactional;

/**
 * @author 小五
 */
@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;


    @Xwtransactional(isEnd = true)
    @Transactional
    public void test() {
        demoDao.insert("server2");
    }
}
