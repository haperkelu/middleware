/** 
* @Title: TransferDataAdapter.java
* @Package com.pieli.middleware.data.transfer
* @Description: TODO
* @author Pie.Li
* @date 2013-3-31 下午6:10:33
* @version V1.0 
*/
package com.pieli.middleware.transfer;

import java.nio.ByteBuffer;

/**
 * 数据传输上线文
 * @ClassName: TransferDataAdapter
 * @Description: TODO
 * @date 2013-3-31 下午6:10:33
 * 
 */
public class TransferDataContext {

	/**默认算法实现**/
	private ByteBufferInflater inflater = new DoubleByteBufferInflaterImp();
	
	/**
	 * 
	* @Title: inflateByteBuffer
	* @Description: TODO
	* @param @param buffer
	* @param @return
	* @return ByteBuffer
	* @throws
	 */
	public ByteBuffer inflateByteBuffer(ByteBuffer buffer){		
		return this.inflater.inflateByteBuffer(buffer);
	}
	
}
