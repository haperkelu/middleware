/** 
* @Title: ByteBufferInflater.java
* @Package com.pieli.middleware.transfer
* @Description: TODO
* @author Pie.Li
* @date 2013-4-1 上午10:53:28
* @version V1.0 
*/
package com.pieli.middleware.transfer;

import java.nio.ByteBuffer;

/**
 * @ClassName: ByteBufferInflater
 * @Description: TODO
 * @date 2013-4-1 上午10:53:28
 * 
 */
public interface ByteBufferInflater {
	
	public ByteBuffer inflateByteBuffer(ByteBuffer buffer);

}
