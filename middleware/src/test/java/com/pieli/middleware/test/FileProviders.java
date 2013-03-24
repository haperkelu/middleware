/** 
 * @Title: FileProviders.java
 * @Package com.pieli.middleware
 * @Description: TODO
 * @author Pie.Li
 * @date 2013-3-22 下午3:09:51
 * @version V1.0 
 */
package com.pieli.middleware.test;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.pieli.middleware.AsynchronousWriterQueue;
import com.pieli.middleware.data.GlobalData;
import com.pieli.middleware.data.Message;
import com.pieli.middleware.data.ObjectMessage;
import com.pieli.middleware.store.StorageProvider;
import com.pieli.middleware.utils.CommonUtils;

/**
 * @ClassName: FileProviders
 * @Description: TODO
 * @date 2013-3-22 下午3:09:51
 * 
 */
public class FileProviders {

	private static final String _fileName = "e:/log4j1.log";

	public static void commonAccessFile() throws Exception {

		File file = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		try {
			file = new File(_fileName);
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);
			for (String line; (line = reader.readLine()) != null;) {
				// System.out.print(line);
			}
		} catch (Exception e) {
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}

	}

	public static void randomAccessFile() throws Exception {

		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(_fileName, "r");
			final byte[] buffer = new byte[1024];
			for (int bytesRead; (bytesRead = file.read(buffer)) != -1;) {

			}
			/**
			 * for ( String line; (line=file.readLine()) != null; ){
			 * //System.out.print(line); }
			 **/
		} catch (Exception e) {
		} finally {
			if (file != null) {
				file.close();
			}
		}

	}

	public static void channelAccessFile() throws Exception {

		FileChannel channel = new RandomAccessFile(_fileName, "r").getChannel();

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		for (int bytesRead; (bytesRead = channel.read(buffer)) != -1;) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				buffer.get();
			}
			buffer.clear();
		}
		channel.close();
	}

	public static void mappedAccessFile() throws Exception {

		final FileChannel channel = new RandomAccessFile(_fileName, "r")
				.getChannel();

		final MappedByteBuffer mbbi = channel.map(READ_ONLY, 0, channel.size());

		while (mbbi.hasRemaining()) {
			byte[] tempByte = new byte[1024];
			if (mbbi.remaining() < 1024) {
				mbbi.get(new byte[mbbi.remaining()]);
				continue;
			}
			mbbi.get(tempByte);
		}

		channel.close();

	}

	/**
	 * @throws Exception
	 * @Title: main
	 * @Description: TODO
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) throws Exception {
		
		final StorageProvider obj = (StorageProvider)GlobalData.get("StorageProvider");
		final String topic = "demo2";
		Thread t1 = new Thread(new Runnable(){
			public void run() {
				
				AsynchronousWriterQueue queue = (AsynchronousWriterQueue)GlobalData.get("AsynchronousWriterQueue");
				//while(true){
					try {
						try {
							Thread.currentThread().sleep(10);
						} catch (InterruptedException e) {
						}
						String result = "";
						ObjectMessage message = new ObjectMessage();
						message.setMessageId(5);
						message.setSentTime(CommonUtils.formatRightNow());
						message.setTopic(topic);
						message.setSegmentId(1);
						BizData data = new BizData();
						data.setF1(22);
						data.setF2("field 222");
						message.setBizData(data);
						queue.pushItem(message);
					} finally {}							
				//}				
			}			
		});
		//t1.start();
		ObjectMessage message = new ObjectMessage();
		message.setMessageId(5);
		message.setSentTime(CommonUtils.formatRightNow());
		message.setTopic(topic);
		message.setSegmentId(1);
		BizData data = new BizData();
		data.setF1(22);
		data.setF2("field 222");
		message.setBizData(data);
		obj.writeMessageData(topic, 1, CommonUtils.makeUpByteArrayWithZero(message.toString()));
	

		/**
		String result = "";
		for(int i = 0; i < 10; i ++){
			Message message = new ObjectMessage();
			message.setMessageId(i);
			message.setSentTime(CommonUtils.formatRightNow());
			if(result.getBytes().length + message.toString().getBytes().length > 512){
				obj.writeMessageData("demo2", 1, CommonUtils.makeUpByteArrayWithZero(result.toString()));
				result = message.toString();
			} else {
				result += message.toString();
			}
		}
		
		obj.writeMessageData("demo2", 1, CommonUtils.makeUpByteArrayWithZero(result.toString()));
		
		
		
		/**
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		System.out.println(sdf.format(new Date()));
		mappedAccessFile();

		/**
		 * FileOutputStream out = new FileOutputStream(new File(_fileName),
		 * true); BufferedOutputStream buff=new BufferedOutputStream(out); int
		 * count = 3000000; while(count -- != 0){
		 * buff.write("这事测试数据\r\n".getBytes()); } buff.close(); out.close();

		System.out.println(sdf.format(new Date()));
		**/
	}

}
