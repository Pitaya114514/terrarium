package com.pitaya.terrarium.server.network;

import com.pitaya.terrarium.game.network.CommunicationException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;

public class ServerCommunicator {
    private static final Logger LOGGER = LogManager.getLogger(ServerCommunicator.class);
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port;
    private final InetAddress address;

    public ServerCommunicator(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public void load() throws CommunicationException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(address, port).sync();
            LOGGER.info("The server has started, IP={}, port={}", address, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new CommunicationException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
