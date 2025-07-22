package org.terrarium.core.client.network;

import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.terrarium.core.game.network.CommunicationException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientCommunicator {
    private static final Logger LOGGER = LogManager.getLogger(ClientCommunicator.class);

    private ChannelFuture channelFuture;
    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    private volatile byte[] received;


    public ClientCommunicator() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addFirst(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<byte[]>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) {
                                received = bytes;
                            }
                        });
                        ch.pipeline().addLast(new ByteArrayEncoder());
                    }

                });
    }

    public void connect(Server server) throws CommunicationException {
        try {
            channelFuture = bootstrap.connect(server.getAddress()).sync();
        } catch (Exception e) {
            close();
            throw new CommunicationException(e);
        }
    }

    public void close() {
        disconnect();
        group.shutdownGracefully();
    }

    public void disconnect() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }

    public void send(Object message) {
        channelFuture.channel().writeAndFlush(message);
    }

    public byte[] receive() {
        while (received == null) {
            Thread.onSpinWait();
        }
        return received;
    }
}
