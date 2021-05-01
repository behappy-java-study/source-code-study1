package org.xiaowu.txmanager.transactional;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaowu.txmanager.netty.NettyClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class XwTransactionManager {


    private static NettyClient nettyClient;

    private static ThreadLocal<XwTransaction> currentXwTransaction = new ThreadLocal<>();
    private static ThreadLocal<String> currentGroupId = new ThreadLocal<>();
    private static ThreadLocal<Integer> transactionCount = new ThreadLocal<>();

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        XwTransactionManager.nettyClient = nettyClient;
    }

//    <groupId,XwTransaction>
    public static Map<String, XwTransaction> XW_TRANSACTION_MAP = new HashMap<>();

    /**
     * 创建事务组，并且返回groupId
     * @return
     */
    public static String createXwTransactionGroup() {
        String groupId = UUID.randomUUID().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", groupId);
        jsonObject.put("command", "create");
        nettyClient.send(jsonObject);
        System.out.println("创建事务组");

        currentGroupId.set(groupId);
        return groupId;
    }

    /**
     * 创建分布式事务
     * @param groupId
     * @return
     */
    public static XwTransaction createXwTransaction(String groupId) {
        String transactionId = UUID.randomUUID().toString();
        XwTransaction xwTransaction = new XwTransaction(groupId, transactionId);
        XW_TRANSACTION_MAP.put(groupId, xwTransaction);
        currentXwTransaction.set(xwTransaction);
        addTransactionCount();

        System.out.println("创建事务");

        return xwTransaction;
    }

    /**
     * 组装数据,添加事务到事务组,发送到tx-manager
     * @param xwTransaction
     * @param isEnd
     * @param transactionType
     * @return
     */
    public static XwTransaction addXwTransaction(XwTransaction xwTransaction, Boolean isEnd, TransactionType transactionType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", xwTransaction.getGroupId());
        jsonObject.put("transactionId", xwTransaction.getTransactionId());
        jsonObject.put("transactionType", transactionType);
        jsonObject.put("command", "add");
        jsonObject.put("isEnd", isEnd);
        jsonObject.put("transactionCount", XwTransactionManager.getTransactionCount());
        nettyClient.send(jsonObject);
        System.out.println("添加事务");
        return xwTransaction;
    }

    public static XwTransaction getXwTransaction(String groupId) {
        return XW_TRANSACTION_MAP.get(groupId);
    }

    public static XwTransaction getCurrentXwTransaction() {
        return currentXwTransaction.get();
    }
    public static String getCurrentGroupId() {
        return currentGroupId.get();
    }

    public static void setCurrentGroupId(String groupId) {
        currentGroupId.set(groupId);
    }

    public static Integer getTransactionCount() {
        return transactionCount.get();
    }

    public static void setTransactionCount(int i) {
        transactionCount.set(i);
    }

    public static Integer addTransactionCount() {
        int i = (transactionCount.get() == null ? 0 : transactionCount.get()) + 1;
        transactionCount.set(i);
        return i;
    }
}
