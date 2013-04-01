/** 
* @Title: SerializerProvider.java
* @Package com.pieli.middleware.serialize
* @Description: TODO
* @author Pie.Li
* @date 2013-3-31 上午10:39:27
* @version V1.0 
*/
package com.pieli.middleware.serialize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pieli.middleware.data.GlobalData;
import com.pieli.middleware.transfer.TransferDataContext;
import com.pieli.middleware.utils.LoggerUtils;

/**
 * @ClassName: SerializerProvider
 * @Description: TODO
 * @date 2013-3-31 上午10:39:27
 * 
 */
public class SerializerProvider {
	
	/**默认byte大小**/
	private final static int  _defaultByteSize = 256;
	
	/**序列化对象Map，线程安全**/
	private ThreadLocal<Kryo> _kryoMap = new ThreadLocal<Kryo>(){
		public Kryo initialValue(){
			return new Kryo();
		}
	};
	
	/**
	 * @throws IOException 
	 * 序列化写
	* @Title: serializedWriteBuffer
	* @Description: TODO
	* @param @param c
	* @param @param targetObj
	* @param @return
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException
	* @return ByteBuffer
	* @throws
	 */
	public ByteBuffer  serializedWriteBuffer(Class<?> c, Object targetObj) throws InstantiationException, IllegalAccessException, IOException{
				
		ByteBuffer buffer = ByteBuffer.allocate(_defaultByteSize);
		ByteBufferOutputStream outStream = new ByteBufferOutputStream(buffer);
		Output out = new Output(outStream);		
		_kryoMap.get().register(c);
		_kryoMap.get().writeObject(out, targetObj);
				
		LoggerUtils.logger.finest("outStream position:" + out.position());
		if(out.position() < _defaultByteSize - 1){
			out.flush();
			return buffer;
		} else {
			LoggerUtils.logger.finest("buffer not enough");
			
			while(true){			
				buffer = ByteBuffer.allocate(buffer.capacity() * 2);
				outStream = new ByteBufferOutputStream(buffer);
				out = new Output(outStream);	
				_kryoMap.get().writeObject(out, targetObj);
				if(out.position() < buffer.capacity() - 1){
					out.flush();
					break;
				}
			}
			LoggerUtils.logger.finest("raw data:" + new String(buffer.array()));

			return buffer;
		}				
	
	}
	
	/**
	 * 
	* @Title: serializedRead
	* @Description: TODO
	* @param @param c
	* @param @param bytes
	* @param @return
	* @return Object
	* @throws
	 */
	public <T> Object deserializedRead(Class<T> c, byte[] bytes) {
		
		Input input = new Input(new ByteArrayInputStream(bytes));
		Object obj =  _kryoMap.get().readObject(input, c);
		return obj;
		
	}
	
}
