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
package io.netty.example.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.math.BigInteger;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class WebClientHandler extends SimpleChannelInboundHandler<BigInteger>  {

	static final InternalLogger logger = InternalLoggerFactory.getInstance(WebClientHandler.class);
	private int receivedNum=0;


    @Override
    public void channelRead0(ChannelHandlerContext ctx, BigInteger msg) {
    	logger.info("WebClientHandler.channelRead0 enter, msg="+msg);
    	receivedNum++;
    	if(receivedNum>=WebClientRunner.NUM)
    	{
    		WebClientScheduler.latch.countDown();
    	}
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//    	 logger.warn("JEFF",new Throwable("WebClientHandler.channelInactive enter" ));
    }

    


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
