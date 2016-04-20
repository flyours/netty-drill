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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.message.InternalMsg;
import io.netty.example.message.MessageInterface;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.math.BigInteger;


/**
 * Handler implementation for the echo server.
 */
@Sharable
public class WebServerHandler extends SimpleChannelInboundHandler<BigInteger> {
	static final InternalLogger logger = InternalLoggerFactory.getInstance(WebServerHandler.class);
	
    
//    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
//    	logger.info("JEFF",new Throwable("WebServerHandler.channelRead enter, buf="+ByteBufUtil.hexDump(buf)));
//        while(buf.readableBytes() >= 4) 
//        {
//        	int data=buf.readInt();
//        	MessageInterface message=new InternalMsg(ctx.channel(),0,new BigInteger(String.valueOf(data)));
//        	broker.addQueue(message);
//        }
//    }
	@Override
    public void channelRead0(ChannelHandlerContext ctx, BigInteger msg) {
    	logger.info("WebServerHandler.channelRead0 enter, msg="+msg);
    	MessageInterface message=new InternalMsg(ctx.channel(),0,msg);
    	MessageBroker.getInstance().addQueue(message);
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	 logger.warn("WebServerHandler.channelInactive enter");
    	 MessageBroker.getInstance().removeMapping(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
