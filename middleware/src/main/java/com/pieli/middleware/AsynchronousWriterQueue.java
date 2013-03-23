/** 
 * @Title: WriterQueu.java
 * @Package com.pieli.middleware
 * @Description: TODO
 * @author Pie.Li
 * @date 2013-3-23 下午12:07:22
 * @version V1.0 
 */
package com.pieli.middleware;

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
import com.pieli.middleware.store.StorageProvider;
import com.pieli.middleware.utils.CommonUtils;
import com.pieli.middleware.utils.LoggerUtils;

/**
 * @ClassName: WriterQueu
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
						&& CommonUtils.beforeNowTime(_flushTime, 1000 * 60))){  //间隔一分钟
					bulkFlushData();
				}
				
			}			
		}, 200, 200);   // 200毫秒 
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
	 * 
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
			temp = Arrays.copyOf(_items, _pointer);
			_pointer = 0;
		} catch (Exception e) {
			LoggerUtils.logExceptionDetail(e.getMessage(), e);
		} finally {
			_addLock.unlock();
		}
		if(temp != null){
			System.out.println("pub:" + temp.length);
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
				
				if(i == 0){list.add(temp[i]);}  //第一次访问
				if(i == temp.length - 1  //最后一个元素
						||( list.size() > 0 && ((previousItem = list.get(list.size() - 1)) != null)  //不是一个topic、seg组
								&& 
									(!previousItem.getTopic().equals(temp[i].getTopic()) 
									 || previousItem.getSegmentId() != temp[i].getSegmentId())
								)
						){
					try {
						//最后一个元素，则提前处理
						boolean lastToProcess = false;
						if(i == temp.length - 1){
							if(previousItem != null && previousItem.getTopic() != null && temp[i].getTopic() != null
									 && previousItem.getTopic().equals(temp[i].getTopic()) 
									 && previousItem.getSegmentId() == temp[i].getSegmentId()){
								list.add(temp[i]);
							} else {
								lastToProcess = true;
							}
						}
						StringBuilder combine = new StringBuilder();
						if(list.size() > 0){
							
							for(Message item1: list){					
								combine.append(item1.toString());
							}
							provider.writeMessageData(list.get(list.size() - 1).getTopic(), list.get(list.size() - 1).getSegmentId(), CommonUtils.makeUpByteArrayWithZero(combine.toString()));
							list.clear();
						}
						if(lastToProcess){
							provider.writeMessageData(temp[i].getTopic(), temp[i].getSegmentId(), CommonUtils.makeUpByteArrayWithZero(temp[i].toString()));
						}
						
					} catch (IOException e) {
						LoggerUtils.logExceptionDetail(e.getMessage(), e);
					}
					continue;
				} else {
					list.add(temp[i]);
				}
				
			}
		}		
		
	}

}
