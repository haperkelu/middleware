/** 
 * @Title: MQServer.java
 * @Package com.pieli.middleware
 * @Description: TODO
 * @author Pie.Li
 * @date 2013-3-22 下午10:34:58
 * @version V1.0 
 */
package com.pieli.middleware.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import junit.framework.Assert;

import com.pieli.middleware.data.GlobalData;
import com.pieli.middleware.serialize.SerializerProvider;
import com.pieli.middleware.test.BizData;
import com.pieli.middleware.transfer.TransferDataContext;
import com.pieli.middleware.transfer.TransferStandardData;
import com.pieli.middleware.utils.LoggerUtils;

/**
 * @ClassName: MQServer
 * @Description: TODO
 * @date 2013-3-22 下午10:34:58
 * 
 */
public class MQServer {

	/** 默认byte大小 **/
	private final static int _defaultByteSize = 256;

	/**
	 * 
	 */
	private static Selector _selector;
	static {
		// open selector
		try {
			_selector = Selector.open();
		} catch (IOException e) {
		}
		// add jvm hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (_selector != null) {
					if (_selector.keys() != null) {
						for (SelectionKey k : _selector.keys()) {
							k.cancel();
							try {
								k.channel().close();
							} catch (IOException e) {
							}
						}
					}
					try {
						_selector.close();
					} catch (IOException e) {
					}
				}
			}
		}));
	}

	/**
	 * @throws Exception
	 * 
	 * @Title: openServer
	 * @Description: TODO
	 * @param @param hostName
	 * @param @param port
	 * @return void
	 * @throws
	 */
	public static void openServer(String hostName, int port) throws Exception {

		if (_selector == null) {
			throw new RuntimeException("selector should not be null");
		}
		Assert.assertTrue(port > 0);
		try {
			ServerSocketChannel server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));
			server.register(_selector, SelectionKey.OP_ACCEPT);
			ByteBuffer buffer = ByteBuffer.allocate(_defaultByteSize);
			while (true) {
				_selector.select();
				Iterator<SelectionKey> keyIterator = _selector.selectedKeys()
						.iterator();
				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					keyIterator.remove();
					// 接收
					if (key.isAcceptable()) {
						SocketChannel socket = ((ServerSocketChannel) key
								.channel()).accept().socket().getChannel();
						socket.configureBlocking(false);
						socket.register(_selector, SelectionKey.OP_READ
								| SelectionKey.OP_WRITE);
					}

					// 读取
					if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						buffer.clear();
						SerializerProvider provider = (SerializerProvider) GlobalData.get("SerializerProvider");
						if (channel.read(buffer) < _defaultByteSize) {
							LoggerUtils.logger.finest("raw data:" + new String(buffer.array()));
							TransferStandardData bizData = (TransferStandardData) provider.deserializedRead(TransferStandardData.class, buffer.array());
							LoggerUtils.logger.finest("real data:" + ((BizData)bizData.getBizData()).getF2());
						} else {
							TransferDataContext context = (TransferDataContext) GlobalData.get("TransferDataContext");
							LoggerUtils.logger.finest("Big Data Read");
							while (true) {

								buffer = context.inflateByteBuffer(buffer);
								LoggerUtils.logger.finest("new raw data:" + new String(buffer.array()));
								int size = buffer.capacity() / 2;
								int read = channel.read(buffer);
								LoggerUtils.logger.finest("new raw data:" + new String(buffer.array()));
								LoggerUtils.logger.info("read buffer count:" + read);
								if (read < size) {
									break;
								}
								
							}
							LoggerUtils.logger.info("data capacity:" + buffer.capacity());
							LoggerUtils.logger.finest("raw data:" + new String(buffer.array()));
							TransferStandardData bizData = (TransferStandardData) provider.deserializedRead(TransferStandardData.class, buffer.array());
							LoggerUtils.logger.info("real real data:" + ((BizData)bizData.getBizData()).getF2());
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
