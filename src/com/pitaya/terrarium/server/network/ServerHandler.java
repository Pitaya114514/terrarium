package com.pitaya.terrarium.server.network;

import com.pitaya.terrarium.game.network.CommunicationException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(ServerHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("reg");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("Unreg");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("act");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Inact");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("read");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("readComplete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        System.out.println("event" + evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        System.out.println("Change");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error(new CommunicationException(cause));
        ctx.close();
    }
}
