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
package io.netty.example.factorialserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.message.InternalMsg;
import io.netty.example.message.MessageInterface;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.math.BigInteger;

/**
 * Handler for a server-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler  to avoid a race condition.
 */
public class FactorialServerHandler extends SimpleChannelInboundHandler<MessageInterface> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(FactorialServerHandler.class);
    int num=0;
    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageInterface msg) throws Exception {
    	logger.info("FactorialServerHandler.channelRead0 enter msg="+msg );
        // Calculate the cumulative factorial and send it to the client.
    	BigInteger factorial = getFactorial(msg.getMsg());
        logger.info("FactorialServerHandler.channelRead0 result factorial="+factorial );
        ctx.write(new InternalMsg(null,msg.getId(),factorial));
        num++;
        
        if(num>50)
        {
        	ctx.flush();
        	num=0;
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    private BigInteger getFactorial(BigInteger max)
    {
    	if(max.equals(BigInteger.valueOf(0)))
    		return max;
    	
    	BigInteger tmp = new BigInteger("1");
    	BigInteger i = new BigInteger("1");
    	while(i.compareTo(max)<=0)
    	{
    		tmp=tmp.multiply(i);
    		i=i.add(new BigInteger("1"));
    	}
    	return tmp;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	 logger.warn("JEFF",new Throwable("FactorialServerHandler.channelInactive enter" ));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
