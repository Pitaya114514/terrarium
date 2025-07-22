package org.terrarium.core.server.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.terrarium.core.game.LocalTerrarium;
import org.terrarium.core.game.network.CommunicationException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerCommunicator {
    private static final Logger LOGGER = LogManager.getLogger(ServerCommunicator.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port;
    private final InetAddress address;
    private final HashMap<Channel, ArrayList<byte[]>> connections = new HashMap<>();

    public ServerCommunicator(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public void load() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addFirst(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<byte[]>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes) {
                                    connections.get(ctx.channel()).add(bytes);
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    LOGGER.info("Discover the connection: {}", ctx.name());
                                    connections.put(ctx.channel(), new ArrayList<>());
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) {
                                    LOGGER.info("{} disconnected", ctx.name());
                                    connections.remove(ctx.channel());
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    LOGGER.warn("There is an exception with the connection to {}", ctx.channel().remoteAddress(), cause);
                                }
                            });
                            ch.pipeline().addLast(new ByteArrayEncoder());
                        }
                    });
            ChannelFuture future = bootstrap.bind(address, port).sync();
            LOGGER.info("The server has started, IP={}, port={}", address, port);
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            LOGGER.error("Unable to load server: ", new CommunicationException(e));
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void tick(LocalTerrarium terrarium) {
        for (Map.Entry<Channel, ArrayList<byte[]>> entry : connections.entrySet()) {
            ArrayList<byte[]> messages = entry.getValue();
            Channel connection = entry.getKey();
            byte[] bytes = messages.get(messages.size() - 1);
            if (bytes.length == 1) {
                switch (bytes[0]) {
                    case -4 -> {
                        try {
                            connection.writeAndFlush(mapper.writeValueAsBytes(terrarium.getRegisteredEntities()));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
