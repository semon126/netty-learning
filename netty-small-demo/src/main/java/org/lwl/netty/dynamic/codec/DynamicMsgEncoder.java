package org.lwl.netty.dynamic.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwl.netty.dynamic.message.DynamicMessage;
import org.lwl.netty.message.Header;
import org.lwl.netty.message.ProtocolMessage;
import org.lwl.netty.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author thinking_fioa
 * @createTime 2018/4/26
 * @description 编码器
 */


public class DynamicMsgEncoder extends MessageToByteEncoder<DynamicMessage> {
    private static final Logger LOGGER = LogManager.getLogger(DynamicMsgEncoder.class);

    private long msgNum = 1;

    public DynamicMsgEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DynamicMessage protocolMessage, ByteBuf outByteBuf) throws Exception {
        if(null == protocolMessage || null == protocolMessage.getBody()) {
            LOGGER.error("protocolMessage is null. refuse encode");

            return;
        }
        try {
            // 填写头协议
            fillInHeader(protocolMessage);
            // TODO 解码

            LOGGER.info("--> encode msgType:{}, msLen: {}", protocolMessage.getHeader().getMsgType(), outByteBuf.writerIndex());
        } catch(Throwable cause) {
            LOGGER.error("Encode error.", cause);
        }

    }

    private void fillInHeader(ProtocolMessage protocolMessage) {
        Header header = protocolMessage.getHeader();
        header.setMsgNum(msgNum++);
        header.setMsgType(protocolMessage.getBody().msgType());
        header.setMsgTime(CommonUtil.nowTime());

        // 下面的值，随机填。主要目的是证明协议支持多种类型格式
        header.setFlag((short)2);
        header.setOneByte((byte)3);

        Map<String, Object> attachment = new HashMap<>();
        attachment.put("name", "thinking_fioa");
        attachment.put("age", 18);

        header.getAttachment().putAll(attachment);
    }
}
