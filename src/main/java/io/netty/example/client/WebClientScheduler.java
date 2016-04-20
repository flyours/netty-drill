package io.netty.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WebClientRunner implements Runnable
{
	private CountDownLatch latch;
	private Channel ch;
	private Bootstrap bs;
	static int NUM = 100;
	

	public WebClientRunner(Bootstrap b) {
		// TODO Auto-generated constructor stub
		this.latch=WebClientScheduler.latch;
		this.bs=b;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			ChannelFuture f = bs.connect(WebClient.HOST, WebClient.PORT).sync();
			ch=f.channel();
			for(int i=0;i<NUM;i++)
	    	{
	    		ch.write(BigInteger.valueOf(i+1000));
	    		//ch.flush();
	    	}
	        ch.flush();
	        latch.await();
//	        WebClientHandler.logger.info("JEFF",new Throwable("WebClientRunner Latch barrier passed"));
		} catch (InterruptedException ex) {
			// Acceptable way to exit
			ex.printStackTrace();
//			WebClientHandler.logger.info("JEFF",new Throwable("WebClientRunner exception hit!"));
		}
		finally{
			if(ch!=null)
				ch.close();
//			WebClientHandler.logger.info("JEFF",new Throwable("WebClientRunner over"));
		}
	}
	
}

public class WebClientScheduler {
	static int SIZE = 10;
	public static CountDownLatch latch;

	public static void start(Bootstrap b) throws Exception
	{
		latch = new CountDownLatch(SIZE);
		
		WebClientHandler.logger.info("JEFF",new Throwable("WebClientScheduler start , SIZE="+SIZE)); 
		ExecutorService exec = Executors.newFixedThreadPool(SIZE);
		long start=System.currentTimeMillis();
		for (int i = 0; i < SIZE; i++)
		{
			exec.execute(new WebClientRunner(b));
		}
		latch.await();
		long end=System.currentTimeMillis();
		double delta=(end-start)/1000.0;
		String report=String.format("client Channel SIZE=%d, every Channel has %d tasks; time delta=%f seconds, ratio=%f",
				SIZE,WebClientRunner.NUM,delta,(SIZE*WebClientRunner.NUM)/delta);
		
		WebClientHandler.logger.warn("WebClientScheduler.start "+report); 
		exec.shutdown(); // Quit when all tasks complete
	}
	
}
