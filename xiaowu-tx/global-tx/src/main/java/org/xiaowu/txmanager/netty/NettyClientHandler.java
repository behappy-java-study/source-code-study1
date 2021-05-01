package org.xiaowu.txmanager.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.xiaowu.txmanager.transactional.XwTransaction;
import org.xiaowu.txmanager.transactional.XwTransactionManager;
import org.xiaowu.txmanager.transactional.TransactionType;

// BV1r5411A7hZ 33 8分钟
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收数据:" + msg.toString());
        JSONObject jsonObject = JSON.parseObject((String) msg);

        String groupId = jsonObject.getString("groupId");
        String command = jsonObject.getString("command");

        System.out.println("接收command:" + command);
        // 1. 对事务进行操作->根据groupId获取本地的分支事务
        XwTransaction xwTransaction = XwTransactionManager.getXwTransaction(groupId);
        if (command.equals("rollback")) {
            xwTransaction.setTransactionType(TransactionType.rollback);
        } else if (command.equals("commit")) {
            xwTransaction.setTransactionType(TransactionType.commit);
        }
        // 2. 唤醒
        xwTransaction.getTask().signalTask();
    }

    public synchronized Object call(JSONObject data) throws Exception {
        context.writeAndFlush(data.toJSONString());
        return null;
    }
}
