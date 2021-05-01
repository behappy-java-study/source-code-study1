package org.xiaowu.txmanager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.xiaowu.txmanager.annotation.Xwtransactional;
import org.xiaowu.txmanager.transactional.XwTransaction;
import org.xiaowu.txmanager.transactional.XwTransactionManager;
import org.xiaowu.txmanager.transactional.TransactionType;

import java.lang.reflect.Method;

/**
 * order 2
 * 切Xwtransactional注解
 * Ordered:和spring的@Transaction有个先后顺序,先执行自己的
 */
@Aspect
@Component
public class XwTransactionAspect implements Ordered {


    @Around("@annotation(org.xiaowu.txmanager.annotation.Xwtransactional)")
    public void invoke(ProceedingJoinPoint point) {
        // 打印出这个注解所对应的方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Xwtransactional lbAnnotation = method.getAnnotation(Xwtransactional.class);
        String groupId = "";
        if (lbAnnotation.isStart()) {
//            1. 创建一个全局事务
            groupId = XwTransactionManager.createXwTransactionGroup();
        } else {
//            2. 获取当前事务组
            groupId = XwTransactionManager.getCurrentGroupId();
        }
//            3. 加入当前事务组
        XwTransaction xwTransaction = XwTransactionManager.createXwTransaction(groupId);

        try {
            // 通过判断point.proceed()是否有异常来判断是commit还是rollback
            // spring会开启事务,和XwDataSourceAspect紧密联系,因为随后会走XwDataSourceAspect@Arround获取Connetction
            point.proceed();
            XwTransactionManager.addXwTransaction(xwTransaction, lbAnnotation.isEnd(), TransactionType.commit);
        } catch (Exception e) {
            XwTransactionManager.addXwTransaction(xwTransaction, lbAnnotation.isEnd(), TransactionType.rollback);
            e.printStackTrace();
        } catch (Throwable throwable) {
            XwTransactionManager.addXwTransaction(xwTransaction, lbAnnotation.isEnd(), TransactionType.rollback);
            throwable.printStackTrace();
        }
    }


    @Override
    public int getOrder() {
        return 10000;
    }
}
