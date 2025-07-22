package org.terrarium.core.client.network;

import io.netty.channel.SimpleChannelInboundHandler;
import org.terrarium.core.game.network.CommunicationException;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientReceiver extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LogManager.getLogger(ClientReceiver.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error(new CommunicationException(cause));
        ctx.close();
    }



}
