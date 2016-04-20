package io.netty.example.message;

import io.netty.channel.Channel;

import java.math.BigInteger;

public class InternalMsg implements MessageInterface {
    private Channel channel;
    private BigInteger msg;
    private int id;

    public InternalMsg(Channel channel, int id, BigInteger msg) {
        this.channel = channel;
        this.msg = msg;
        this.id = id;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public BigInteger getMsg() {
        return msg;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[InternalMsg]: id=" + id + ",msg=" + msg;
    }
}
