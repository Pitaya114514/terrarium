package com.pitaya.terrarium.client.network;

import com.pitaya.terrarium.client.Player;
import com.pitaya.terrarium.game.network.CommunicationException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientCommunicator {
    private static final Logger LOGGER = LogManager.getLogger(ClientCommunicator.class);

    public void connect(Server server, Player player) throws CommunicationException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(server.getAddress()).sync();
            future.channel().writeAndFlush("H E L L O < W O R L D");
        } catch (Exception e) {
            throw new CommunicationException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
