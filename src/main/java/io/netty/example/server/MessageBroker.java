package io.netty.example.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.factorialserver.FactorialServer;
import io.netty.example.message.MessageInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


class MessageBroker {
    private static int POOL_SIZE = 16;
    private ExecutorService ex = Executors.newFixedThreadPool(POOL_SIZE);
    private BlockingQueue<MessageInterface> queue = new LinkedBlockingQueue<>();
    private Map<Channel, Integer> channel2IdMap = new HashMap<>();
    private Map<Integer, Channel> id2ChannelMap = new HashMap<>();

    private static int id = 1;
    private static final String HOST = System.getProperty("host", "127.0.0.1");

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();

    private static MessageBroker instance = new MessageBroker();

    private MessageBroker() {

    }

    static MessageBroker getInstance() {
        return instance;
    }

    void addQueue(MessageInterface msg) {
        queue.offer(msg);
    }

    private synchronized int addMapping(MessageInterface msg) {
        if (!channel2IdMap.containsKey(msg.getChannel())) {
            int tmp = id++;
            msg.setId(tmp);
            channel2IdMap.put(msg.getChannel(), tmp);
            id2ChannelMap.put(tmp, msg.getChannel());
        } else {
            //update id
            msg.setId(channel2IdMap.get(msg.getChannel()));
        }
        return channel2IdMap.get(msg.getChannel());
    }

    synchronized Channel getChannel(int id) {
        return id2ChannelMap.get(id);
    }

    synchronized void removeMapping(Channel channel) {
        id2ChannelMap.remove(channel2IdMap.get(channel));
        channel2IdMap.remove(channel);
    }

    void start() {
        // Configure the client.

        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new FactorialClientInitializer(null));

        for (int i = 0; i < POOL_SIZE; i++) {
            ex.execute(new BrokerRunner());
        }
    }

    private class BrokerRunner implements Runnable {

        @Override
        public void run() {
            ChannelFuture f = null;
            try {
                // Start the client.
                f = b.connect(HOST, FactorialServer.PORT).sync();
                Channel ch = f.channel();
                int num = 0;
                while (!Thread.interrupted()) {
                    MessageInterface msg = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (msg == null) {
                        ch.flush();
                        num = 0;
                    } else {
                        addMapping(msg);
                        ch.write(msg);
                        num++;
                    }
                }
            } catch (InterruptedException e) {
                WebServerHandler.logger.warn(e.getMessage());
            }
            WebServerHandler.logger.warn("BrokerRunner end!");
        }

    }
}
