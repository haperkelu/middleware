/** 
 * @Title: WriterQueu.java
 * @Package com.pieli.middleware
 * @Description: TODO
 * @author Pie.Li
 * @date 2013-3-23 下午12:07:22
 * @version V1.0 
 */
package com.pieli.middleware.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import com.pieli.middleware.data.GlobalData;
import com.pieli.middleware.data.Message;
import com.pieli.middleware.data.MessageSerializer;
import com.pieli.middleware.store.StorageProvider;
import com.pieli.middleware.utils.CommonUtils;
import com.pieli.middleware.utils.LoggerUtils;

/**
 * 异步写消息队列
 * @ClassName: WriterQueue
 * @Description: TODO
 * @date 2013-3-23 下午12:07:22
 * 
 */
public class AsynchronousWriterQueue {

	private Message[] _items;
	private volatile int _pointer;
	private final ReentrantLock _addLock = new ReentrantLock();
	private final static int _threshHold = 500;
	private static int _capacity = 2000;
	private static final int _bulkSize = 512;
	private Date _flushTime;
	
	public AsynchronousWriterQueue() {
		_items = new Message[_capacity];
		_pointer = 0;
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				
				//第一次flush
				if(_flushTime == null && _pointer != 0){
					_flushTime = new Date();
					bulkFlushData();
					return;
				} 
				
				if(_pointer >= _threshHold
						|| (_pointer != 0 
						&& _flushTime != null 
						&& CommonUtils.beforeNowTime(_flushTime, 500))){  //间隔500毫秒
					bulkFlushData();
				}
				
			}			
		}, 100, 100);   // 100毫秒 
	}
	
	/**
	 * 
	* @Title: pushItem
	* @Description: TODO
	* @param @param item
	* @return void
	* @throws
	 */
	public void pushItem(Message item) {
		System.out.println("pubsh:" + item.getClass());
		_addLock.lock();
		try {
			_items[_pointer++] = item;
			if(_pointer * 2 > _capacity){
				_capacity = _capacity  * 2;
				_items = Arrays.copyOf(_items, _capacity);
			}
		} catch (Exception e) {
			LoggerUtils.logExceptionDetail(e.getMessage(), e);
		} finally {
			_addLock.unlock();
		}
	}
	
	/**
	 * 批量导入消息存储系统
	* @Title: bulkFlushData
	* @Description: TODO
	* @param 
	* @return void
	* @throws
	 */
	private void bulkFlushData(){
		
		Message[] temp = null;
		if(_pointer == 0){
			return;
		}
		_addLock.lock();
		try {
			System.out.println("buk:" + _pointer);
			temp = Arrays.copyOf(_items, _pointer);
			_pointer = 0;
		} catch (Exception e) {
			LoggerUtils.logExceptionDetail(e.getMessage(), e);
		} finally {
			_addLock.unlock();
		}
		if(temp != null){
			Arrays.sort(temp, new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					if(o1 == null || o2 == null){
						return 0;
					}
					if(o1.getTopic() == null || o2.getTopic() ==null){
						return 0;
					}
					if(o1.getTopic().equals(o2.getTopic())){
						return o1.getSegmentId() - o2.getSegmentId();
					}	
					return o1.getTopic().hashCode() - o2.getTopic().hashCode();
				}
			});
			List<Message> list = new ArrayList<Message>();
			Message previousItem = null;
			StorageProvider provider = (StorageProvider)GlobalData.get("StorageProvider");
			for(int i = 0; i < temp.length; i ++){

				if(i == temp.length - 1  //最后一个元素
						||( list.size() > 0 && ((previousItem = list.get(list.size() - 1)) != null)  //不是一个topic、seg组
								&& 
									(!previousItem.getTopic().equals(temp[i].getTopic()) 
									 || previousItem.getSegmentId() != temp[i].getSegmentId())
								)
						){
					try {
						
						boolean lastToProcess = false;
						
						//最后一个元素
						if(i == temp.length - 1){
							if(previousItem != null && previousItem.getTopic() != null && temp[i].getTopic() != null
									 && previousItem.getTopic().equals(temp[i].getTopic()) 
									 && previousItem.getSegmentId() == temp[i].getSegmentId()){
								list.add(temp[i]);
							} else {
								lastToProcess = true; //放到后面处理
							}
						}
						
						StringBuilder combine = new StringBuilder();
						if(list.size() > 0){
							for(Message item1: list){					
								try {
									String itemStr = MessageSerializer.serialize(item1);
									//保证读取消息的数据不是被分块的
									if(combine.toString().getBytes().length + itemStr.getBytes().length > _bulkSize){
										provider.writeMessageData(list.get(list.size() - 1).getTopic(), list.get(list.size() - 1).getSegmentId(), CommonUtils.makeUpByteArrayWithZero(combine.toString()));
										combine = new StringBuilder();
										continue;
									}else {
										combine.append(MessageSerializer.serialize(item1));
									}
									
								} catch (Exception e) {
									LoggerUtils.logExceptionDetail(e.getMessage(), e);
									e.printStackTrace();
								}
							}
							provider.writeMessageData(list.get(list.size() - 1).getTopic(), list.get(list.size() - 1).getSegmentId(), CommonUtils.makeUpByteArrayWithZero(combine.toString()));
							list.clear();
						}
						//最后一个单独处理
						if(lastToProcess){
							try {
								provider.writeMessageData(temp[i].getTopic(), temp[i].getSegmentId(), CommonUtils.makeUpByteArrayWithZero(MessageSerializer.serialize(temp[i])));
							} catch (Exception e) {
								LoggerUtils.logExceptionDetail(e.getMessage(), e);
								e.printStackTrace();
							}
						}
						
					} catch (IOException e) {
						LoggerUtils.logExceptionDetail(e.getMessage(), e);
						e.printStackTrace();
					}
				} else {
					list.add(temp[i]);
				}
				
			}
		}		
		
	}

}
