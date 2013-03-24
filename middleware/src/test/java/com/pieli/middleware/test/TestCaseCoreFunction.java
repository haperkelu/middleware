package com.pieli.middleware.test;

import java.io.IOException;
import java.util.List;

import com.pieli.middleware.AsynchronousWriterQueue;
import com.pieli.middleware.data.GlobalData;
import com.pieli.middleware.data.ObjectMessage;
import com.pieli.middleware.store.StorageProvider;
import com.pieli.middleware.utils.CommonUtils;

public class TestCaseCoreFunction {

	
	static final StorageProvider obj = (StorageProvider)GlobalData.get("StorageProvider");
	static final AsynchronousWriterQueue queue = (AsynchronousWriterQueue)GlobalData.get("AsynchronousWriterQueue");
	static final String topic = "demo2";
	/**
	 * @throws Exception 
	 * @Title: main
	 * @Description: TODO
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) throws Exception {
		
		insert();
		
		final long[] arr = new long[1]; 
		Thread t2 = new Thread(new Runnable(){
			public void run() {
				while (true) {
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e1) {
					}
					List<byte[]> list = null;
					try {		
						long size = obj.getSegFileSize(topic, 1);
						if(size > arr[0]){
							list = obj.readMessageData(topic, 1, arr[0]);
						}						
						arr[0] += size;
					} catch (IOException e) {
					}
					int count = 0;
					if (list != null) {
						//System.out.println("size:" + list.size());
						for (byte[] item : list) {
							count += item.length;
							String[] arr = new String(item).split(";");
							if (arr != null) {
								for (String item1 : arr) {
									//System.out.println(item1);
									if(item1.contains("Body:")){
										String target = item1.substring(14);
										System.out.println("target:" + new String(CommonUtils.convertHexStrToBytes(target)));
									}								
								}
							}
						}
					}
				}
			}			
		});
		t2.start();
	}
	
	public static void insert() throws Exception{

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
		//obj.writeMessageData(topic, 1, CommonUtils.makeUpByteArrayWithZero(message.toString()));		
		
		ObjectMessage message1 = new ObjectMessage();
		message1.setMessageId(5);
		message1.setSentTime(CommonUtils.formatRightNow());
		message1.setTopic(topic);
		message1.setSegmentId(1);
		BizData data1 = new BizData();
		data1.setF1(22333);
		data1.setF2("field2 \"222");
		message1.setBizData(data1);
		queue.pushItem(message1);
		//obj.writeMessageData(topic, 1, CommonUtils.makeUpByteArrayWithZero(message1.toString()));
	}

}
