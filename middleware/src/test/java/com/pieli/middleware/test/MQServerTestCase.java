/** 
* @Title: MQServerTestCase.java
* @Package com.pieli.middleware.test
* @Description: TODO
* @author Pie.Li
* @date 2013-3-24 下午9:21:38
* @version V1.0 
*/
package com.pieli.middleware.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.Test;

import com.pieli.middleware.core.MQServer;
import com.pieli.middleware.data.GlobalData;
import com.pieli.middleware.serialize.SerializerProvider;
import com.pieli.middleware.transfer.TransferStandardData;

/**
 * @ClassName: MQServerTestCase
 * @Description: TODO
 * @date 2013-3-24 下午9:21:38
 * 
 */
public class MQServerTestCase {

	/**
	 * @throws Exception 
	 * @Title: main
	 * @Description: TODO
	 * @param @param args
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		
		final int port = 8113;
		new Thread(new Runnable(){
			public void run() {
				try {
					MQServer.openServer("", port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}).start();
		
		Thread.currentThread().sleep(1000);
		InetSocketAddress addr = new InetSocketAddress(port); 
		SocketChannel   channel  = SocketChannel.open();   
		channel.configureBlocking(false);
		channel.connect(addr);                       
		if(channel.finishConnect()){
			
			while(true){
				Thread.currentThread().sleep(2000);
				
				SerializerProvider obj = (SerializerProvider)GlobalData.get("SerializerProvider");
				TransferStandardData w = new TransferStandardData();
				BizData data = new BizData();
				data.setF1(1);
				int count = 10;
				StringBuilder sb = new StringBuilder();
				sb.append("ggg");
				while(count -- != 0){
					sb.append("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
				}
				data.setF2(sb.toString());
				w.setBizData(data);
				
				ByteBuffer buffer = obj.serializedWriteBuffer(TransferStandardData.class, w);
				buffer.flip();
				channel.write(buffer);
				
			}
			
		}
		channel.close();

	}
	
	@Test
	public void fn() throws InstantiationException, IllegalAccessException, IOException{
		
		SerializerProvider obj = (SerializerProvider)GlobalData.get("SerializerProvider");
		BizData data = new BizData();
		data.setF1(1);
		ByteBuffer buffer = obj.serializedWriteBuffer(BizData.class, data);
		//buffer.flip();
		System.out.println(buffer.limit());
		System.out.println(buffer.position());
	}

}
