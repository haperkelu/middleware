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

import com.pieli.middleware.AsynchronousWriterQueue;
import com.pieli.middleware.store.StorageProvider;

/**
 * @ClassName: GlobalData
 * @Description: TODO
 * @date 2013-3-22 下午10:59:14
 * 
 */
public class GlobalData {

	private static final Map<String, Object> _map = new HashMap<String, Object>();
	static{
		_map.put("StorageProvider", new StorageProvider());
		_map.put("AsynchronousWriterQueue", new AsynchronousWriterQueue());
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
	public static Object get(String key){
		return _map.get(key);
	}
	
}
