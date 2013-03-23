/** 
* @Title: StorageProvider.java
* @Package com.pieli.middleware.store
* @Description: TODO
* @author Pie.Li
* @date 2013-3-22 下午10:58:33
* @version V1.0 
*/
package com.pieli.middleware.store;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import junit.framework.Assert;
import com.pieli.middleware.utils.CommonUtils;
import com.pieli.middleware.utils.LoggerUtils;

/**
 * @ClassName: StorageProvider
 * @Description: TODO
 * @date 2013-3-22 下午10:58:33
 * 
 */
public class StorageProvider {
		
	private static final Map<SegFile, FileChannelInfo> _readFileChannelMap = new HashMap<SegFile, FileChannelInfo>();
	private static final int _bulkSize = 512;
	private static final String _storePathPrefix = "/store/message/";	
	private static final int _readFileChannelOverDue = 2000; //milli seconds
	/**
	 * 
	* <p>Title: </p>
	* <p>Description: </p>
	 */
	public StorageProvider(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@SuppressWarnings("resource")
			@Override
			public void run() {
				Iterator<Entry<SegFile, FileChannelInfo>> iter = _readFileChannelMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<SegFile, FileChannelInfo> entry = (Map.Entry<SegFile, FileChannelInfo>) iter.next();
					SegFile key = entry.getKey();
					FileChannelInfo val = entry.getValue();
					if(val.getUpdateTime() != null 
							&& !CommonUtils.beforeNowTime(val.getUpdateTime(), _readFileChannelOverDue)){
						try {
							FileChannel channel = new RandomAccessFile(generateFilePath(key), "r").getChannel();
							FileChannelInfo info = new FileChannelInfo();
							info.setChannel(channel);
							info.setUpdateTime(Calendar.getInstance().getTime());
							_readFileChannelMap.put(key, info);
							if(val.getChannel().isOpen()){
								val.getChannel().close();
							}
						} catch (Exception e) {
						}
					}
				}
			}}, 1000, 1000);
	}
	
	/**
	 * @throws IOException 
	 * @throws FileNotFoundException 
	* @Title: writeMessageData
	* @Description: TODO
	* @param @param topic
	* @param @param segmentId
	* @param @param input
	* @return void
	* @throws
	 */
	@SuppressWarnings("resource")
	public void writeMessageData(String topic, int segmentId, byte[] input) throws IOException{
		
		if(topic == null || topic.trim().equalsIgnoreCase("")){
			return;
		}
		if(input == null || input.length < _bulkSize || input.length % _bulkSize != 0){
			return;
		}
		File file = new File(generateFilePath(new SegFile(topic, segmentId)));
		if(!file.exists()){
			if(file.getParentFile().mkdirs()){
				file.createNewFile();
			}
		}
		
		FileChannel channel = null;
		try {
			channel = new FileOutputStream(file, true).getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate(_bulkSize);
			int offset = 0;
			while(offset != input.length){
				byteBuffer.clear();
				byteBuffer.put(input, offset, _bulkSize);
				offset += _bulkSize;
				byteBuffer.flip();
				channel.write(byteBuffer);
			}		
		} catch (Exception e) {
		} finally {
			if(channel != null){
				channel.close();
			}
		}
		
	}
	
	/**
	 * 单机读取消息文件
	* @Title: readMessageData
	* @Description: TODO
	* @param @param topic
	* @param @param segmentId
	* @param @return
	* @param @throws IOException
	* @return String
	* @throws
	 */
	@SuppressWarnings({ "resource"})
	public List<byte[]> readMessageData(String topic, int segmentId, long offset) throws IOException {	
		
		if(topic == null || topic.trim().equalsIgnoreCase("")){
			return null;
		}
		Assert.assertTrue(offset % _bulkSize ==0);
		List<byte[]> result = new ArrayList<byte[]>();
		SegFile key = new SegFile(topic, segmentId);
		
		FileChannelInfo channelInfo = null;
		if(_readFileChannelMap.get(key) != null){
			channelInfo = _readFileChannelMap.get(key);			
		} else {			
			 FileChannel channel = new RandomAccessFile(generateFilePath(key), "r").getChannel();
			 channelInfo = new FileChannelInfo();
			 channelInfo.setChannel(channel);
			 channelInfo.setUpdateTime(Calendar.getInstance().getTime());
			 _readFileChannelMap.put(key, channelInfo);
		}
		
		Assert.assertTrue(offset <= channelInfo.getChannel().size());
		if(channelInfo.getChannel().isOpen()){
			MappedByteBuffer mbbi = channelInfo.getChannel().map(READ_ONLY, 0 + offset, channelInfo.getChannel().size() - offset);
			while (mbbi.hasRemaining()) {
				byte[] tempByte = new byte[_bulkSize];
				if (mbbi.remaining() < _bulkSize) {
					offset += mbbi.remaining();
					continue;
				}
				mbbi.get(tempByte);
				offset += _bulkSize;
				result.add(tempByte);
			}
		}
		return result;
	}
	
	/**
	 * 
	* @Title: getSegFileSize
	* @Description: TODO
	* @param @param topic
	* @param @param segmentId
	* @param @return
	* @return long
	* @throws
	 */
	public long getSegFileSize(String topic , int segmentId){
		
		SegFile key = new SegFile(topic, segmentId);
		
		FileChannelInfo channelInfo = null;
		if(_readFileChannelMap.get(key) != null){
			channelInfo = _readFileChannelMap.get(key);
			try {
				return channelInfo.getChannel().size();
			} catch (IOException e) {
				LoggerUtils.logExceptionDetail(e.toString(), e);
			}
		}
		return 0;
	}
	
	/**
	 * 
	* @Title: generateFilePath
	* @Description: TODO
	* @param @param seg
	* @param @return
	* @return String
	* @throws
	 */
	private String generateFilePath(SegFile seg){
		return _storePathPrefix + seg.getTopic().hashCode() + "_" + seg.getSegmentId();
	}
	
	/**
	 * 
	* @ClassName: SegFile
	* @Description: TODO
	* @date 2013-3-22 下午11:03:59
	*
	 */
	static class SegFile{
		private String topic;
		private int segmentId;
		
		SegFile(String topic, int segmentId){
			this.topic = topic;
			this.segmentId = segmentId;
		}
		
		/**
		 * @return the topic
		 */
		public String getTopic() {
			return topic;
		}

		/**
		 * @param topic the topic to set
		 */
		public void setTopic(String topic) {
			this.topic = topic;
		}



		/**
		 * @return the segmentId
		 */
		public int getSegmentId() {
			return segmentId;
		}

		/**
		 * @param segmentId the segmentId to set
		 */
		public void setSegmentId(int segmentId) {
			this.segmentId = segmentId;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + segmentId;
			result = prime * result + ((topic == null) ? 0 : topic.hashCode());
			return result;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SegFile other = (SegFile) obj;
			if (segmentId != other.segmentId)
				return false;
			if (topic == null) {
				if (other.topic != null)
					return false;
			} else if (!topic.equals(other.topic))
				return false;
			return true;
		}
		
	}
	static class FileChannelInfo{
		
		private FileChannel channel;
		private Date updateTime;
		/**
		 * @return the channel
		 */
		public FileChannel getChannel() {
			return channel;
		}
		/**
		 * @param channel the channel to set
		 */
		public void setChannel(FileChannel channel) {
			this.channel = channel;
		}
		/**
		 * @return the updateTime
		 */
		public Date getUpdateTime() {
			return updateTime;
		}
		/**
		 * @param updateTime the updateTime to set
		 */
		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}
		
	}
	
}
