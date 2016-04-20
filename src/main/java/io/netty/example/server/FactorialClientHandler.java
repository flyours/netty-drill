/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.message.MessageInterface;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Handler for a client-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler to avoid a race condition.
 */
public class FactorialClientHandler extends SimpleChannelInboundHandler<MessageInterface> {
	
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(FactorialClientHandler.class);
	private Set<Channel> oneShotAllChannels=new HashSet<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final MessageInterface msg) {
        logger.info("FactorialClientHandler.channelRead0 enter msg="+msg);
        //relay to webclient
        Channel ch= MessageBroker.getInstance().getChannel(msg.getId());
        ch.write(msg.getMsg());
        oneShotAllChannels.add(ch);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	for(Channel ch : oneShotAllChannels)
    	{
    		ch.flush();
    	}
    	oneShotAllChannels.clear();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	 logger.warn("JEFF",new Throwable("FactorialClientHandler.channelInactive enter" ));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
