package org.xiaowu.txmanager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.xiaowu.txmanager.connection.XwConnection;
import org.xiaowu.txmanager.transactional.XwTransactionManager;

import java.sql.Connection;

/**
 * order 3
 */
@Aspect
@Component
public class XwDataSourceAspect {

    /**
     * 切的是一个接口，所以所有的实现类都会被切到
     * spring会调用这个方法来生成一个本地事务
     * 所以point.proceed()返回的也是一个Connection
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        if (XwTransactionManager.getCurrentXwTransaction() != null) {
            return new XwConnection((Connection) point.proceed(), XwTransactionManager.getCurrentXwTransaction());
        } else {
            return (Connection) point.proceed();
        }
    }
}
