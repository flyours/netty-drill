package io.netty.example.message;

import io.netty.channel.Channel;

import java.math.BigInteger;

public interface MessageInterface
{
	Channel getChannel();
	BigInteger getMsg();
	int getId();
	void setId(int id);
}
