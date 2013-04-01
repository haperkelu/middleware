/** 
 * @Title: GlobalData.java
 * @Package com.pieli.middleware.data
 * @Description: TODO
 * @author Pie.Li
 * @date 2013-3-22 下午10:59:14
 * @version V1.0 
 */
package com.pieli.middleware.data;

import java.util.HashMap;
import java.util.Map;

import com.pieli.middleware.core.AsynchronousWriterQueue;
import com.pieli.middleware.serialize.SerializerProvider;
import com.pieli.middleware.store.StorageProvider;
import com.pieli.middleware.transfer.TransferDataContext;

/**
 * @ClassName: GlobalData
 * @Description: TODO
 * @date 2013-3-22 下午10:59:14
 * 
 */
public class GlobalData {

	/** 私有全局Map**/
	private static final Map<String, Object> _map = new HashMap<String, Object>();
	
	/** BootStrap Action**/
	static {
		_map.put("StorageProvider", new StorageProvider());
		_map.put("AsynchronousWriterQueue", new AsynchronousWriterQueue());
		_map.put("SerializerProvider", new SerializerProvider());
		_map.put("TransferDataContext", new TransferDataContext());
	}

	/**
	 * 
	 * @Title: get
	 * @Description: TODO
	 * @param @param key
	 * @param @return
	 * @return Object
	 * @throws
	 */
	public static Object get(String key) {
		return _map.get(key);
	}

}
