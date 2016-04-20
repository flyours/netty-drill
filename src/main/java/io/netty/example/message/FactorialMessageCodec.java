package io.netty.example.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

public class FactorialMessageCodec extends ByteToMessageCodec<MessageInterface> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageInterface msg,
			ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		 // Write a message.
        out.writeByte((byte) 'F'); // magic number
        out.writeInt(msg.getId());
        
        BigInteger v=msg.getMsg();
        byte[] data = v.toByteArray();
        int dataLength = data.length;
        out.writeInt(dataLength);  // data length
        out.writeBytes(data);      // data
		
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
	    // Wait until the length prefix is available.
        if (in.readableBytes() < 9) {
            return;
        }

        in.markReaderIndex();

        // Check the magic number.
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F') {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }
        
        int id = in.readInt();

        // Wait until the whole data is available.
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // Convert the received data into a new BigInteger.
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);
        
        BigInteger data=new BigInteger(decoded);
        
        MessageInterface msg=new InternalMsg(null,id,data);

        out.add(msg);
		
	}

}
